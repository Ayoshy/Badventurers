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

$expectedFiles = @()

if ($failures.Count -eq 0) {
    Add-Type -AssemblyName System.Drawing

    $items = @(Import-Csv -LiteralPath $csvFullPath -Delimiter ';')
    $acceptedRows = @($items | Where-Object { $_.art_status -match '\(accepted BV-[0-9]+\)' })

    if ($acceptedRows.Count -eq 0) {
        Add-Failure $failures "No accepted loot art rows found in $CsvPath."
    }

    $duplicates = @($acceptedRows | Group-Object -Property id | Where-Object { $_.Count -gt 1 })
    foreach ($duplicate in $duplicates) {
        Add-Failure $failures "Duplicate accepted item id in ${CsvPath}: $($duplicate.Name)"
    }

    foreach ($row in $acceptedRows) {
        if ([string]::IsNullOrWhiteSpace($row.id)) {
            Add-Failure $failures "Accepted item row has an empty id."
            continue
        }

        if ($row.id -notmatch '^[a-z0-9_]+$') {
            Add-Failure $failures "Accepted item id '$($row.id)' is not Android resource-safe lowercase snake case."
            continue
        }

        $fileName = "loot_art_$($row.id).png"
        $expectedPath = "$ResourceDir/$fileName"
        if ($row.art_status -notlike "*$expectedPath*") {
            Add-Failure $failures "$($row.id): accepted art_status must reference $expectedPath."
        }
        $expectedFiles += $fileName
    }

    Write-Host "Expected accepted loot art files ($($expectedFiles.Count)):"
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
            [void] $warnings.Add("Unexpected loot_art PNG not marked accepted in ${CsvPath}: $actualFile")
        }
    }

    foreach ($fileName in $expectedFiles) {
        $assetPath = Join-Path -Path $resourceFullPath -ChildPath $fileName
        if (-not (Test-Path -LiteralPath $assetPath -PathType Leaf)) {
            Add-Failure $failures "Missing accepted loot art file: $ResourceDir/$fileName"
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
Write-Host "Loot art validation passed: checked $($expectedFiles.Count) accepted PNG assets."