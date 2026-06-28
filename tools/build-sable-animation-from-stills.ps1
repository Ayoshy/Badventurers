param(
    [string] $SourcePath = "docs/art/hero-animations/sable/sable-animation-sheet-candidate-v1-source.png",
    [string] $OutputDir = "app/src/main/res-heroes/drawable-nodpi",
    [string] $ReviewDir = "docs/art/hero-animations/sable",
    [int] $Rows = 6,
    [int] $Columns = 6,
    [int] $OutputColumns = 5,
    [int] $FrameWidth = 128,
    [int] $FrameHeight = 128,
    [int] $TargetBodyHeight = 76,
    [int] $BaselineY = 118,
    [string[]] $States = @("idle", "walk", "fight", "hurt_dead", "celebrate", "loot_interact")
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

$source = @"
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

public static class SableStillAnimator
{
    private sealed class PixelData
    {
        public int Width;
        public int Height;
        public int Stride;
        public byte[] Bytes;

        public int Offset(int x, int y)
        {
            return (y * Stride) + (x * 4);
        }

        public static PixelData FromBitmap(Bitmap bitmap)
        {
            var rect = new Rectangle(0, 0, bitmap.Width, bitmap.Height);
            var data = bitmap.LockBits(rect, ImageLockMode.ReadOnly, PixelFormat.Format32bppArgb);
            try
            {
                var stride = Math.Abs(data.Stride);
                var bytes = new byte[stride * bitmap.Height];
                for (var y = 0; y < bitmap.Height; y++)
                {
                    Marshal.Copy(IntPtr.Add(data.Scan0, y * data.Stride), bytes, y * stride, stride);
                }
                return new PixelData { Width = bitmap.Width, Height = bitmap.Height, Stride = stride, Bytes = bytes };
            }
            finally
            {
                bitmap.UnlockBits(data);
            }
        }
    }

    private sealed class CellInfo
    {
        public int Row;
        public int Col;
        public Rectangle CellBounds;
        public Rectangle ForegroundBounds;
        public Rectangle BodyBounds;
        public double AnchorX;
        public double AnchorY;
        public int ForegroundPixels;
        public int BodyPixels;
        public int BodyHeight { get { return BodyBounds.Height; } }
    }

    private sealed class RenderMetric
    {
        public string State = "";
        public int Frame;
        public int MagentaPixels;
        public Rectangle Bounds;
        public Rectangle BodyBounds;
        public double BodyCenterX;
        public int BodyFootY;
        public double AnchorSourceX;
        public double AnchorSourceY;
        public int DestX;
        public int DestY;
    }

    public static void Run(
        string sourcePath,
        string outputDir,
        string reviewDir,
        int rows,
        int columns,
        int outputColumns,
        int frameWidth,
        int frameHeight,
        int targetBodyHeight,
        int baselineY,
        string[] states)
    {
        if (states.Length != rows)
        {
            throw new InvalidOperationException("State count must match sheet row count.");
        }
        if (!File.Exists(sourcePath))
        {
            throw new FileNotFoundException("Source sheet not found.", sourcePath);
        }
        if (outputColumns <= 0 || outputColumns > columns)
        {
            throw new InvalidOperationException("Output column count must be between 1 and the source column count.");
        }

        Directory.CreateDirectory(outputDir);
        Directory.CreateDirectory(reviewDir);

        using (var source = new Bitmap(sourcePath))
        {
            var sourcePixels = PixelData.FromBitmap(source);
            using (var sanitized = SanitizeSource(sourcePixels))
            {
                var cells = new CellInfo[rows, columns];
                var standingHeights = new List<int>();
                for (var row = 0; row < rows; row++)
                {
                    for (var col = 0; col < columns; col++)
                    {
                        var cell = AnalyzeCell(sourcePixels, row, col, rows, columns);
                        cells[row, col] = cell;
                        if (col < outputColumns && row != 3 && cell.BodyHeight >= 35)
                        {
                            standingHeights.Add(cell.BodyHeight);
                        }
                    }
                }

                if (standingHeights.Count == 0)
                {
                    throw new InvalidOperationException("Could not detect a standing Sable body height.");
                }

                var medianHeight = Median(standingHeights);
                var scale = targetBodyHeight / medianHeight;
                var metrics = new List<RenderMetric>();
                var stripPaths = new List<string>();

                for (var row = 0; row < rows; row++)
                {
                    var state = states[row];
                    var outPath = Path.Combine(outputDir, "hero_anim_sable_" + state + ".png");
                    RenderStateStrip(sanitized, cells, row, state, outputColumns, frameWidth, frameHeight, scale, baselineY, outPath, metrics);
                    stripPaths.Add(outPath);
                }

                var previewPath = Path.Combine(reviewDir, "sable-runtime-stills-preview.png");
                SavePreview(stripPaths.ToArray(), frameWidth, frameHeight, previewPath);

                var csvPath = Path.Combine(reviewDir, "sable-runtime-stills-metrics.csv");
                SaveMetrics(metrics, csvPath);

                Console.WriteLine("source=" + sourcePath);
                Console.WriteLine("globalScale=" + scale.ToString("0.000", CultureInfo.InvariantCulture) + " medianBodyHeight=" + medianHeight.ToString("0.0", CultureInfo.InvariantCulture) + " outputFrames=" + outputColumns);
                Console.WriteLine("preview=" + previewPath);
                Console.WriteLine("metrics=" + csvPath);
                Console.WriteLine("outputs=");
                foreach (var path in stripPaths)
                {
                    Console.WriteLine("  " + path);
                }
            }
        }
    }

    private static Bitmap SanitizeSource(PixelData source)
    {
        var output = new Bitmap(source.Width, source.Height, PixelFormat.Format32bppArgb);
        var bytes = new byte[source.Bytes.Length];
        for (var y = 0; y < source.Height; y++)
        {
            for (var x = 0; x < source.Width; x++)
            {
                var offset = source.Offset(x, y);
                var b = source.Bytes[offset + 0];
                var g = source.Bytes[offset + 1];
                var r = source.Bytes[offset + 2];
                var a = source.Bytes[offset + 3];
                if (IsChroma(r, g, b, a))
                {
                    bytes[offset + 0] = 0;
                    bytes[offset + 1] = 0;
                    bytes[offset + 2] = 0;
                    bytes[offset + 3] = 0;
                }
                else
                {
                    bytes[offset + 0] = b;
                    bytes[offset + 1] = g;
                    bytes[offset + 2] = r;
                    bytes[offset + 3] = a;
                }
            }
        }

        var rect = new Rectangle(0, 0, output.Width, output.Height);
        var data = output.LockBits(rect, ImageLockMode.WriteOnly, PixelFormat.Format32bppArgb);
        try
        {
            var stride = Math.Abs(data.Stride);
            for (var y = 0; y < output.Height; y++)
            {
                Marshal.Copy(bytes, y * source.Stride, IntPtr.Add(data.Scan0, y * data.Stride), stride);
            }
        }
        finally
        {
            output.UnlockBits(data);
        }
        return output;
    }

    private static CellInfo AnalyzeCell(PixelData source, int row, int col, int rows, int columns)
    {
        var x0 = (int)Math.Floor((double)source.Width * col / columns);
        var x1 = (int)Math.Floor((double)source.Width * (col + 1) / columns) - 1;
        var y0 = (int)Math.Floor((double)source.Height * row / rows);
        var y1 = (int)Math.Floor((double)source.Height * (row + 1) / rows) - 1;
        var cellBounds = Rectangle.FromLTRB(x0, y0, x1 + 1, y1 + 1);

        var fgMinX = int.MaxValue;
        var fgMinY = int.MaxValue;
        var fgMaxX = int.MinValue;
        var fgMaxY = int.MinValue;
        var bodyMinX = int.MaxValue;
        var bodyMinY = int.MaxValue;
        var bodyMaxX = int.MinValue;
        var bodyMaxY = int.MinValue;
        var foregroundPixels = 0;
        var bodyPixels = 0;

        for (var y = y0; y <= y1; y++)
        {
            for (var x = x0; x <= x1; x++)
            {
                var offset = source.Offset(x, y);
                var b = source.Bytes[offset + 0];
                var g = source.Bytes[offset + 1];
                var r = source.Bytes[offset + 2];
                var a = source.Bytes[offset + 3];
                if (IsChroma(r, g, b, a))
                {
                    continue;
                }

                foregroundPixels++;
                if (x < fgMinX) fgMinX = x;
                if (x > fgMaxX) fgMaxX = x;
                if (y < fgMinY) fgMinY = y;
                if (y > fgMaxY) fgMaxY = y;

                if (!IsBodyPixel(r, g, b, a))
                {
                    continue;
                }

                bodyPixels++;
                if (x < bodyMinX) bodyMinX = x;
                if (x > bodyMaxX) bodyMaxX = x;
                if (y < bodyMinY) bodyMinY = y;
                if (y > bodyMaxY) bodyMaxY = y;
            }
        }

        if (foregroundPixels == 0)
        {
            throw new InvalidOperationException("No foreground detected at row " + row + ", col " + col + ".");
        }
        if (bodyPixels == 0)
        {
            bodyMinX = fgMinX;
            bodyMaxX = fgMaxX;
            bodyMinY = fgMinY;
            bodyMaxY = fgMaxY;
        }

        var bodyBounds = Rectangle.FromLTRB(bodyMinX, bodyMinY, bodyMaxX + 1, bodyMaxY + 1);
        var anchorXs = new List<int>();
        var upperLimit = bodyBounds.Top + (int)Math.Round(bodyBounds.Height * 0.72);

        for (var y = bodyBounds.Top; y <= Math.Min(bodyBounds.Bottom - 1, upperLimit); y++)
        {
            for (var x = bodyBounds.Left; x <= bodyBounds.Right - 1; x++)
            {
                var offset = source.Offset(x, y);
                var b = source.Bytes[offset + 0];
                var g = source.Bytes[offset + 1];
                var r = source.Bytes[offset + 2];
                var a = source.Bytes[offset + 3];
                if (IsBodyPixel(r, g, b, a))
                {
                    anchorXs.Add(x - x0);
                }
            }
        }

        var anchorX = anchorXs.Count > 0 ? Median(anchorXs) : (bodyBounds.Left + (bodyBounds.Width / 2.0) - x0);
        var anchorY = bodyBounds.Bottom - 1 - y0;

        return new CellInfo
        {
            Row = row,
            Col = col,
            CellBounds = cellBounds,
            ForegroundBounds = Rectangle.FromLTRB(fgMinX, fgMinY, fgMaxX + 1, fgMaxY + 1),
            BodyBounds = bodyBounds,
            AnchorX = anchorX,
            AnchorY = anchorY,
            ForegroundPixels = foregroundPixels,
            BodyPixels = bodyPixels
        };
    }

    private static void RenderStateStrip(
        Bitmap sanitizedSource,
        CellInfo[,] cells,
        int row,
        string state,
        int columns,
        int frameWidth,
        int frameHeight,
        double scale,
        int baselineY,
        string outPath,
        List<RenderMetric> metrics)
    {
        var placements = new List<RenderMetric>();
        using (var strip = new Bitmap(frameWidth * columns, frameHeight, PixelFormat.Format32bppArgb))
        using (var g = Graphics.FromImage(strip))
        {
            g.Clear(Color.Transparent);
            g.CompositingMode = CompositingMode.SourceOver;
            g.CompositingQuality = CompositingQuality.HighSpeed;
            g.InterpolationMode = InterpolationMode.NearestNeighbor;
            g.PixelOffsetMode = PixelOffsetMode.Half;
            g.SmoothingMode = SmoothingMode.None;

            for (var col = 0; col < columns; col++)
            {
                var cell = cells[row, col];
                var src = cell.ForegroundBounds;
                var localSrcX = src.Left - cell.CellBounds.Left;
                var localSrcY = src.Top - cell.CellBounds.Top;
                var destX = (col * frameWidth) + (int)Math.Round((frameWidth / 2.0) - (cell.AnchorX * scale));
                var destY = (int)Math.Round(baselineY - (cell.AnchorY * scale));
                var dest = new Rectangle(
                    destX + (int)Math.Round(localSrcX * scale),
                    destY + (int)Math.Round(localSrcY * scale),
                    Math.Max(1, (int)Math.Round(src.Width * scale)),
                    Math.Max(1, (int)Math.Round(src.Height * scale)));

                g.DrawImage(sanitizedSource, dest, src, GraphicsUnit.Pixel);
                placements.Add(new RenderMetric
                {
                    State = state,
                    Frame = col,
                    AnchorSourceX = cell.AnchorX,
                    AnchorSourceY = cell.AnchorY,
                    DestX = destX,
                    DestY = destY
                });
            }

            g.Flush();
            CleanRenderedStrip(strip, state, frameWidth, frameHeight);
            var pixels = PixelData.FromBitmap(strip);
            for (var col = 0; col < columns; col++)
            {
                var metric = AnalyzeRenderedFrame(pixels, state, col, frameWidth, frameHeight);
                metric.AnchorSourceX = placements[col].AnchorSourceX;
                metric.AnchorSourceY = placements[col].AnchorSourceY;
                metric.DestX = placements[col].DestX;
                metric.DestY = placements[col].DestY;
                metrics.Add(metric);
            }

            strip.Save(outPath, ImageFormat.Png);
        }
    }

    private sealed class RenderComponent
    {
        public int Id;
        public int Count;
        public int MinX = int.MaxValue;
        public int MinY = int.MaxValue;
        public int MaxX = int.MinValue;
        public int MaxY = int.MinValue;
        public double CenterX { get { return MinX + ((MaxX - MinX + 1) / 2.0); } }
        public double CenterY { get { return MinY + ((MaxY - MinY + 1) / 2.0); } }
    }

    private static void CleanRenderedStrip(Bitmap strip, string state, int frameWidth, int frameHeight)
    {
        var rect = new Rectangle(0, 0, strip.Width, strip.Height);
        var data = strip.LockBits(rect, ImageLockMode.ReadWrite, PixelFormat.Format32bppArgb);
        try
        {
            var stride = Math.Abs(data.Stride);
            var bytes = new byte[stride * strip.Height];
            for (var y = 0; y < strip.Height; y++)
            {
                Marshal.Copy(IntPtr.Add(data.Scan0, y * data.Stride), bytes, y * stride, stride);
            }

            var frameCount = strip.Width / frameWidth;
            for (var frame = 0; frame < frameCount; frame++)
            {
                var x0 = frame * frameWidth;
                RemoveChromaPixels(bytes, stride, x0, frameWidth, frameHeight);
                var labels = new int[frameWidth * frameHeight];
                for (var i = 0; i < labels.Length; i++) labels[i] = -1;
                var components = LabelComponents(bytes, stride, x0, frameWidth, frameHeight, labels);
                if (components.Count == 0) continue;
                var main = ChooseMainComponent(components, frameWidth, frameHeight);
                for (var y = 0; y < frameHeight; y++)
                {
                    for (var x = 0; x < frameWidth; x++)
                    {
                        var id = labels[(y * frameWidth) + x];
                        if (id < 0) continue;
                        var component = components[id];
                        if (!ShouldKeepComponent(state, component, main, frameWidth, frameHeight))
                        {
                            var offset = (y * stride) + ((x0 + x) * 4);
                            bytes[offset + 0] = 0;
                            bytes[offset + 1] = 0;
                            bytes[offset + 2] = 0;
                            bytes[offset + 3] = 0;
                        }
                    }
                }
            }

            for (var y = 0; y < strip.Height; y++)
            {
                Marshal.Copy(bytes, y * stride, IntPtr.Add(data.Scan0, y * data.Stride), stride);
            }
        }
        finally
        {
            strip.UnlockBits(data);
        }
    }

    private static void RemoveChromaPixels(byte[] bytes, int stride, int x0, int frameWidth, int frameHeight)
    {
        for (var y = 0; y < frameHeight; y++)
        {
            for (var x = 0; x < frameWidth; x++)
            {
                var offset = (y * stride) + ((x0 + x) * 4);
                var b = bytes[offset + 0];
                var g = bytes[offset + 1];
                var r = bytes[offset + 2];
                var a = bytes[offset + 3];
                if (IsChroma(r, g, b, a))
                {
                    bytes[offset + 0] = 0;
                    bytes[offset + 1] = 0;
                    bytes[offset + 2] = 0;
                    bytes[offset + 3] = 0;
                }
            }
        }
    }

    private static List<RenderComponent> LabelComponents(byte[] bytes, int stride, int x0, int frameWidth, int frameHeight, int[] labels)
    {
        var components = new List<RenderComponent>();
        var dx = new int[] { 1, -1, 0, 0 };
        var dy = new int[] { 0, 0, 1, -1 };
        for (var sy = 0; sy < frameHeight; sy++)
        {
            for (var sx = 0; sx < frameWidth; sx++)
            {
                var startIndex = (sy * frameWidth) + sx;
                if (labels[startIndex] >= 0 || !IsOpaque(bytes, stride, x0 + sx, sy)) continue;

                var component = new RenderComponent { Id = components.Count };
                components.Add(component);
                var queue = new Queue<int[]>();
                queue.Enqueue(new int[] { sx, sy });
                labels[startIndex] = component.Id;

                while (queue.Count > 0)
                {
                    var point = queue.Dequeue();
                    var x = point[0];
                    var y = point[1];
                    component.Count++;
                    if (x < component.MinX) component.MinX = x;
                    if (x > component.MaxX) component.MaxX = x;
                    if (y < component.MinY) component.MinY = y;
                    if (y > component.MaxY) component.MaxY = y;

                    for (var i = 0; i < 4; i++)
                    {
                        var nx = x + dx[i];
                        var ny = y + dy[i];
                        if (nx < 0 || nx >= frameWidth || ny < 0 || ny >= frameHeight) continue;
                        var nextIndex = (ny * frameWidth) + nx;
                        if (labels[nextIndex] >= 0 || !IsOpaque(bytes, stride, x0 + nx, ny)) continue;
                        labels[nextIndex] = component.Id;
                        queue.Enqueue(new int[] { nx, ny });
                    }
                }
            }
        }
        return components;
    }

    private static RenderComponent ChooseMainComponent(List<RenderComponent> components, int frameWidth, int frameHeight)
    {
        RenderComponent best = null;
        var bestScore = double.MaxValue;
        foreach (var component in components)
        {
            if (component.Count < 120) continue;
            var score = Math.Abs(component.CenterX - (frameWidth / 2.0));
            if (component.MaxY < frameHeight * 0.58) score += 80;
            score -= Math.Min(14, component.Count / 260.0);
            if (score < bestScore)
            {
                best = component;
                bestScore = score;
            }
        }
        if (best != null) return best;
        foreach (var component in components)
        {
            if (best == null || component.Count > best.Count) best = component;
        }
        return best;
    }

    private static bool ShouldKeepComponent(string state, RenderComponent component, RenderComponent main, int frameWidth, int frameHeight)
    {
        if (component.Id == main.Id) return true;
        if (component.MaxY < main.MinY - 4) return false;
        if (component.Count < 90) return false;
        if (component.MaxX < main.MinX - 6) return false;
        if (component.MinX > main.MaxX + 14 && component.Count < 260) return false;

        if (state == "loot_interact" && component.Count >= 180 && component.MaxY >= frameHeight * 0.70 && Math.Abs(component.CenterX - main.CenterX) <= 48)
        {
            return true;
        }

        if (ExpandedOverlap(component, main, 8) && component.Count >= 160)
        {
            return true;
        }

        if (component.Count >= 520 && Math.Abs(component.CenterX - main.CenterX) <= 28 && VerticalOverlap(component, main))
        {
            return true;
        }

        return false;
    }

    private static bool ExpandedOverlap(RenderComponent a, RenderComponent b, int padding)
    {
        return a.MinX <= b.MaxX + padding && a.MaxX >= b.MinX - padding && a.MinY <= b.MaxY + padding && a.MaxY >= b.MinY - padding;
    }

    private static bool VerticalOverlap(RenderComponent a, RenderComponent b)
    {
        return a.MinY <= b.MaxY && a.MaxY >= b.MinY;
    }

    private static bool IsOpaque(byte[] bytes, int stride, int x, int y)
    {
        var offset = (y * stride) + (x * 4);
        return bytes[offset + 3] > 24;
    }
    private static RenderMetric AnalyzeRenderedFrame(PixelData pixels, string state, int frame, int frameWidth, int frameHeight)
    {
        var x0 = frame * frameWidth;
        var x1 = x0 + frameWidth - 1;
        var minX = int.MaxValue;
        var minY = int.MaxValue;
        var maxX = int.MinValue;
        var maxY = int.MinValue;
        var bodyMinX = int.MaxValue;
        var bodyMinY = int.MaxValue;
        var bodyMaxX = int.MinValue;
        var bodyMaxY = int.MinValue;
        var magenta = 0;
        var fg = 0;
        var body = 0;

        for (var y = 0; y < frameHeight; y++)
        {
            for (var x = x0; x <= x1; x++)
            {
                var offset = pixels.Offset(x, y);
                var b = pixels.Bytes[offset + 0];
                var g = pixels.Bytes[offset + 1];
                var r = pixels.Bytes[offset + 2];
                var a = pixels.Bytes[offset + 3];
                if (a <= 20)
                {
                    continue;
                }
                if (IsChroma(r, g, b, a))
                {
                    magenta++;
                }

                fg++;
                if (x - x0 < minX) minX = x - x0;
                if (x - x0 > maxX) maxX = x - x0;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;

                if (IsBodyPixel(r, g, b, a))
                {
                    body++;
                    if (x - x0 < bodyMinX) bodyMinX = x - x0;
                    if (x - x0 > bodyMaxX) bodyMaxX = x - x0;
                    if (y < bodyMinY) bodyMinY = y;
                    if (y > bodyMaxY) bodyMaxY = y;
                }
            }
        }

        var bounds = fg == 0 ? Rectangle.Empty : Rectangle.FromLTRB(minX, minY, maxX + 1, maxY + 1);
        var bodyBounds = body == 0 ? bounds : Rectangle.FromLTRB(bodyMinX, bodyMinY, bodyMaxX + 1, bodyMaxY + 1);
        return new RenderMetric
        {
            State = state,
            Frame = frame,
            MagentaPixels = magenta,
            Bounds = bounds,
            BodyBounds = bodyBounds,
            BodyCenterX = bodyBounds.IsEmpty ? 0 : bodyBounds.Left + (bodyBounds.Width / 2.0),
            BodyFootY = bodyBounds.IsEmpty ? 0 : bodyBounds.Bottom - 1
        };
    }

    private static void SavePreview(string[] stripPaths, int frameWidth, int frameHeight, string outPath)
    {
        var previewWidth = frameWidth;
        using (var firstStrip = new Bitmap(stripPaths[0]))
        {
            previewWidth = firstStrip.Width;
        }
        using (var preview = new Bitmap(previewWidth, frameHeight * stripPaths.Length, PixelFormat.Format32bppArgb))
        using (var g = Graphics.FromImage(preview))
        {
            DrawChecker(g, preview.Width, preview.Height, 16);
            using (var pen = new Pen(Color.FromArgb(80, 0, 0, 0)))
            {
                for (var y = 0; y <= preview.Height; y += frameHeight) g.DrawLine(pen, 0, y, preview.Width, y);
                for (var x = 0; x <= preview.Width; x += frameWidth) g.DrawLine(pen, x, 0, x, preview.Height);
            }
            for (var i = 0; i < stripPaths.Length; i++)
            {
                using (var strip = new Bitmap(stripPaths[i]))
                {
                    g.DrawImageUnscaled(strip, 0, i * frameHeight);
                }
            }
            preview.Save(outPath, ImageFormat.Png);
        }
    }

    private static void DrawChecker(Graphics g, int width, int height, int size)
    {
        using (var a = new SolidBrush(Color.FromArgb(238, 238, 238)))
        using (var b = new SolidBrush(Color.FromArgb(214, 218, 220)))
        {
            for (var y = 0; y < height; y += size)
            {
                for (var x = 0; x < width; x += size)
                {
                    var useA = ((x / size) + (y / size)) % 2 == 0;
                    g.FillRectangle(useA ? a : b, x, y, size, size);
                }
            }
        }
    }

    private static void SaveMetrics(List<RenderMetric> metrics, string outPath)
    {
        var sb = new StringBuilder();
        sb.AppendLine("state,frame,magentaPixels,boundsLeft,boundsTop,boundsWidth,boundsHeight,bodyCenterX,bodyFootY,bodyBoundsLeft,bodyBoundsTop,bodyBoundsWidth,bodyBoundsHeight,anchorSourceX,anchorSourceY,destX,destY");
        foreach (var m in metrics)
        {
            sb.Append(m.State).Append(',')
              .Append(m.Frame).Append(',')
              .Append(m.MagentaPixels).Append(',')
              .Append(m.Bounds.Left).Append(',')
              .Append(m.Bounds.Top).Append(',')
              .Append(m.Bounds.Width).Append(',')
              .Append(m.Bounds.Height).Append(',')
              .Append(m.BodyCenterX.ToString("0.00", CultureInfo.InvariantCulture)).Append(',')
              .Append(m.BodyFootY).Append(',')
              .Append(m.BodyBounds.Left).Append(',')
              .Append(m.BodyBounds.Top).Append(',')
              .Append(m.BodyBounds.Width).Append(',')
              .Append(m.BodyBounds.Height).Append(',')
              .Append(m.AnchorSourceX.ToString("0.00", CultureInfo.InvariantCulture)).Append(',')
              .Append(m.AnchorSourceY.ToString("0.00", CultureInfo.InvariantCulture)).Append(',')
              .Append(m.DestX).Append(',')
              .Append(m.DestY)
              .AppendLine();
        }
        File.WriteAllText(outPath, sb.ToString(), new UTF8Encoding(false));
    }

    private static bool IsBodyPixel(byte r, byte g, byte b, byte a)
    {
        if (a <= 20 || IsChroma(r, g, b, a)) return false;
        if (r > 220 && g > 205 && b > 155) return false;
        if (r < 190 && g > 145 && b > 155) return false;
        return true;
    }

    private static bool IsChroma(byte r, byte g, byte b, byte a)
    {
        if (a <= 8) return true;
        if (r > 145 && b > 130 && g < 120 && r > (g * 1.35) && b > (g * 1.35)) return true;
        if (r > 190 && b > 170 && g < 145 && Math.Abs(r - b) < 95) return true;
        if (r > 85 && b > 85 && g < 95 && r > (g * 1.35) && b > (g * 1.35) && Math.Abs(r - b) < 90) return true;
        if (r > 55 && b > 70 && g < 60 && (r + b) > (g * 3.0) && Math.Abs(r - b) < 85) return true;
        return false;
    }

    private static double Median(List<int> values)
    {
        var ordered = values.OrderBy(v => v).ToArray();
        if (ordered.Length == 0) return 0;
        var mid = ordered.Length / 2;
        if ((ordered.Length % 2) == 1) return ordered[mid];
        return (ordered[mid - 1] + ordered[mid]) / 2.0;
    }
}
"@

Add-Type -TypeDefinition $source -ReferencedAssemblies 'System.Drawing.dll'

function Resolve-RepoPath {
    param([string] $Path)
    if ([System.IO.Path]::IsPathRooted($Path)) { return $Path }
    return [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
}

[SableStillAnimator]::Run(
    (Resolve-RepoPath $SourcePath),
    (Resolve-RepoPath $OutputDir),
    (Resolve-RepoPath $ReviewDir),
    $Rows,
    $Columns,
    $OutputColumns,
    $FrameWidth,
    $FrameHeight,
    $TargetBodyHeight,
    $BaselineY,
    $States)