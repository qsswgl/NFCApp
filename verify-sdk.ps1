# Verify Android SDK Installation

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   Checking Android SDK Installation" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"

Write-Host "Checking SDK at:" -ForegroundColor Yellow
Write-Host "  $sdkPath" -ForegroundColor White
Write-Host ""

if (Test-Path $sdkPath) {
    Write-Host "[OK] SDK directory exists" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "SDK Components:" -ForegroundColor Yellow
    
    # Check Platform Tools
    if (Test-Path "$sdkPath\platform-tools\adb.exe") {
        Write-Host "  [OK] Platform Tools" -ForegroundColor Green
    } else {
        Write-Host "  [X] Platform Tools missing" -ForegroundColor Red
    }
    
    # Check Build Tools
    if (Test-Path "$sdkPath\build-tools\") {
        Write-Host "  [OK] Build Tools" -ForegroundColor Green
        $buildTools = Get-ChildItem "$sdkPath\build-tools" | Select-Object -First 3
        foreach ($bt in $buildTools) {
            Write-Host "      - $($bt.Name)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "  [X] Build Tools missing" -ForegroundColor Red
    }
    
    # Check Platforms
    if (Test-Path "$sdkPath\platforms\") {
        Write-Host "  [OK] Android Platforms" -ForegroundColor Green
        $platforms = Get-ChildItem "$sdkPath\platforms"
        foreach ($p in $platforms) {
            Write-Host "      - $($p.Name)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "  [X] Android Platforms missing" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "   Creating local.properties..." -ForegroundColor Yellow
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    
    # Create local.properties
    $propsFile = "k:\NFC\NFCApp\local.properties"
    $sdkPathEscaped = $sdkPath -replace '\\', '\\'
    
    "sdk.dir=$sdkPathEscaped" | Out-File -FilePath $propsFile -Encoding utf8
    
    Write-Host "[OK] Created local.properties" -ForegroundColor Green
    Write-Host "     File: $propsFile" -ForegroundColor Cyan
    Write-Host "     SDK Path: $sdkPath" -ForegroundColor Cyan
    Write-Host ""
    
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "   Setting ANDROID_HOME..." -ForegroundColor Yellow
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    
    # Set ANDROID_HOME
    try {
        [Environment]::SetEnvironmentVariable("ANDROID_HOME", $sdkPath, "User")
        Write-Host "[OK] ANDROID_HOME set to:" -ForegroundColor Green
        Write-Host "     $sdkPath" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "[INFO] Please restart terminal for ANDROID_HOME to take effect" -ForegroundColor Yellow
    } catch {
        Write-Host "[WARN] Could not set ANDROID_HOME" -ForegroundColor Yellow
        Write-Host "       You may need to set it manually" -ForegroundColor Gray
    }
    
} else {
    Write-Host "[X] SDK directory not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Expected location: $sdkPath" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Please check Android Studio:" -ForegroundColor Yellow
    Write-Host "  1. Open Android Studio" -ForegroundColor White
    Write-Host "  2. Go to: Tools > SDK Manager" -ForegroundColor White
    Write-Host "  3. Check SDK Location" -ForegroundColor White
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
