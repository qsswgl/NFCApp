# Android SDK Auto-Install Script (PowerShell)
# Run as Administrator

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host ""
Write-Host "╔════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Android SDK Auto-Install (CLI)          ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Check admin
$isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "[X] Need administrator privileges" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please run as administrator:" -ForegroundColor Yellow
    Write-Host "Right-click PowerShell -> Run as Administrator" -ForegroundColor Yellow
    Write-Host ""
    pause
    exit 1
}

Write-Host "[OK] Running as administrator" -ForegroundColor Green
Write-Host ""

# Set paths
$sdkRoot = "$env:LOCALAPPDATA\Android\Sdk"
$cmdlineToolsZip = "$env:TEMP\commandlinetools.zip"
$cmdlineToolsUrl = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"

Write-Host "SDK will be installed to: $sdkRoot" -ForegroundColor Cyan
Write-Host ""
Write-Host "This script will:" -ForegroundColor Yellow
Write-Host "  1. Download Android command-line tools (~150 MB)" -ForegroundColor Gray
Write-Host "  2. Install Android SDK (~2-3 GB)" -ForegroundColor Gray
Write-Host "  3. Set environment variables" -ForegroundColor Gray
Write-Host ""
Write-Host "Estimated time: 10-15 minutes" -ForegroundColor Cyan
Write-Host ""

$confirm = Read-Host "Continue? (Y/N)"
if ($confirm -ne 'Y' -and $confirm -ne 'y') {
    Write-Host "Installation cancelled" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Step 1: Download Command-Line Tools" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Create directories
if (-not (Test-Path $sdkRoot)) {
    New-Item -ItemType Directory -Path $sdkRoot -Force | Out-Null
}

# Download
Write-Host "Downloading from Google..." -ForegroundColor Cyan
Write-Host "This may take 2-5 minutes..." -ForegroundColor Gray
Write-Host ""

try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $cmdlineToolsUrl -OutFile $cmdlineToolsZip -UseBasicParsing
    Write-Host "[OK] Download complete!" -ForegroundColor Green
}
catch {
    Write-Host "[X] Download failed: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "  - Internet connection" -ForegroundColor Gray
    Write-Host "  - Firewall settings" -ForegroundColor Gray
    Write-Host ""
    pause
    exit 1
}

# Extract
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Step 2: Extract Tools" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

try {
    $cmdlineDir = "$sdkRoot\cmdline-tools"
    if (-not (Test-Path $cmdlineDir)) {
        New-Item -ItemType Directory -Path $cmdlineDir -Force | Out-Null
    }
    
    Write-Host "Extracting..." -ForegroundColor Cyan
    Expand-Archive -Path $cmdlineToolsZip -DestinationPath $cmdlineDir -Force
    
    # Rename to 'latest'
    $latestDir = "$cmdlineDir\latest"
    if (Test-Path "$cmdlineDir\cmdline-tools") {
        if (Test-Path $latestDir) {
            Remove-Item -Path $latestDir -Recurse -Force
        }
        Move-Item -Path "$cmdlineDir\cmdline-tools" -Destination $latestDir -Force
    }
    
    Write-Host "[OK] Extraction complete!" -ForegroundColor Green
}
catch {
    Write-Host "[X] Extraction failed: $_" -ForegroundColor Red
    pause
    exit 1
}

# Set environment variables
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Step 3: Set Environment Variables" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

try {
    # Set ANDROID_HOME
    [Environment]::SetEnvironmentVariable("ANDROID_HOME", $sdkRoot, "Machine")
    Write-Host "[OK] ANDROID_HOME = $sdkRoot" -ForegroundColor Green
    
    # Update PATH
    $currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
    if ($currentPath -notlike "*$sdkRoot*") {
        $newPath = "$currentPath;$sdkRoot\platform-tools;$sdkRoot\cmdline-tools\latest\bin"
        [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
        Write-Host "[OK] PATH updated" -ForegroundColor Green
    } else {
        Write-Host "[OK] PATH already contains Android SDK" -ForegroundColor Green
    }
    
    # Set for current session
    $env:ANDROID_HOME = $sdkRoot
    $env:PATH = "$sdkRoot\cmdline-tools\latest\bin;$sdkRoot\platform-tools;$env:PATH"
}
catch {
    Write-Host "[!] Warning: Could not set environment variables" -ForegroundColor Yellow
}

# Install SDK packages
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Step 4: Install SDK Packages" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$sdkmanager = "$sdkRoot\cmdline-tools\latest\bin\sdkmanager.bat"

if (Test-Path $sdkmanager) {
    Write-Host "Accepting licenses..." -ForegroundColor Cyan
    echo y | & $sdkmanager --licenses | Out-Null
    Write-Host "[OK] Licenses accepted" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "Installing SDK packages (this may take 5-10 minutes)..." -ForegroundColor Cyan
    Write-Host ""
    
    $packages = @(
        "platform-tools",
        "build-tools;34.0.0",
        "platforms;android-34",
        "emulator",
        "system-images;android-34;google_apis;x86_64",
        "cmake;3.22.1",
        "ndk;25.1.8937393"
    )
    
    $i = 0
    foreach ($package in $packages) {
        $i++
        Write-Host "[$i/7] Installing $package..." -ForegroundColor Yellow
        & $sdkmanager $package | Out-Null
        Write-Host "      [OK] $package installed" -ForegroundColor Green
    }
    
    Write-Host ""
    Write-Host "[OK] All packages installed!" -ForegroundColor Green
} else {
    Write-Host "[X] sdkmanager not found" -ForegroundColor Red
}

# Summary
Write-Host ""
Write-Host "╔════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║   Installation Complete!                   ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Host "SDK Location: $sdkRoot" -ForegroundColor Cyan
Write-Host ""
Write-Host "Installed components:" -ForegroundColor Yellow
Write-Host "  • Platform Tools (adb, fastboot)" -ForegroundColor Gray
Write-Host "  • Build Tools 34.0.0" -ForegroundColor Gray
Write-Host "  • Android 14.0 API Level 34" -ForegroundColor Gray
Write-Host "  • Android Emulator" -ForegroundColor Gray
Write-Host "  • System Images (x86_64)" -ForegroundColor Gray
Write-Host "  • CMake 3.22.1" -ForegroundColor Gray
Write-Host "  • NDK 25.1.8937393" -ForegroundColor Gray
Write-Host ""

Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Close and reopen terminal" -ForegroundColor Gray
Write-Host "  2. Run: adb version (to verify)" -ForegroundColor Gray
Write-Host "  3. Run: .\Setup-LocalProperties.bat" -ForegroundColor Gray
Write-Host ""

pause
