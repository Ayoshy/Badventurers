param(
    [string] $CsvPath = "docs/data/items.csv",
    [string] $ResourceDir = "app/src/main/res-loot/drawable-nodpi"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Resolve-RepoPath {
    param([string] $Path)

    if ([System.IO.Path]::IsPathRooted($Path)) {
        return $Path
    }

    return Join-Path -Path (Get-Location) -ChildPath $Path
}

function Add-Failure {
    param(
        [System.Collections.Generic.List[string]] $Failures,
        [string] $Message
    )

    [void] $Failures.Add($Message)
}

function Test-LootPng {
    param(
        [string] $Path,
        [string] $FileName,
        [System.Collections.Generic.List[string]] $Failures
    )

    $bitmap = $null
    try {
        $bitmap = New-Object System.Drawing.Bitmap $Path
    } catch {
        Add-Failure $Failures "${FileName}: could not be opened as an image. $($_.Exception.Message)"
        return
    }

    try {
        if ($bitmap.Width -ne 128 -or $bitmap.Height -ne 128) {
            Add-Failure $Failures "${FileName}: expected 128x128, found $($bitmap.Width)x$($bitmap.Height)."
        }

        $hasAlpha = ($bitmap.Flags -band [int][System.Drawing.Imaging.ImageFlags]::HasAlpha) -ne 0
        if (-not $hasAlpha) {
            Add-Failure $Failures "${FileName}: PNG does not report an alpha channel."
        }

        $cornerAlpha = @(
            $bitmap.GetPixel(0, 0).A,
            $bitmap.GetPixel($bitmap.Width - 1, 0).A,
            $bitmap.GetPixel(0, $bitmap.Height - 1).A,
            $bitmap.GetPixel($bitmap.Width - 1, $bitmap.Height - 1).A
        )

        $opaqueCorners = @($cornerAlpha | Where-Object { $_ -ne 0 })
        if ($opaqueCorners.Count -gt 0) {
            Add-Failure $Failures "${FileName}: all four corner pixels must be fully transparent; found alpha values $($cornerAlpha -join ', ')."
        }
    } finally {
        $bitmap.Dispose()
    }
}

$csvFullPath = Resolve-RepoPath $CsvPath
$resourceFullPath = Resolve-RepoPath $ResourceDir
$failures = [System.Collections.Generic.List[string]]::new()
$warnings = [System.Collections.Generic.List[string]]::new()

if (-not (Test-Path -LiteralPath $csvFullPath -PathType Leaf)) {
    Add-Failure $failures "Missing CSV catalog: $CsvPath"
}

if (-not (Test-Path -LiteralPath $resourceFullPath -PathType Container)) {
    Add-Failure $failures "Missing resource directory: $ResourceDir"
}

if ($failures.Count -eq 0) {
    Add-Type -AssemblyName System.Drawing

    $items = @(Import-Csv -LiteralPath $csvFullPath -Delimiter ';')
    $commonRows = @($items | Where-Object { $_.rarity -eq "Common" })

    if ($commonRows.Count -ne 25) {
        Add-Failure $failures "Expected exactly 25 Common rows in $CsvPath, found $($commonRows.Count)."
    }

    $duplicates = @($commonRows | Group-Object -Property id | Where-Object { $_.Count -gt 1 })
    foreach ($duplicate in $duplicates) {
        Add-Failure $failures "Duplicate Common item id in ${CsvPath}: $($duplicate.Name)"
    }

    $expectedFiles = @()
    foreach ($row in $commonRows) {
        if ([string]::IsNullOrWhiteSpace($row.id)) {
            Add-Failure $failures "Common item row has an empty id."
            continue
        }

        if ($row.id -notmatch '^[a-z0-9_]+$') {
            Add-Failure $failures "Common item id '$($row.id)' is not Android resource-safe lowercase snake case."
            continue
        }

        $expectedFiles += "loot_art_$($row.id).png"
    }

    Write-Host "Expected Common loot art files ($($expectedFiles.Count)):"
    foreach ($fileName in $expectedFiles) {
        Write-Host "  $fileName"
    }

    $expectedSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
    foreach ($fileName in $expectedFiles) {
        [void] $expectedSet.Add($fileName)
    }

    $actualLootArtFiles = @(Get-ChildItem -LiteralPath $resourceFullPath -File -Filter "loot_art_*.png" | Select-Object -ExpandProperty Name)
    foreach ($actualFile in $actualLootArtFiles) {
        if (-not $expectedSet.Contains($actualFile)) {
            [void] $warnings.Add("Unexpected loot_art PNG not part of the Common BV-024 set: $actualFile")
        }
    }

    foreach ($fileName in $expectedFiles) {
        $assetPath = Join-Path -Path $resourceFullPath -ChildPath $fileName
        if (-not (Test-Path -LiteralPath $assetPath -PathType Leaf)) {
            Add-Failure $failures "Missing Common loot art file: $ResourceDir/$fileName"
            continue
        }

        Test-LootPng -Path $assetPath -FileName $fileName -Failures $failures
    }
}

foreach ($warning in $warnings) {
    Write-Warning $warning
}

if ($failures.Count -gt 0) {
    Write-Host ""
    Write-Host "Loot art validation failed:"
    foreach ($failure in $failures) {
        Write-Host "  - $failure"
    }
    exit 1
}

Write-Host ""
Write-Host "Loot art validation passed: checked $($expectedFiles.Count) Common PNG assets."
