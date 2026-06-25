param(
    [string] $SourcePath = "artifacts/loot-art/bv025/bv025-generated-source.png",
    [string] $ResourceDir = "app/src/main/res-loot/drawable-nodpi",
    [string] $ContactSheetPath = "docs/content/uncommon-rare-loot-art-contact-sheet.png"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

function Resolve-RepoPath {
    param([string] $Path)

    if ([System.IO.Path]::IsPathRooted($Path)) {
        return $Path
    }

    return Join-Path -Path (Get-Location) -ChildPath $Path
}

function New-TransparentBitmap {
    param(
        [int] $Width,
        [int] $Height
    )

    $bitmap = New-Object System.Drawing.Bitmap $Width, $Height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.Color]::Transparent)
    } finally {
        $graphics.Dispose()
    }
    return $bitmap
}

function Test-ChromaGreen {
    param([System.Drawing.Color] $Color)

    return $Color.G -ge 150 -and $Color.G -gt ($Color.R * 1.35) -and $Color.G -gt ($Color.B * 1.35)
}

function Remove-ChromaKey {
    param([System.Drawing.Bitmap] $Bitmap)

    $output = New-TransparentBitmap -Width $Bitmap.Width -Height $Bitmap.Height
    for ($y = 0; $y -lt $Bitmap.Height; $y++) {
        for ($x = 0; $x -lt $Bitmap.Width; $x++) {
            $pixel = $Bitmap.GetPixel($x, $y)
            if (Test-ChromaGreen -Color $pixel) {
                $output.SetPixel($x, $y, [System.Drawing.Color]::Transparent)
            } else {
                $output.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $pixel.R, $pixel.G, $pixel.B))
            }
        }
    }
    return $output
}

function Get-OpaqueBounds {
    param([System.Drawing.Bitmap] $Bitmap)

    $minX = $Bitmap.Width
    $minY = $Bitmap.Height
    $maxX = -1
    $maxY = -1

    for ($y = 0; $y -lt $Bitmap.Height; $y++) {
        for ($x = 0; $x -lt $Bitmap.Width; $x++) {
            if ($Bitmap.GetPixel($x, $y).A -gt 0) {
                if ($x -lt $minX) { $minX = $x }
                if ($y -lt $minY) { $minY = $y }
                if ($x -gt $maxX) { $maxX = $x }
                if ($y -gt $maxY) { $maxY = $y }
            }
        }
    }

    if ($maxX -lt 0 -or $maxY -lt 0) {
        return [System.Drawing.Rectangle]::new(0, 0, $Bitmap.Width, $Bitmap.Height)
    }

    return [System.Drawing.Rectangle]::new($minX, $minY, ($maxX - $minX + 1), ($maxY - $minY + 1))
}

function Export-Icon {
    param(
        [System.Drawing.Bitmap] $Sheet,
        [int] $Index,
        [string] $Id,
        [string] $Name,
        [string] $Rarity,
        [string] $ResourceDir
    )

    $cellWidth = [int][Math]::Floor($Sheet.Width / 4)
    $cellHeight = [int][Math]::Floor($Sheet.Height / 4)
    $col = $Index % 4
    $row = [int][Math]::Floor($Index / 4)
    $cell = [System.Drawing.Rectangle]::new($col * $cellWidth, $row * $cellHeight, $cellWidth, $cellHeight)
    $cellBitmap = $Sheet.Clone($cell, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $keyed = Remove-ChromaKey -Bitmap $cellBitmap
    $bounds = Get-OpaqueBounds -Bitmap $keyed

    $asset = New-TransparentBitmap -Width 128 -Height 128
    $graphics = [System.Drawing.Graphics]::FromImage($asset)
    try {
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None
        $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality

        $maxSize = 112.0
        $scale = [Math]::Min($maxSize / $bounds.Width, $maxSize / $bounds.Height)
        $drawWidth = [int][Math]::Round($bounds.Width * $scale)
        $drawHeight = [int][Math]::Round($bounds.Height * $scale)
        $drawX = [int][Math]::Round((128 - $drawWidth) / 2)
        $drawY = [int][Math]::Round((128 - $drawHeight) / 2)
        $target = [System.Drawing.Rectangle]::new($drawX, $drawY, $drawWidth, $drawHeight)
        $graphics.DrawImage($keyed, $target, $bounds, [System.Drawing.GraphicsUnit]::Pixel)
    } finally {
        $graphics.Dispose()
        $cellBitmap.Dispose()
        $keyed.Dispose()
    }

    $fileName = "loot_art_$Id.png"
    $outPath = Join-Path -Path $ResourceDir -ChildPath $fileName
    $asset.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $asset.Dispose()

    Write-Host "Exported $fileName ($Rarity - $Name)"
}

function Draw-ContactSheet {
    param(
        [array] $Items,
        [string] $ResourceDir,
        [string] $OutputPath
    )

    $cellWidth = 220
    $cellHeight = 184
    $sheet = New-Object System.Drawing.Bitmap ($cellWidth * 4), ($cellHeight * 4), ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($sheet)
    $font = New-Object System.Drawing.Font "Segoe UI", 9
    $smallFont = New-Object System.Drawing.Font "Segoe UI", 8
    $textBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(246, 235, 204))
    $subBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(180, 207, 224))
    $uncommonBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(36, 71, 64))
    $rareBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(42, 50, 87))
    $gridPen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(88, 80, 61)), 1

    try {
        $graphics.Clear([System.Drawing.Color]::FromArgb(21, 22, 18))
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
        for ($i = 0; $i -lt $Items.Count; $i++) {
            $item = $Items[$i]
            $col = $i % 4
            $row = [int][Math]::Floor($i / 4)
            $x = $col * $cellWidth
            $y = $row * $cellHeight
            $bg = if ($item.Rarity -eq "Rare") { $rareBrush } else { $uncommonBrush }
            $graphics.FillRectangle($bg, $x + 8, $y + 8, $cellWidth - 16, $cellHeight - 16)
            $graphics.DrawRectangle($gridPen, $x + 8, $y + 8, $cellWidth - 16, $cellHeight - 16)

            $iconPath = Join-Path -Path $ResourceDir -ChildPath "loot_art_$($item.Id).png"
            $icon = [System.Drawing.Bitmap]::FromFile($iconPath)
            try {
                $graphics.DrawImage($icon, $x + 46, $y + 14, 128, 128)
            } finally {
                $icon.Dispose()
            }

            $graphics.DrawString($item.Name, $font, $textBrush, $x + 12, $y + 145)
            $graphics.DrawString("$($item.Rarity) / $($item.Slot)", $smallFont, $subBrush, $x + 12, $y + 164)
        }
    } finally {
        $graphics.Dispose()
        $font.Dispose()
        $smallFont.Dispose()
        $textBrush.Dispose()
        $subBrush.Dispose()
        $uncommonBrush.Dispose()
        $rareBrush.Dispose()
        $gridPen.Dispose()
    }

    $sheet.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $sheet.Dispose()
}

$items = @(
    [pscustomobject]@{ Id = "weapon_receipt_cutter"; Name = "Receipt Cutter"; Rarity = "Uncommon"; Slot = "Weapon" },
    [pscustomobject]@{ Id = "weapon_mop_halberd"; Name = "Mop Halberd"; Rarity = "Uncommon"; Slot = "Weapon" },
    [pscustomobject]@{ Id = "armor_buttoned_barrel"; Name = "Buttoned Barrel"; Rarity = "Uncommon"; Slot = "Armor" },
    [pscustomobject]@{ Id = "footwear_squeaky_greaves"; Name = "Squeaky Greaves"; Rarity = "Uncommon"; Slot = "Footwear" },
    [pscustomobject]@{ Id = "trinket_queue_token"; Name = "Queue Token"; Rarity = "Uncommon"; Slot = "Trinket" },
    [pscustomobject]@{ Id = "headgear_bucket_visor"; Name = "Bucket Visor"; Rarity = "Uncommon"; Slot = "Headgear" },
    [pscustomobject]@{ Id = "consumable_warmish_tea"; Name = "Warmish Tea"; Rarity = "Uncommon"; Slot = "Consumable" },
    [pscustomobject]@{ Id = "consumable_pickle_potion"; Name = "Pickle Potion"; Rarity = "Uncommon"; Slot = "Consumable" },
    [pscustomobject]@{ Id = "weapon_fine_print_rapier"; Name = "Fine Print Rapier"; Rarity = "Rare"; Slot = "Weapon" },
    [pscustomobject]@{ Id = "weapon_taxman_gavel"; Name = "Taxman Gavel"; Rarity = "Rare"; Slot = "Weapon" },
    [pscustomobject]@{ Id = "armor_invoice_mail"; Name = "Invoice Mail"; Rarity = "Rare"; Slot = "Armor" },
    [pscustomobject]@{ Id = "footwear_witness_slippers"; Name = "Witness Slippers"; Rarity = "Rare"; Slot = "Footwear" },
    [pscustomobject]@{ Id = "trinket_overtime_hourglass"; Name = "Overtime Hourglass"; Rarity = "Rare"; Slot = "Trinket" },
    [pscustomobject]@{ Id = "headgear_notary_wig"; Name = "Notary Wig"; Rarity = "Rare"; Slot = "Headgear" },
    [pscustomobject]@{ Id = "consumable_bottled_courage"; Name = "Bottled Courage"; Rarity = "Rare"; Slot = "Consumable" },
    [pscustomobject]@{ Id = "trinket_minor_oracle_receipt"; Name = "Minor Oracle Receipt"; Rarity = "Rare"; Slot = "Trinket" }
)

$sourceFullPath = Resolve-RepoPath $SourcePath
$resourceFullPath = Resolve-RepoPath $ResourceDir
$contactSheetFullPath = Resolve-RepoPath $ContactSheetPath

New-Item -ItemType Directory -Force -Path $resourceFullPath | Out-Null
New-Item -ItemType Directory -Force -Path ([System.IO.Path]::GetDirectoryName($contactSheetFullPath)) | Out-Null

$sheet = [System.Drawing.Bitmap]::FromFile($sourceFullPath)
try {
    if ($sheet.Width -lt 512 -or $sheet.Height -lt 512) {
        throw "Source sprite sheet is too small: $($sheet.Width)x$($sheet.Height)."
    }

    Write-Host "Slicing BV-025 source sheet $($sheet.Width)x$($sheet.Height)."
    for ($i = 0; $i -lt $items.Count; $i++) {
        Export-Icon -Sheet $sheet -Index $i -Id $items[$i].Id -Name $items[$i].Name -Rarity $items[$i].Rarity -ResourceDir $resourceFullPath
    }
} finally {
    $sheet.Dispose()
}

Draw-ContactSheet -Items $items -ResourceDir $resourceFullPath -OutputPath $contactSheetFullPath
Write-Host "Generated $ContactSheetPath"
