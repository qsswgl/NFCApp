# Extract and setup Android SDK
$sdkRoot = "$env:LOCALAPPDATA\Android\Sdk"
$zip = "$env:TEMP\cmdtools.zip"
$cmdlineDir = "$sdkRoot\cmdline-tools"
$latestDir = "$cmdlineDir\latest"

Write-Host "Android SDK Setup" -ForegroundColor Cyan
Write-Host "================" -ForegroundColor Cyan
Write-Host ""

# Create directories
Write-Host "[1/5] Creating directories..." -ForegroundColor Yellow
if (-not (Test-Path $sdkRoot)) {
    New-Item -ItemType Directory -Path $sdkRoot -Force | Out-Null
}
if (-not (Test-Path $cmdlineDir)) {
    New-Item -ItemType Directory -Path $cmdlineDir -Force | Out-Null
}
Write-Host "      Done" -ForegroundColor Green

# Extract
Write-Host "[2/5] Extracting SDK tools..." -ForegroundColor Yellow
Expand-Archive -Path $zip -DestinationPath $cmdlineDir -Force
if (Test-Path "$cmdlineDir\cmdline-tools") {
    if (Test-Path $latestDir) {
        Remove-Item -Path $latestDir -Recurse -Force
    }
    Move-Item -Path "$cmdlineDir\cmdline-tools" -Destination $latestDir -Force
}
Write-Host "      Done" -ForegroundColor Green

# Set environment variables
Write-Host "[3/5] Setting environment variables..." -ForegroundColor Yellow
try {
    [Environment]::SetEnvironmentVariable("ANDROID_HOME", $sdkRoot, "Machine")
    Write-Host "      ANDROID_HOME = $sdkRoot" -ForegroundColor Green
} catch {
    Write-Host "      Warning: Could not set ANDROID_HOME" -ForegroundColor Yellow
}

# Update PATH
try {
    $currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
    if ($currentPath -notlike "*$sdkRoot*") {
        $newPath = $currentPath + ";$sdkRoot\platform-tools;$sdkRoot\cmdline-tools\latest\bin"
        [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
        Write-Host "      PATH updated" -ForegroundColor Green
    } else {
        Write-Host "      PATH already contains SDK" -ForegroundColor Green
    }
} catch {
    Write-Host "      Warning: Could not update PATH" -ForegroundColor Yellow
}

# Set for current session
$env:ANDROID_HOME = $sdkRoot
$env:PATH = "$sdkRoot\cmdline-tools\latest\bin;$sdkRoot\platform-tools;$env:PATH"

# Accept licenses
Write-Host "[4/5] Accepting SDK licenses..." -ForegroundColor Yellow
$sdkmanager = "$latestDir\bin\sdkmanager.bat"
if (Test-Path $sdkmanager) {
    $input = "y`ny`ny`ny`ny`ny`ny`ny`ny`n"
    $input | & $sdkmanager --licenses 2>&1 | Out-Null
    Write-Host "      Done" -ForegroundColor Green
} else {
    Write-Host "      Error: sdkmanager not found" -ForegroundColor Red
}

# Install packages
Write-Host "[5/5] Installing SDK packages..." -ForegroundColor Yellow
Write-Host "      This will take 5-10 minutes, please wait..." -ForegroundColor Gray
Write-Host ""

$packages = @(
    "platform-tools",
    "build-tools;34.0.0",
    "platforms;android-34"
)

foreach ($pkg in $packages) {
    Write-Host "      Installing $pkg..." -ForegroundColor Cyan
    & $sdkmanager $pkg 2>&1 | Out-Null
    Write-Host "      [OK] $pkg" -ForegroundColor Green
}

Write-Host ""
Write-Host "================" -ForegroundColor Cyan
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "================" -ForegroundColor Cyan
Write-Host ""
Write-Host "SDK Location: $sdkRoot" -ForegroundColor Cyan
Write-Host ""
Write-Host "Installed:" -ForegroundColor Yellow
Write-Host "  • Platform Tools" -ForegroundColor Gray
Write-Host "  • Build Tools 34.0.0" -ForegroundColor Gray  
Write-Host "  • Android 14 (API 34)" -ForegroundColor Gray
Write-Host ""
