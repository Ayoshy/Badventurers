param(
    [string] $ManifestPath = "docs/data/hero-animation-manifest.json",
    [string] $HeroCatalogPath = "docs/data/heroes.csv",
    [string] $ResourceDir = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RequiredStateIds = @("idle", "walk", "fight", "hurt_dead", "celebrate", "loot_interact")
$AllowedStatuses = @("accepted", "planned_bv044", "missing_bv045")

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

function Add-WarningMessage {
    param(
        [System.Collections.Generic.List[string]] $Warnings,
        [string] $Message
    )

    [void] $Warnings.Add($Message)
}

function Get-PropertyValue {
    param(
        $Object,
        [string] $Name
    )

    if ($null -eq $Object) {
        return $null
    }

    foreach ($property in $Object.PSObject.Properties) {
        if ($property.Name -eq $Name) {
            return $property.Value
        }
    }

    return $null
}

function Get-PropertyNames {
    param($Object)

    if ($null -eq $Object) {
        return @()
    }

    return @($Object.PSObject.Properties | ForEach-Object { $_.Name })
}

function Skip-JsonWhitespace {
    param(
        [string] $Text,
        [int] $Index
    )

    while ($Index -lt $Text.Length -and [char]::IsWhiteSpace($Text[$Index])) {
        $Index++
    }

    return $Index
}

function Read-JsonStringToken {
    param(
        [string] $Text,
        [int] $Start
    )

    if ($Start -ge $Text.Length -or $Text[$Start] -ne [char]34) {
        throw "Expected JSON string at character $Start."
    }

    $builder = [System.Text.StringBuilder]::new()
    $escaped = $false

    for ($i = $Start + 1; $i -lt $Text.Length; $i++) {
        $ch = $Text[$i]

        if ($escaped) {
            [void] $builder.Append($ch)
            $escaped = $false
            continue
        }

        if ($ch -eq [char]92) {
            $escaped = $true
            continue
        }

        if ($ch -eq [char]34) {
            return [pscustomobject]@{
                Value = $builder.ToString()
                End = $i
            }
        }

        [void] $builder.Append($ch)
    }

    throw "Unterminated JSON string starting at character $Start."
}

function Find-MatchingJsonBrace {
    param(
        [string] $Text,
        [int] $OpenIndex
    )

    if ($OpenIndex -ge $Text.Length -or $Text[$OpenIndex] -ne [char]123) {
        throw "Expected opening brace at character $OpenIndex."
    }

    $depth = 0
    $inString = $false
    $escaped = $false

    for ($i = $OpenIndex; $i -lt $Text.Length; $i++) {
        $ch = $Text[$i]

        if ($inString) {
            if ($escaped) {
                $escaped = $false
            } elseif ($ch -eq [char]92) {
                $escaped = $true
            } elseif ($ch -eq [char]34) {
                $inString = $false
            }
            continue
        }

        if ($ch -eq [char]34) {
            $inString = $true
            continue
        }

        if ($ch -eq [char]123) {
            $depth++
        } elseif ($ch -eq [char]125) {
            $depth--
            if ($depth -eq 0) {
                return $i
            }
        }
    }

    throw "Unterminated JSON object starting at character $OpenIndex."
}

function Get-TopLevelObjectKeys {
    param([string] $ObjectText)

    $keys = [System.Collections.Generic.List[string]]::new()
    $depth = 0

    for ($i = 0; $i -lt $ObjectText.Length; $i++) {
        $ch = $ObjectText[$i]

        if ($ch -eq [char]34) {
            $token = Read-JsonStringToken -Text $ObjectText -Start $i
            if ($depth -eq 1) {
                $next = Skip-JsonWhitespace -Text $ObjectText -Index ($token.End + 1)
                if ($next -lt $ObjectText.Length -and $ObjectText[$next] -eq [char]58) {
                    [void] $keys.Add($token.Value)
                }
            }
            $i = $token.End
            continue
        }

        if ($ch -eq [char]123) {
            $depth++
        } elseif ($ch -eq [char]125) {
            $depth--
        }
    }

    return $keys
}

function Get-TopLevelObjectPropertySlices {
    param([string] $ObjectText)

    $slices = [System.Collections.Generic.List[object]]::new()
    $depth = 0

    for ($i = 0; $i -lt $ObjectText.Length; $i++) {
        $ch = $ObjectText[$i]

        if ($ch -eq [char]34) {
            $token = Read-JsonStringToken -Text $ObjectText -Start $i
            if ($depth -eq 1) {
                $colon = Skip-JsonWhitespace -Text $ObjectText -Index ($token.End + 1)
                if ($colon -lt $ObjectText.Length -and $ObjectText[$colon] -eq [char]58) {
                    $valueStart = Skip-JsonWhitespace -Text $ObjectText -Index ($colon + 1)
                    if ($valueStart -lt $ObjectText.Length -and $ObjectText[$valueStart] -eq [char]123) {
                        $valueEnd = Find-MatchingJsonBrace -Text $ObjectText -OpenIndex $valueStart
                        [void] $slices.Add([pscustomobject]@{
                            Key = $token.Value
                            Json = $ObjectText.Substring($valueStart, $valueEnd - $valueStart + 1)
                        })
                        $i = $valueEnd
                        continue
                    }
                }
            }
            $i = $token.End
            continue
        }

        if ($ch -eq [char]123) {
            $depth++
        } elseif ($ch -eq [char]125) {
            $depth--
        }
    }

    return $slices
}

function Get-NamedTopLevelObjectSlice {
    param(
        [string] $ObjectText,
        [string] $Name
    )

    foreach ($slice in @(Get-TopLevelObjectPropertySlices -ObjectText $ObjectText)) {
        if ($slice.Key -eq $Name) {
            return $slice.Json
        }
    }

    return $null
}

function Add-DuplicateKeyFailures {
    param(
        [string] $ObjectText,
        [string] $Scope,
        [System.Collections.Generic.List[string]] $Failures
    )

    $duplicates = @(Get-TopLevelObjectKeys -ObjectText $ObjectText | Group-Object | Where-Object { $_.Count -gt 1 })
    foreach ($duplicate in $duplicates) {
        Add-Failure $Failures "$Scope contains duplicate JSON key '$($duplicate.Name)'."
    }
}

function Test-DuplicateJsonKeys {
    param(
        [string] $JsonText,
        [System.Collections.Generic.List[string]] $Failures
    )

    try {
        Add-DuplicateKeyFailures -ObjectText $JsonText -Scope "manifest root" -Failures $Failures

        $requiredStatesSlice = Get-NamedTopLevelObjectSlice -ObjectText $JsonText -Name "requiredStates"
        if ($null -ne $requiredStatesSlice) {
            Add-DuplicateKeyFailures -ObjectText $requiredStatesSlice -Scope "requiredStates" -Failures $Failures
        }

        $validStatusesSlice = Get-NamedTopLevelObjectSlice -ObjectText $JsonText -Name "validStatuses"
        if ($null -ne $validStatusesSlice) {
            Add-DuplicateKeyFailures -ObjectText $validStatusesSlice -Scope "validStatuses" -Failures $Failures
        }

        $heroesSlice = Get-NamedTopLevelObjectSlice -ObjectText $JsonText -Name "heroes"
        if ($null -ne $heroesSlice) {
            Add-DuplicateKeyFailures -ObjectText $heroesSlice -Scope "heroes" -Failures $Failures
            foreach ($heroSlice in @(Get-TopLevelObjectPropertySlices -ObjectText $heroesSlice)) {
                Add-DuplicateKeyFailures -ObjectText $heroSlice.Json -Scope "heroes.$($heroSlice.Key)" -Failures $Failures
                $statesSlice = Get-NamedTopLevelObjectSlice -ObjectText $heroSlice.Json -Name "states"
                if ($null -ne $statesSlice) {
                    Add-DuplicateKeyFailures -ObjectText $statesSlice -Scope "heroes.$($heroSlice.Key).states" -Failures $Failures
                }
            }
        }
    } catch {
        Add-Failure $Failures "Could not scan JSON duplicate keys: $($_.Exception.Message)"
    }
}

function Get-StateDisplayName {
    param([string] $StateId)

    switch ($StateId) {
        "hurt_dead" { return "hurt/dead" }
        "loot_interact" { return "loot/interact" }
        default { return $StateId }
    }
}

function Get-StatusCode {
    param([string] $Status)

    switch ($Status) {
        "accepted" { return "OK" }
        "planned_bv044" { return "PLAN" }
        "missing_bv045" { return "MISS" }
        default { return "BAD" }
    }
}

function Format-AuditCell {
    param(
        [string] $Text,
        [int] $Width
    )

    return ([string] $Text).PadRight($Width)
}

function Write-AuditGrid {
    param(
        [System.Collections.IEnumerable] $Rows,
        [string[]] $StateIds
    )

    $rowList = @($Rows)
    if ($rowList.Count -eq 0) {
        Write-Host "Contact-sheet placeholder: no rows to display."
        return
    }

    $widths = @{}
    $widths["hero"] = 24
    foreach ($stateId in $StateIds) {
        $widths[$stateId] = [Math]::Max((Get-StateDisplayName -StateId $stateId).Length, 4)
    }

    foreach ($row in $rowList) {
        $heroText = [string] $row["hero"]
        $widths["hero"] = [Math]::Max($widths["hero"], $heroText.Length)
        foreach ($stateId in $StateIds) {
            $stateText = [string] $row[$stateId]
            $widths[$stateId] = [Math]::Max($widths[$stateId], $stateText.Length)
        }
    }

    Write-Host ""
    Write-Host "Contact-sheet placeholder (text only):"
    $header = Format-AuditCell -Text "hero" -Width $widths["hero"]
    foreach ($stateId in $StateIds) {
        $header += "  " + (Format-AuditCell -Text (Get-StateDisplayName -StateId $stateId) -Width $widths[$stateId])
    }
    Write-Host $header
    Write-Host ("-" * $header.Length)

    foreach ($row in $rowList) {
        $line = Format-AuditCell -Text ([string] $row["hero"]) -Width $widths["hero"]
        foreach ($stateId in $StateIds) {
            $line += "  " + (Format-AuditCell -Text ([string] $row[$stateId]) -Width $widths[$stateId])
        }
        Write-Host $line
    }
}

$manifestFullPath = Resolve-RepoPath $ManifestPath
$catalogFullPath = Resolve-RepoPath $HeroCatalogPath
$failures = [System.Collections.Generic.List[string]]::new()
$warnings = [System.Collections.Generic.List[string]]::new()
$auditRows = [System.Collections.Generic.List[object]]::new()
$statusCounts = @{}
foreach ($status in $AllowedStatuses) {
    $statusCounts[$status] = 0
}
$acceptedCount = 0

if (-not (Test-Path -LiteralPath $manifestFullPath -PathType Leaf)) {
    Add-Failure $failures "Missing hero animation manifest: $ManifestPath"
}

if (-not (Test-Path -LiteralPath $catalogFullPath -PathType Leaf)) {
    Add-Failure $failures "Missing hero catalog CSV: $HeroCatalogPath"
}

$manifest = $null
$catalogHeroes = @()
$rawManifest = ""

if (Test-Path -LiteralPath $manifestFullPath -PathType Leaf) {
    $rawManifest = [System.IO.File]::ReadAllText($manifestFullPath)
    Test-DuplicateJsonKeys -JsonText $rawManifest -Failures $failures
    try {
        $manifest = $rawManifest | ConvertFrom-Json
    } catch {
        Add-Failure $failures "Manifest is not valid JSON: $($_.Exception.Message)"
    }
}

if (Test-Path -LiteralPath $catalogFullPath -PathType Leaf) {
    $catalogHeroes = @(Import-Csv -LiteralPath $catalogFullPath -Delimiter ';')
    if ($catalogHeroes.Count -eq 0) {
        Add-Failure $failures "Hero catalog CSV has no hero rows: $HeroCatalogPath"
    }
}

$catalogById = @{}
foreach ($hero in $catalogHeroes) {
    $heroId = [string] $hero.id
    $catalogStatus = [string] $hero.animation_pack_status

    if ([string]::IsNullOrWhiteSpace($heroId)) {
        Add-Failure $failures "Hero catalog contains a row with an empty id."
        continue
    }

    if ($heroId -notmatch '^[a-z0-9_]+$') {
        Add-Failure $failures "Hero id '$heroId' is not Android resource-safe lowercase snake case."
    }

    if ($AllowedStatuses -notcontains $catalogStatus) {
        Add-Failure $failures "Hero catalog status '$catalogStatus' for '$heroId' is not one of: $($AllowedStatuses -join ', ')."
    }

    $catalogById[$heroId] = $hero
}

foreach ($duplicate in @($catalogHeroes | Group-Object -Property id | Where-Object { $_.Count -gt 1 })) {
    Add-Failure $failures "Duplicate hero id in ${HeroCatalogPath}: $($duplicate.Name)"
}

if ($null -ne $manifest) {
    $requiredStatesObject = Get-PropertyValue -Object $manifest -Name "requiredStates"
    $manifestStateIds = Get-PropertyNames -Object $requiredStatesObject
    if ($null -eq $requiredStatesObject) {
        Add-Failure $failures "Manifest is missing requiredStates."
    } else {
        foreach ($stateId in $RequiredStateIds) {
            if ($manifestStateIds -notcontains $stateId) {
                Add-Failure $failures "Manifest missing required state '$stateId'."
            }
        }
        foreach ($stateId in $manifestStateIds) {
            if ($RequiredStateIds -notcontains $stateId) {
                Add-Failure $failures "Manifest declares unknown required state '$stateId'."
            }
        }
    }

    $validStatusesObject = Get-PropertyValue -Object $manifest -Name "validStatuses"
    $manifestStatuses = Get-PropertyNames -Object $validStatusesObject
    if ($null -eq $validStatusesObject) {
        Add-Failure $failures "Manifest is missing validStatuses."
    } else {
        foreach ($status in $AllowedStatuses) {
            if ($manifestStatuses -notcontains $status) {
                Add-Failure $failures "Manifest missing valid status '$status'."
            }
        }
        foreach ($status in $manifestStatuses) {
            if ($AllowedStatuses -notcontains $status) {
                Add-Failure $failures "Manifest declares unknown status '$status'."
            }
        }
    }

    $locations = Get-PropertyValue -Object $manifest -Name "locations"
    $exportRoot = [string] (Get-PropertyValue -Object $locations -Name "androidExportRoot")
    $exportPattern = [string] (Get-PropertyValue -Object $locations -Name "androidExportPattern")
    if (-not [string]::IsNullOrWhiteSpace($ResourceDir)) {
        $exportRoot = $ResourceDir
    }

    if ([string]::IsNullOrWhiteSpace($exportRoot)) {
        Add-Failure $failures "Manifest locations.androidExportRoot is empty."
    }

    if ([string]::IsNullOrWhiteSpace($exportPattern)) {
        Add-Failure $failures "Manifest locations.androidExportPattern is empty."
    } elseif ($exportPattern -notlike "*{hero_id}*" -or $exportPattern -notlike "*{state}*") {
        Add-Failure $failures "Manifest androidExportPattern must include {hero_id} and {state} placeholders."
    }

    $manifestHeroesObject = Get-PropertyValue -Object $manifest -Name "heroes"
    $manifestHeroIds = Get-PropertyNames -Object $manifestHeroesObject
    if ($null -eq $manifestHeroesObject) {
        Add-Failure $failures "Manifest is missing heroes."
    }

    foreach ($heroId in $catalogById.Keys) {
        if ($manifestHeroIds -notcontains $heroId) {
            Add-Failure $failures "Manifest missing current hero id '$heroId'."
        }
    }

    foreach ($heroId in $manifestHeroIds) {
        if (-not $catalogById.ContainsKey($heroId)) {
            Add-Failure $failures "Manifest contains unknown hero id '$heroId'."
        }
    }

    $exportRootForManifest = $exportRoot -replace '[\\/]+$', ''

    foreach ($catalogHero in $catalogHeroes) {
        $heroId = [string] $catalogHero.id
        if ([string]::IsNullOrWhiteSpace($heroId)) {
            continue
        }

        $heroEntry = Get-PropertyValue -Object $manifestHeroesObject -Name $heroId
        if ($null -eq $heroEntry) {
            continue
        }

        $catalogStatus = [string] $catalogHero.animation_pack_status
        $packStatus = [string] (Get-PropertyValue -Object $heroEntry -Name "packStatus")
        if ([string]::IsNullOrWhiteSpace($packStatus)) {
            Add-Failure $failures "${heroId}: missing packStatus."
        } elseif ($AllowedStatuses -notcontains $packStatus) {
            Add-Failure $failures "${heroId}: unknown packStatus '$packStatus'."
        } elseif ($packStatus -ne $catalogStatus) {
            Add-Failure $failures "${heroId}: packStatus '$packStatus' does not match heroes.csv animation_pack_status '$catalogStatus'."
        }

        $manifestName = [string] (Get-PropertyValue -Object $heroEntry -Name "name")
        if (-not [string]::IsNullOrWhiteSpace($manifestName) -and $manifestName -ne [string] $catalogHero.name) {
            Add-WarningMessage $warnings "${heroId}: manifest name '$manifestName' differs from heroes.csv name '$($catalogHero.name)'."
        }

        $statesObject = Get-PropertyValue -Object $heroEntry -Name "states"
        if ($null -eq $statesObject) {
            Add-Failure $failures "${heroId}: missing states object."
            continue
        }

        $stateKeys = Get-PropertyNames -Object $statesObject
        foreach ($stateId in $RequiredStateIds) {
            if ($stateKeys -notcontains $stateId) {
                Add-Failure $failures "${heroId}: missing required state '$stateId'."
            }
        }
        foreach ($stateId in $stateKeys) {
            if ($RequiredStateIds -notcontains $stateId) {
                Add-Failure $failures "${heroId}: unknown state '$stateId'."
            }
        }

        $row = [ordered]@{ hero = $heroId }
        foreach ($stateId in $RequiredStateIds) {
            $stateEntry = Get-PropertyValue -Object $statesObject -Name $stateId
            if ($null -eq $stateEntry) {
                $row[$stateId] = "MISSING"
                continue
            }

            $stateStatus = [string] (Get-PropertyValue -Object $stateEntry -Name "status")
            if ([string]::IsNullOrWhiteSpace($stateStatus)) {
                Add-Failure $failures "${heroId}/${stateId}: missing status."
                $row[$stateId] = "BAD"
                continue
            }

            if ($AllowedStatuses -notcontains $stateStatus) {
                Add-Failure $failures "${heroId}/${stateId}: unknown status '$stateStatus'."
            } else {
                $statusCounts[$stateStatus] = [int] $statusCounts[$stateStatus] + 1
            }

            if (-not [string]::IsNullOrWhiteSpace($catalogStatus) -and $stateStatus -ne $catalogStatus) {
                Add-Failure $failures "${heroId}/${stateId}: status '$stateStatus' does not match heroes.csv animation_pack_status '$catalogStatus'."
            }

            $expectedFile = $exportPattern.Replace('{hero_id}', $heroId).Replace('{state}', $stateId)
            $expectedAsset = "$exportRootForManifest/$expectedFile"
            $actualAsset = [string] (Get-PropertyValue -Object $stateEntry -Name "asset")
            if ([string]::IsNullOrWhiteSpace($actualAsset)) {
                Add-Failure $failures "${heroId}/${stateId}: missing asset path."
            } elseif ($actualAsset -ne $expectedAsset) {
                Add-Failure $failures "${heroId}/${stateId}: asset '$actualAsset' should be '$expectedAsset'."
            }

            if ($stateStatus -eq "accepted") {
                $acceptedCount++
                $assetFullPath = Resolve-RepoPath $actualAsset
                if (-not (Test-Path -LiteralPath $assetFullPath -PathType Leaf)) {
                    Add-Failure $failures "${heroId}/${stateId}: accepted asset is missing: $actualAsset"
                }
            }

            $row[$stateId] = Get-StatusCode -Status $stateStatus
        }

        [void] $auditRows.Add($row)
    }
}

foreach ($warning in $warnings) {
    Write-Warning $warning
}

Write-Host "Hero animation manifest audit"
Write-Host "Manifest: $ManifestPath"
Write-Host "Hero catalog: $HeroCatalogPath"
Write-Host "Current heroes: $($catalogHeroes.Count)"
Write-Host "Required states: $($RequiredStateIds -join ', ')"
Write-Host "Status counts:"
foreach ($status in $AllowedStatuses) {
    Write-Host ("  {0}: {1}" -f $status, $statusCounts[$status])
}

if ($acceptedCount -eq 0) {
    Write-Host "Accepted asset check: 0 accepted states; no PNG files required yet."
} else {
    Write-Host "Accepted asset check: checked $acceptedCount accepted state asset(s)."
}

Write-AuditGrid -Rows $auditRows -StateIds $RequiredStateIds

if ($failures.Count -gt 0) {
    Write-Host ""
    Write-Host "Hero animation validation failed:"
    foreach ($failure in $failures) {
        Write-Host "  - $failure"
    }
    exit 1
}

Write-Host ""
Write-Host "Hero animation validation passed."
