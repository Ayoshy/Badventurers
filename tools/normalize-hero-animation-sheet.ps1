param(
    [string] $HeroId,
    [string] $SourcePath,
    [string] $OutputDir = "app/src/main/res-heroes/drawable-nodpi",
    [string] $ReviewDir = "docs/art/hero-animations",
    [int] $Rows = 6,
    [int] $Columns = 6,
    [int] $FrameWidth = 128,
    [int] $FrameHeight = 128,
    [int] $TargetMaxSize = 112,
    [string[]] $States = @("idle", "walk", "fight", "hurt_dead", "celebrate", "loot_interact")
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

if ([string]::IsNullOrWhiteSpace($HeroId)) {
    throw "HeroId is required."
}

if ([string]::IsNullOrWhiteSpace($SourcePath)) {
    throw "SourcePath is required."
}

if ($States.Count -ne $Rows) {
    throw "States count ($($States.Count)) must match Rows ($Rows)."
}

function Resolve-RepoPath {
    param([string] $Path)
    if ([System.IO.Path]::IsPathRooted($Path)) { return $Path }
    return Join-Path (Get-Location) $Path
}

function New-ArgbBitmap {
    param([int] $Width, [int] $Height)
    return [System.Drawing.Bitmap]::new($Width, $Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Get-CellBounds {
    param(
        [System.Drawing.Bitmap] $Image,
        [int] $X0,
        [int] $Y0,
        [int] $W,
        [int] $H
    )

    $minX = $X0 + $W
    $minY = $Y0 + $H
    $maxX = $X0
    $maxY = $Y0

    for ($y = $Y0; $y -lt ($Y0 + $H); $y++) {
        for ($x = $X0; $x -lt ($X0 + $W); $x++) {
            if ($Image.GetPixel($x, $y).A -gt 15) {
                if ($x -lt $minX) { $minX = $x }
                if ($x -gt $maxX) { $maxX = $x }
                if ($y -lt $minY) { $minY = $y }
                if ($y -gt $maxY) { $maxY = $y }
            }
        }
    }

    if ($maxX -lt $minX -or $maxY -lt $minY) {
        return $null
    }

    return [pscustomobject]@{
        X = $minX
        Y = $minY
        Width = ($maxX - $minX + 1)
        Height = ($maxY - $minY + 1)
        MaxY = $maxY
        CellCenterX = ($X0 + ($W / 2.0))
        BoundsCenterX = ($minX + (($maxX - $minX + 1) / 2.0))
    }
}

$sourceFullPath = Resolve-RepoPath $SourcePath
if (-not (Test-Path -LiteralPath $sourceFullPath -PathType Leaf)) {
    throw "Missing source animation sheet: $SourcePath"
}

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null
$heroReviewDir = Join-Path $ReviewDir $HeroId
New-Item -ItemType Directory -Force -Path $heroReviewDir | Out-Null

$source = [System.Drawing.Bitmap]::FromFile($sourceFullPath)
try {
    $cellWidth = [int][Math]::Floor($source.Width / $Columns)
    $cellHeight = [int][Math]::Floor($source.Height / $Rows)
    $generated = [System.Collections.Generic.List[string]]::new()

    for ($row = 0; $row -lt $Rows; $row++) {
        $stateId = $States[$row]
        $y0 = [int][Math]::Floor($row * $source.Height / $Rows)
        $y1 = if ($row -eq ($Rows - 1)) { $source.Height - 1 } else { [int][Math]::Floor(($row + 1) * $source.Height / $Rows) - 1 }
        $rowHeight = $y1 - $y0 + 1
        $bounds = @()

        for ($col = 0; $col -lt $Columns; $col++) {
            $x0 = [int][Math]::Floor($col * $source.Width / $Columns)
            $x1 = if ($col -eq ($Columns - 1)) { $source.Width - 1 } else { [int][Math]::Floor(($col + 1) * $source.Width / $Columns) - 1 }
            $bounds += Get-CellBounds -Image $source -X0 $x0 -Y0 $y0 -W ($x1 - $x0 + 1) -H $rowHeight
        }

        if (@($bounds | Where-Object { $null -ne $_ }).Count -eq 0) {
            throw "No foreground pixels found for ${HeroId}/${stateId}."
        }

        $maxWidth = [double](@($bounds | Where-Object { $null -ne $_ } | ForEach-Object { $_.Width } | Measure-Object -Maximum).Maximum)
        $maxHeight = [double](@($bounds | Where-Object { $null -ne $_ } | ForEach-Object { $_.Height } | Measure-Object -Maximum).Maximum)
        $baseline = [double](@($bounds | Where-Object { $null -ne $_ } | ForEach-Object { $_.MaxY } | Measure-Object -Maximum).Maximum)
        $scale = [Math]::Min($TargetMaxSize / $maxWidth, $TargetMaxSize / $maxHeight)
        if ($scale -le 0) { throw "Invalid scale for ${HeroId}/${stateId}." }

        $strip = New-ArgbBitmap -Width ($FrameWidth * $Columns) -Height $FrameHeight
        $g = [System.Drawing.Graphics]::FromImage($strip)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
            $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
            $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None

            for ($col = 0; $col -lt $Columns; $col++) {
                $b = $bounds[$col]
                if ($null -eq $b) { continue }

                $dstW = [int][Math]::Max(1, [Math]::Round($b.Width * $scale))
                $dstH = [int][Math]::Max(1, [Math]::Round($b.Height * $scale))
                # Live-Lite owns horizontal travel; frame strips should animate pose, not slide the whole sprite.
                $motionX = 0
                $motionY = [int][Math]::Round(($baseline - $b.MaxY) * $scale)
                $dstX = ($FrameWidth * $col) + [int][Math]::Round(($FrameWidth - $dstW) / 2.0) + $motionX
                $dstY = ($FrameHeight - 10 - $dstH) - $motionY

                $dstX = [Math]::Max(($FrameWidth * $col) + 1, [Math]::Min(($FrameWidth * ($col + 1)) - $dstW - 1, $dstX))
                $dstY = [Math]::Max(1, [Math]::Min($FrameHeight - $dstH - 1, $dstY))

                $srcRect = [System.Drawing.Rectangle]::new($b.X, $b.Y, $b.Width, $b.Height)
                $dstRect = [System.Drawing.Rectangle]::new($dstX, $dstY, $dstW, $dstH)
                $g.DrawImage($source, $dstRect, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)
            }
        } finally {
            $g.Dispose()
        }

        $outPath = Join-Path $OutputDir "hero_anim_${HeroId}_${stateId}.png"
        $strip.Save((Resolve-RepoPath $outPath), [System.Drawing.Imaging.ImageFormat]::Png)
        $strip.Dispose()
        [void] $generated.Add($outPath)
        Write-Host "Generated $outPath"
    }

    $previewWidth = 900
    $previewHeight = 760
    $preview = New-ArgbBitmap -Width $previewWidth -Height $previewHeight
    $pg = [System.Drawing.Graphics]::FromImage($preview)
    try {
        $pg.Clear([System.Drawing.Color]::FromArgb(255, 20, 25, 22))
        $pg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $pg.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        $titleFont = [System.Drawing.Font]::new("Arial", 18, [System.Drawing.FontStyle]::Bold)
        $smallFont = [System.Drawing.Font]::new("Arial", 9, [System.Drawing.FontStyle]::Regular)
        $textBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 244, 228, 178))
        $mutedBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 184, 171, 128))
        try {
            $pg.DrawString("$HeroId runtime-normalized animation preview", $titleFont, $textBrush, 18, 16)
            $pg.DrawString("128px cell exports sampled at 64px and 48px", $smallFont, $mutedBrush, 20, 48)
            $pg.DrawString("64px live lane", $smallFont, $textBrush, 20, 86)
            $pg.DrawString("48px compact", $smallFont, $textBrush, 575, 86)

            for ($row = 0; $row -lt $Rows; $row++) {
                $stateId = $States[$row]
                $stripPath = Resolve-RepoPath (Join-Path $OutputDir "hero_anim_${HeroId}_${stateId}.png")
                $strip = [System.Drawing.Bitmap]::FromFile($stripPath)
                try {
                    $pg.DrawString($stateId, $smallFont, $mutedBrush, 20, 116 + ($row * 82))
                    for ($col = 0; $col -lt $Columns; $col++) {
                        $srcRect = [System.Drawing.Rectangle]::new($FrameWidth * $col, 0, $FrameWidth, $FrameHeight)
                        $dstRect = [System.Drawing.Rectangle]::new(122 + ($col * 70), 104 + ($row * 82), 64, 64)
                        $pg.DrawImage($strip, $dstRect, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)
                    }
                    $pg.DrawString($stateId, $smallFont, $mutedBrush, 575, 116 + ($row * 62))
                    foreach ($sampleCol in @(0, 2, 4)) {
                        $srcRect = [System.Drawing.Rectangle]::new($FrameWidth * $sampleCol, 0, $FrameWidth, $FrameHeight)
                        $dstRect = [System.Drawing.Rectangle]::new(686 + (($sampleCol / 2) * 56), 104 + ($row * 62), 48, 48)
                        $pg.DrawImage($strip, $dstRect, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)
                    }
                } finally {
                    $strip.Dispose()
                }
            }
        } finally {
            $titleFont.Dispose()
            $smallFont.Dispose()
            $textBrush.Dispose()
            $mutedBrush.Dispose()
        }
    } finally {
        $pg.Dispose()
    }

    $previewPath = Join-Path $heroReviewDir "${HeroId}-runtime-normalized-preview.png"
    $preview.Save((Resolve-RepoPath $previewPath), [System.Drawing.Imaging.ImageFormat]::Png)
    $preview.Dispose()
    Write-Host "Preview $previewPath"
} finally {
    $source.Dispose()
}