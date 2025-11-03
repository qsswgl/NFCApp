# Re-extract SDK tools correctly
$sdkRoot = "$env:LOCALAPPDATA\Android\Sdk"
$zip = "$env:TEMP\cmdtools.zip"
$cmdlineDir = "$sdkRoot\cmdline-tools"
$latestDir = "$cmdlineDir\latest"

Write-Host "Re-extracting Android SDK tools..." -ForegroundColor Cyan
Write-Host ""

# Clean up first
if (Test-Path $cmdlineDir) {
    Write-Host "Removing old files..." -ForegroundColor Yellow
    Remove-Item -Path $cmdlineDir -Recurse -Force
}

# Create directory
New-Item -ItemType Directory -Path $cmdlineDir -Force | Out-Null

# Extract
Write-Host "Extracting..." -ForegroundColor Yellow
Expand-Archive -Path $zip -DestinationPath $cmdlineDir -Force

Write-Host "Checking extracted files..." -ForegroundColor Yellow
Get-ChildItem $cmdlineDir | ForEach-Object {
    Write-Host "  Found: $($_.Name)" -ForegroundColor Gray
}

# Rename if needed
if (Test-Path "$cmdlineDir\cmdline-tools") {
    Write-Host ""
    Write-Host "Renaming to 'latest'..." -ForegroundColor Yellow
    if (Test-Path $latestDir) {
        Remove-Item -Path $latestDir -Recurse -Force
    }
    Rename-Item -Path "$cmdlineDir\cmdline-tools" -NewName "latest"
    Write-Host "[OK] Renamed to latest" -ForegroundColor Green
}

# Verify
Write-Host ""
Write-Host "Verification:" -ForegroundColor Cyan
$sdkmanager = "$latestDir\bin\sdkmanager.bat"
if (Test-Path $sdkmanager) {
    Write-Host "[OK] sdkmanager found at: $sdkmanager" -ForegroundColor Green
} else {
    Write-Host "[X] sdkmanager NOT found" -ForegroundColor Red
    Write-Host "Expected at: $sdkmanager" -ForegroundColor Gray
}

Write-Host ""
