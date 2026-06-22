#!/usr/bin/env pwsh
[CmdletBinding()]
param(
    [string]$DeviceSerial,
    [string]$OutputDir
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Resolve-ProjectPath {
    param(
        [Parameter(Mandatory = $true)]
        [string]$PathValue,
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot
    )

    if ([System.IO.Path]::IsPathRooted($PathValue)) {
        return $PathValue
    }

    return (Join-Path -Path $ProjectRoot -ChildPath $PathValue)
}

function Get-ReadyDevices {
    $rawLines = & adb devices 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw 'adb devices failed.'
    }

    $devices = New-Object System.Collections.Generic.List[object]
    foreach ($line in $rawLines | Select-Object -Skip 1) {
        if ([string]::IsNullOrWhiteSpace($line)) {
            continue
        }

        if ($line -match '^(?<serial>\S+)\s+(?<state>\S+)$') {
            if ($Matches.state -eq 'device') {
                $devices.Add([pscustomobject]@{
                    Serial = $Matches.serial
                    State  = $Matches.state
                })
            }
        }
    }

    return $devices
}

$projectRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $resolvedOutputDir = Join-Path -Path $projectRoot -ChildPath 'artifacts\screenshots'
} else {
    $resolvedOutputDir = Resolve-ProjectPath -PathValue $OutputDir -ProjectRoot $projectRoot
}

if (-not (Get-Command adb -ErrorAction SilentlyContinue)) {
    throw 'adb is not available on PATH.'
}

$readyDevices = @(Get-ReadyDevices)
if ($readyDevices.Count -eq 0) {
    throw 'No adb device is currently in the "device" state.'
}

$selectedDevice = $null
if ($DeviceSerial) {
    $selectedDevice = $readyDevices | Where-Object { $_.Serial -eq $DeviceSerial } | Select-Object -First 1
    if (-not $selectedDevice) {
        $known = ($readyDevices | ForEach-Object { $_.Serial }) -join ', '
        throw "Device '$DeviceSerial' is not in the ready device list: $known"
    }
} elseif ($readyDevices.Count -eq 1) {
    $selectedDevice = $readyDevices[0]
} else {
    $known = ($readyDevices | ForEach-Object { $_.Serial }) -join ', '
    throw "Multiple ready devices detected. Pass -DeviceSerial. Ready devices: $known"
}

New-Item -ItemType Directory -Path $resolvedOutputDir -Force | Out-Null

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$finalFile = Join-Path -Path $resolvedOutputDir -ChildPath "emulator-$timestamp.png"
$remoteFile = '/sdcard/badventurers-screen.png'

& adb -s $selectedDevice.Serial shell screencap -p $remoteFile
if ($LASTEXITCODE -ne 0) {
    throw "adb screencap failed for device '$($selectedDevice.Serial)'."
}

& adb -s $selectedDevice.Serial pull $remoteFile $finalFile | Out-Null
if ($LASTEXITCODE -ne 0) {
    throw "adb pull failed for device '$($selectedDevice.Serial)'."
}

Write-Host $finalFile


