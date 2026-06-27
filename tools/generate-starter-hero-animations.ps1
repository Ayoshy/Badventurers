param(
    [string] $HeroResourceDir = "app/src/main/res-heroes/drawable-nodpi",
    [string] $ReviewDir = "docs/art/hero-animations/starter"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

$CellSize = 128
$SpriteSize = 116
$BackgroundThreshold = 42
$BackgroundDistance = 34
$PreviewScale = 0.5

$Heroes = @(
    @{ Id = "brugg"; Name = "Brugg"; Portrait = "hero_portrait_brugg.png" },
    @{ Id = "mira"; Name = "Mira"; Portrait = "hero_portrait_mira.png" },
    @{ Id = "nell"; Name = "Nell"; Portrait = "hero_portrait_nell.png" }
)

$States = @(
    @{ Id = "idle"; Label = "idle"; Frames = @(
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 },
        @{ X = 0; Y = -2; Rotation = 0; ScaleX = 1.00; ScaleY = 1.01 },
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 },
        @{ X = 0; Y = 1; Rotation = 0; ScaleX = 1.01; ScaleY = 0.99 }
    ) },
    @{ Id = "walk"; Label = "walk"; Frames = @(
        @{ X = -5; Y = 2; Rotation = -2; ScaleX = 1.00; ScaleY = 0.99 },
        @{ X = -3; Y = -1; Rotation = -1; ScaleX = 1.01; ScaleY = 1.01 },
        @{ X = 0; Y = 1; Rotation = 1; ScaleX = 1.00; ScaleY = 0.99 },
        @{ X = 3; Y = -1; Rotation = 2; ScaleX = 0.99; ScaleY = 1.01 },
        @{ X = 5; Y = 2; Rotation = 1; ScaleX = 1.00; ScaleY = 0.99 },
        @{ X = 0; Y = 0; Rotation = -1; ScaleX = 1.01; ScaleY = 1.00 }
    ) },
    @{ Id = "fight"; Label = "fight"; Frames = @(
        @{ X = -3; Y = 0; Rotation = -4; ScaleX = 0.99; ScaleY = 1.01 },
        @{ X = 3; Y = -1; Rotation = 5; ScaleX = 1.02; ScaleY = 0.99 },
        @{ X = 8; Y = -2; Rotation = 7; ScaleX = 1.03; ScaleY = 0.98 },
        @{ X = -2; Y = 1; Rotation = -3; ScaleX = 0.99; ScaleY = 1.00 },
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 }
    ) },
    @{ Id = "hurt_dead"; Label = "hurt/dead"; Frames = @(
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 },
        @{ X = 4; Y = 5; Rotation = 10; ScaleX = 1.00; ScaleY = 0.96 },
        @{ X = 8; Y = 12; Rotation = 22; ScaleX = 1.02; ScaleY = 0.90 },
        @{ X = 13; Y = 17; Rotation = 35; ScaleX = 1.05; ScaleY = 0.82 }
    ) },
    @{ Id = "celebrate"; Label = "celebrate"; Frames = @(
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 },
        @{ X = -2; Y = -6; Rotation = -4; ScaleX = 1.03; ScaleY = 1.03 },
        @{ X = 2; Y = -9; Rotation = 4; ScaleX = 1.05; ScaleY = 1.05 },
        @{ X = 0; Y = -4; Rotation = -2; ScaleX = 1.02; ScaleY = 1.02 },
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 }
    ) },
    @{ Id = "loot_interact"; Label = "loot/interact"; Frames = @(
        @{ X = -2; Y = 1; Rotation = -2; ScaleX = 1.00; ScaleY = 1.00 },
        @{ X = -4; Y = 5; Rotation = -5; ScaleX = 1.03; ScaleY = 0.96 },
        @{ X = 3; Y = 8; Rotation = 5; ScaleX = 1.04; ScaleY = 0.94 },
        @{ X = 4; Y = 4; Rotation = 3; ScaleX = 1.02; ScaleY = 0.97 },
        @{ X = 0; Y = 0; Rotation = 0; ScaleX = 1.00; ScaleY = 1.00 }
    ) }
)

function New-ArgbBitmap {
    param([int] $Width, [int] $Height)
    return [System.Drawing.Bitmap]::new($Width, $Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Get-ColorDistance {
    param([System.Drawing.Color] $A, [System.Drawing.Color] $B)
    $dr = [int]$A.R - [int]$B.R
    $dg = [int]$A.G - [int]$B.G
    $db = [int]$A.B - [int]$B.B
    return [Math]::Sqrt(($dr * $dr) + ($dg * $dg) + ($db * $db))
}

function Test-BackgroundCandidate {
    param(
        [System.Drawing.Color] $Color,
        [System.Drawing.Color] $Reference
    )
    $brightness = ([int]$Color.R + [int]$Color.G + [int]$Color.B) / 3
    return $brightness -le $BackgroundThreshold -and (Get-ColorDistance -A $Color -B $Reference) -le $BackgroundDistance
}

function Clear-EdgeBackground {
    param([System.Drawing.Bitmap] $Bitmap)

    $w = $Bitmap.Width
    $h = $Bitmap.Height
    $reference = $Bitmap.GetPixel(0, 0)
    $visited = New-Object 'bool[,]' $w, $h
    $queue = [System.Collections.Generic.Queue[object]]::new()

    for ($x = 0; $x -lt $w; $x++) {
        $queue.Enqueue(@($x, 0))
        $queue.Enqueue(@($x, ($h - 1)))
    }
    for ($y = 0; $y -lt $h; $y++) {
        $queue.Enqueue(@(0, $y))
        $queue.Enqueue(@(($w - 1), $y))
    }

    while ($queue.Count -gt 0) {
        $pt = $queue.Dequeue()
        $x = [int]$pt[0]
        $y = [int]$pt[1]
        if ($x -lt 0 -or $x -ge $w -or $y -lt 0 -or $y -ge $h) { continue }
        if ($visited[$x, $y]) { continue }
        $visited[$x, $y] = $true
        $c = $Bitmap.GetPixel($x, $y)
        if (-not (Test-BackgroundCandidate -Color $c -Reference $reference)) { continue }
        $Bitmap.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, $c.R, $c.G, $c.B))
        $queue.Enqueue(@(($x + 1), $y))
        $queue.Enqueue(@(($x - 1), $y))
        $queue.Enqueue(@($x, ($y + 1)))
        $queue.Enqueue(@($x, ($y - 1)))
    }
}

function New-BaseSprite {
    param([string] $PortraitPath)

    $portrait = [System.Drawing.Bitmap]::FromFile((Resolve-Path $PortraitPath))
    try {
        $sprite = New-ArgbBitmap -Width $CellSize -Height $CellSize
        $g = [System.Drawing.Graphics]::FromImage($sprite)
        try {
            $g.Clear([System.Drawing.Color]::Transparent)
            $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
            $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
            $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None
            $offset = [int](($CellSize - $SpriteSize) / 2)
            $g.DrawImage($portrait, $offset, $offset, $SpriteSize, $SpriteSize)
        } finally {
            $g.Dispose()
        }
        Clear-EdgeBackground -Bitmap $sprite
        return $sprite
    } finally {
        $portrait.Dispose()
    }
}

function Draw-Frame {
    param(
        [System.Drawing.Graphics] $Graphics,
        [System.Drawing.Bitmap] $Sprite,
        [hashtable] $Frame,
        [int] $OffsetX,
        [bool] $DrawGround
    )

    if ($DrawGround) {
        $shadowBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(80, 0, 0, 0))
        try {
            $Graphics.FillEllipse($shadowBrush, $OffsetX + 34, 106, 60, 13)
        } finally {
            $shadowBrush.Dispose()
        }
    }

    $state = $Graphics.Save()
    $Graphics.TranslateTransform($OffsetX + ($CellSize / 2) + [float]$Frame.X, ($CellSize / 2) + [float]$Frame.Y)
    $Graphics.RotateTransform([float]$Frame.Rotation)
    $Graphics.ScaleTransform([float]$Frame.ScaleX, [float]$Frame.ScaleY)
    $Graphics.DrawImage($Sprite, -($CellSize / 2), -($CellSize / 2), $CellSize, $CellSize)
    $Graphics.Restore($state)
}

function Save-StateSheet {
    param(
        [System.Drawing.Bitmap] $Sprite,
        [hashtable] $Hero,
        [hashtable] $State
    )

    $frames = @($State.Frames)
    $sheet = New-ArgbBitmap -Width ($CellSize * $frames.Count) -Height $CellSize
    $g = [System.Drawing.Graphics]::FromImage($sheet)
    try {
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        for ($i = 0; $i -lt $frames.Count; $i++) {
            Draw-Frame -Graphics $g -Sprite $Sprite -Frame $frames[$i] -OffsetX ($CellSize * $i) -DrawGround $true
        }
    } finally {
        $g.Dispose()
    }

    $outPath = Join-Path $HeroResourceDir "hero_anim_$($Hero.Id)_$($State.Id).png"
    $sheet.Save((Join-Path (Get-Location) $outPath), [System.Drawing.Imaging.ImageFormat]::Png)
    $sheet.Dispose()
    return $outPath
}

function Draw-PreviewFrame {
    param(
        [System.Drawing.Graphics] $Graphics,
        [System.Drawing.Bitmap] $Image,
        [int] $SourceX,
        [int] $DestX,
        [int] $DestY,
        [int] $DestSize
    )
    $src = [System.Drawing.Rectangle]::new($SourceX, 0, $CellSize, $CellSize)
    $dst = [System.Drawing.Rectangle]::new($DestX, $DestY, $DestSize, $DestSize)
    $Graphics.DrawImage($Image, $dst, $src, [System.Drawing.GraphicsUnit]::Pixel)
}

New-Item -ItemType Directory -Force -Path $HeroResourceDir | Out-Null
New-Item -ItemType Directory -Force -Path $ReviewDir | Out-Null

$generated = [System.Collections.Generic.List[string]]::new()
$sheetPathsByKey = @{}
foreach ($hero in $Heroes) {
    $portraitPath = Join-Path $HeroResourceDir $hero.Portrait
    $sprite = New-BaseSprite -PortraitPath $portraitPath
    try {
        foreach ($state in $States) {
            $outPath = Save-StateSheet -Sprite $sprite -Hero $hero -State $state
            [void] $generated.Add($outPath)
            $sheetPathsByKey["$($hero.Id)/$($state.Id)"] = $outPath
        }
    } finally {
        $sprite.Dispose()
    }
}

$contactCell = 96
$labelHeight = 22
$leftLabel = 74
$contactWidth = $leftLabel + ($States.Count * $contactCell)
$contactHeight = $labelHeight + ($Heroes.Count * ($contactCell + $labelHeight))
$contact = New-ArgbBitmap -Width $contactWidth -Height $contactHeight
$cg = [System.Drawing.Graphics]::FromImage($contact)
try {
    $cg.Clear([System.Drawing.Color]::FromArgb(255, 26, 27, 22))
    $cg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $font = [System.Drawing.Font]::new("Arial", 9, [System.Drawing.FontStyle]::Bold)
    $smallFont = [System.Drawing.Font]::new("Arial", 7, [System.Drawing.FontStyle]::Regular)
    $textBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 245, 231, 178))
    $mutedBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 196, 184, 133))
    try {
        for ($s = 0; $s -lt $States.Count; $s++) {
            $cg.DrawString($States[$s].Label, $smallFont, $mutedBrush, $leftLabel + ($s * $contactCell) + 4, 4)
        }
        for ($h = 0; $h -lt $Heroes.Count; $h++) {
            $hero = $Heroes[$h]
            $rowTop = $labelHeight + ($h * ($contactCell + $labelHeight))
            $cg.DrawString($hero.Name, $font, $textBrush, 4, $rowTop + 36)
            for ($s = 0; $s -lt $States.Count; $s++) {
                $state = $States[$s]
                $sheet = [System.Drawing.Bitmap]::FromFile((Resolve-Path $sheetPathsByKey["$($hero.Id)/$($state.Id)"]))
                try {
                    Draw-PreviewFrame -Graphics $cg -Image $sheet -SourceX 0 -DestX ($leftLabel + ($s * $contactCell)) -DestY $rowTop -DestSize $contactCell
                } finally {
                    $sheet.Dispose()
                }
            }
        }
    } finally {
        $font.Dispose()
        $smallFont.Dispose()
        $textBrush.Dispose()
        $mutedBrush.Dispose()
    }
} finally {
    $cg.Dispose()
}
$contactPath = Join-Path $ReviewDir "starter-hero-animation-contact-sheet.png"
$contact.Save((Join-Path (Get-Location) $contactPath), [System.Drawing.Imaging.ImageFormat]::Png)
$contact.Dispose()

$phoneWidth = 360
$phoneHeight = 640
$preview = New-ArgbBitmap -Width $phoneWidth -Height $phoneHeight
$pg = [System.Drawing.Graphics]::FromImage($preview)
try {
    $pg.Clear([System.Drawing.Color]::FromArgb(255, 21, 23, 19))
    $pg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $titleFont = [System.Drawing.Font]::new("Arial", 15, [System.Drawing.FontStyle]::Bold)
    $smallFont = [System.Drawing.Font]::new("Arial", 8, [System.Drawing.FontStyle]::Regular)
    $textBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 255, 240, 189))
    $linePen = [System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(160, 52, 176, 138), 1)
    try {
        $pg.DrawString("Starter Live-Lite animation preview", $titleFont, $textBrush, 18, 20)
        $pg.DrawRectangle($linePen, 24, 70, 312, 190)
        $pg.FillRectangle([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 43, 55, 47)), 25, 71, 310, 188)
        $baselineY = 176
        for ($h = 0; $h -lt $Heroes.Count; $h++) {
            $hero = $Heroes[$h]
            $walk = [System.Drawing.Bitmap]::FromFile((Resolve-Path $sheetPathsByKey["$($hero.Id)/walk"]))
            try {
                Draw-PreviewFrame -Graphics $pg -Image $walk -SourceX ($CellSize * (($h + 1) % 6)) -DestX (52 + ($h * 82)) -DestY ($baselineY - 60 + ($h % 2) * 4) -DestSize 64
                Draw-PreviewFrame -Graphics $pg -Image $walk -SourceX ($CellSize * (($h + 3) % 6)) -DestX (60 + ($h * 82)) -DestY 288 -DestSize 48
            } finally {
                $walk.Dispose()
            }
            $pg.DrawString($hero.Name, $smallFont, $textBrush, 54 + ($h * 82), 242)
        }
        $pg.DrawString("64px lane preview", $smallFont, $textBrush, 24, 266)
        $pg.DrawString("48px readability check", $smallFont, $textBrush, 24, 350)
    } finally {
        $titleFont.Dispose()
        $smallFont.Dispose()
        $textBrush.Dispose()
        $linePen.Dispose()
    }
} finally {
    $pg.Dispose()
}
$previewPath = Join-Path $ReviewDir "starter-hero-animation-phone-preview.png"
$preview.Save((Join-Path (Get-Location) $previewPath), [System.Drawing.Imaging.ImageFormat]::Png)
$preview.Dispose()

Write-Host "Generated starter hero animation sheets:"
foreach ($path in $generated) { Write-Host "  $path" }
Write-Host "Review contact sheet: $contactPath"
Write-Host "Phone preview: $previewPath"