<#
.SYNOPSIS
Installs the repository Copilot instructions into the current user's
GitHub Copilot IntelliJ config directory (%LOCALAPPDATA%\github-copilot\intellij).

This script makes a timestamped backup of any existing file and then copies
`.github/instructions/copilot-instructions.md` from the repository root into
the local Copilot config directory as `copilot-instructions.md`.

Usage (PowerShell, from repository root):
    ./.github/scripts/install-copilot-instructions.ps1

This script is safe to run multiple times.
#>

try {
    # Resolve repository root reliably even when script is dot-sourced
    $scriptDef = $MyInvocation.MyCommand.Definition
    if ([string]::IsNullOrEmpty($scriptDef)) {
        $repoRoot = (Get-Location).ProviderPath
    }
    else {
        $scriptFolder = Split-Path -Parent $scriptDef
        # If script is under .github\scripts, move up two levels to repo root
        $parentFolder = Split-Path -Parent $scriptFolder
        if ((Split-Path $scriptFolder -Leaf) -ieq 'scripts' -and (Split-Path $parentFolder -Leaf) -ieq '.github') {
            $repoRoot = Split-Path -Parent $parentFolder
        }
        else {
            $repoRoot = Split-Path -Parent $scriptFolder
        }
    }

    $source = Join-Path $repoRoot '.github\instructions\copilot-instructions.md'

    if (-not (Test-Path $source)) {
        Write-Error "Source instructions file not found: $source"
        exit 2
    }

    # Determine local Copilot config directory with fallback
    if ($env:LOCALAPPDATA) {
        $localDir = Join-Path $env:LOCALAPPDATA 'github-copilot\intellij'
    }
    else {
        $localDir = Join-Path (Join-Path $env:USERPROFILE 'AppData\Local') 'github-copilot\intellij'
    }

    if (-not (Test-Path $localDir)) {
        New-Item -ItemType Directory -Path $localDir -Force | Out-Null
        Write-Host "Created directory: $localDir"
    }

    $dest = Join-Path $localDir 'copilot-instructions.md'

    # Backup existing file if present
    if (Test-Path $dest) {
        $timestamp = (Get-Date).ToString('yyyyMMdd_HHmmss')
        $backup = "$dest.$timestamp.bak"
        Copy-Item -Path $dest -Destination $backup -Force
        Write-Host "Backed up existing file to: $backup"
    }

    Copy-Item -Path $source -Destination $dest -Force
    Write-Host "Copied $source -> $dest"
    Write-Host "Done. Restart your JetBrains IDE or reload the GitHub Copilot plugin to apply instructions."
    exit 0
}
catch {
    Write-Error "Failed to install copilot instructions: $($_.Exception.Message)"
    exit 1
}