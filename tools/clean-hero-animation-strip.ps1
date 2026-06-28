param(
    [string] $InputPath,
    [string] $OutputPath,
    [int] $FrameWidth = 128,
    [int] $FrameHeight = 128,
    [int] $MinComponentPixels = 20,
    [int] $MinUsableMainPixels = 1400,
    [int] $MinUsableMainWidth = 38,
    [int] $MaxDetachedCenterDistance = 46,
    [string] $FrameMap = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

function Resolve-RepoPath {
    param([string] $Path)
    if ([System.IO.Path]::IsPathRooted($Path)) { return $Path }
    return Join-Path (Get-Location) $Path
}

function New-ArgbBitmap {
    param([int] $Width, [int] $Height)
    return [System.Drawing.Bitmap]::new($Width, $Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Get-FrameComponents {
    param(
        [System.Drawing.Bitmap] $Image,
        [int] $FrameIndex,
        [int] $FrameWidth,
        [int] $FrameHeight,
        [int] $MinPixels
    )

    $seen = New-Object 'bool[,]' $FrameWidth, $FrameHeight
    $componentIds = New-Object 'int[,]' $FrameWidth, $FrameHeight
    $components = [System.Collections.Generic.List[object]]::new()
    $nextId = 1

    for ($y = 0; $y -lt $FrameHeight; $y++) {
        for ($x = 0; $x -lt $FrameWidth; $x++) {
            if ($seen[$x, $y]) { continue }
            $seen[$x, $y] = $true
            $pixel = $Image.GetPixel(($FrameIndex * $FrameWidth) + $x, $y)
            if ($pixel.A -le 15) { continue }

            $queue = [System.Collections.Generic.Queue[object]]::new()
            $queue.Enqueue([pscustomobject]@{ X = $x; Y = $y })
            $pixels = 0
            $minX = $x
            $maxX = $x
            $minY = $y
            $maxY = $y
            $componentPixels = [System.Collections.Generic.List[object]]::new()

            while ($queue.Count -gt 0) {
                $point = $queue.Dequeue()
                [void] $componentPixels.Add($point)
                $pixels++
                if ($point.X -lt $minX) { $minX = $point.X }
                if ($point.X -gt $maxX) { $maxX = $point.X }
                if ($point.Y -lt $minY) { $minY = $point.Y }
                if ($point.Y -gt $maxY) { $maxY = $point.Y }

                foreach ($delta in @(@(-1, 0), @(1, 0), @(0, -1), @(0, 1))) {
                    $nx = $point.X + $delta[0]
                    $ny = $point.Y + $delta[1]
                    if ($nx -lt 0 -or $nx -ge $FrameWidth -or $ny -lt 0 -or $ny -ge $FrameHeight -or $seen[$nx, $ny]) { continue }
                    $seen[$nx, $ny] = $true
                    if ($Image.GetPixel(($FrameIndex * $FrameWidth) + $nx, $ny).A -gt 15) {
                        $queue.Enqueue([pscustomobject]@{ X = $nx; Y = $ny })
                    }
                }
            }

            if ($pixels -ge $MinPixels) {
                foreach ($point in $componentPixels) {
                    $componentIds[$point.X, $point.Y] = $nextId
                }
                [void] $components.Add([pscustomobject]@{
                    Id = $nextId
                    Pixels = $pixels
                    MinX = $minX
                    MaxX = $maxX
                    MinY = $minY
                    MaxY = $maxY
                    Width = ($maxX - $minX + 1)
                    Height = ($maxY - $minY + 1)
                    CenterX = ($minX + (($maxX - $minX + 1) / 2.0))
                })
                $nextId++
            }
        }
    }

    return [pscustomobject]@{
        Components = @($components)
        ComponentIds = $componentIds
    }
}

$inputFullPath = Resolve-RepoPath $InputPath
$outputFullPath = Resolve-RepoPath $OutputPath
if ([System.IO.Path]::GetFullPath($inputFullPath) -eq [System.IO.Path]::GetFullPath($outputFullPath)) {
    throw "OutputPath must differ from InputPath. Write to a review/temp PNG, then copy it over the runtime asset after this script exits."
}
if (-not (Test-Path -LiteralPath $inputFullPath -PathType Leaf)) {
    throw "Missing input strip: $InputPath"
}

$source = [System.Drawing.Bitmap]::FromFile($inputFullPath)
try {
    if ($source.Height -ne $FrameHeight) { throw "Expected height $FrameHeight, got $($source.Height)." }
    if ($source.Width % $FrameWidth -ne 0) { throw "Width $($source.Width) is not a multiple of $FrameWidth." }

    $sourceFrameCount = [int]($source.Width / $FrameWidth)
    $cleanFrames = [System.Collections.Generic.List[object]]::new()

    for ($frame = 0; $frame -lt $sourceFrameCount; $frame++) {
        $analysis = Get-FrameComponents -Image $source -FrameIndex $frame -FrameWidth $FrameWidth -FrameHeight $FrameHeight -MinPixels $MinComponentPixels
        $components = @($analysis.Components)
        if ($components.Count -eq 0) { continue }

        $main = $components | Sort-Object Pixels -Descending | Select-Object -First 1
        $kept = @($components | Where-Object { [Math]::Abs($_.CenterX - $main.CenterX) -le $MaxDetachedCenterDistance })
        $usable = $main.Pixels -ge $MinUsableMainPixels -and $main.Width -ge $MinUsableMainWidth
        if (-not $usable) {
            Write-Host "Skipping weak frame $frame mainPixels=$($main.Pixels) mainWidth=$($main.Width)"
            continue
        }

        $keepIds = @($kept | ForEach-Object { $_.Id })
        $minX = [int](($kept | Measure-Object MinX -Minimum).Minimum)
        $maxX = [int](($kept | Measure-Object MaxX -Maximum).Maximum)
        $minY = [int](($kept | Measure-Object MinY -Minimum).Minimum)
        $maxY = [int](($kept | Measure-Object MaxY -Maximum).Maximum)
        $width = $maxX - $minX + 1
        $height = $maxY - $minY + 1

        $frameBitmap = New-ArgbBitmap -Width $width -Height $height
        for ($y = $minY; $y -le $maxY; $y++) {
            for ($x = $minX; $x -le $maxX; $x++) {
                $id = $analysis.ComponentIds[$x, $y]
                if ($keepIds -contains $id) {
                    $frameBitmap.SetPixel($x - $minX, $y - $minY, $source.GetPixel(($frame * $FrameWidth) + $x, $y))
                }
            }
        }

        [void] $cleanFrames.Add([pscustomobject]@{
            SourceFrame = $frame
            Bitmap = $frameBitmap
            Width = $width
            Height = $height
            Bottom = $maxY
            Pixels = (($kept | Measure-Object Pixels -Sum).Sum)
        })
    }

    if ($cleanFrames.Count -eq 0) { throw "No usable frames found in $InputPath." }

    $outputIndices = if ([string]::IsNullOrWhiteSpace($FrameMap)) {
        0..($cleanFrames.Count - 1)
    } else {
        $FrameMap.Split(',') | ForEach-Object { [int]$_.Trim() }
    }

    foreach ($index in $outputIndices) {
        if ($index -lt 0 -or $index -ge $cleanFrames.Count) {
            throw "FrameMap references clean frame $index but only $($cleanFrames.Count) clean frames exist."
        }
    }

    $baselineY = $FrameHeight - 11
    $output = New-ArgbBitmap -Width ($FrameWidth * $outputIndices.Count) -Height $FrameHeight
    $graphics = [System.Drawing.Graphics]::FromImage($output)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None

        for ($outFrame = 0; $outFrame -lt $outputIndices.Count; $outFrame++) {
            $clean = $cleanFrames[$outputIndices[$outFrame]]
            $dstX = ($outFrame * $FrameWidth) + [int][Math]::Round(($FrameWidth - $clean.Width) / 2.0)
            $dstY = $baselineY - $clean.Height + 1
            $graphics.DrawImage($clean.Bitmap, $dstX, $dstY, $clean.Width, $clean.Height)
            Write-Host "Output frame $outFrame from source frame $($clean.SourceFrame) size=$($clean.Width)x$($clean.Height) pixels=$($clean.Pixels)"
        }
    } finally {
        $graphics.Dispose()
    }

    $output.Save($outputFullPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $output.Dispose()

    foreach ($clean in $cleanFrames) { $clean.Bitmap.Dispose() }
    Write-Host "Cleaned $OutputPath"
} finally {
    $source.Dispose()
}