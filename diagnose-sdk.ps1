# Diagnose and fix Android SDK setup

$sdkRoot = "$env:LOCALAPPDATA\Android\Sdk"
$cmdlineDir = "$sdkRoot\cmdline-tools"

Write-Host "Diagnosing Android SDK..." -ForegroundColor Cyan
Write-Host ""

# Check if directory exists
if (Test-Path $cmdlineDir) {
    Write-Host "[OK] cmdline-tools directory exists" -ForegroundColor Green
    
    # List contents
    Write-Host ""
    Write-Host "Contents:" -ForegroundColor Yellow
    Get-ChildItem $cmdlineDir -Recurse -Directory | ForEach-Object {
        Write-Host "  $($_.FullName)" -ForegroundColor Gray
    }
    
    # Check for bin directory
    $binPaths = Get-ChildItem "$cmdlineDir\*\bin" -Recurse -ErrorAction SilentlyContinue
    if ($binPaths) {
        Write-Host ""
        Write-Host "[OK] Found bin directories:" -ForegroundColor Green
        $binPaths | ForEach-Object {
            Write-Host "  $($_.FullName)" -ForegroundColor Cyan
        }
        
        # Check for sdkmanager
        $sdkmanagers = Get-ChildItem "$cmdlineDir\*\bin\sdkmanager.bat" -Recurse -ErrorAction SilentlyContinue
        if ($sdkmanagers) {
            Write-Host ""
            Write-Host "[OK] Found sdkmanager:" -ForegroundColor Green
            $sdkmanagers | ForEach-Object {
                Write-Host "  $($_.FullName)" -ForegroundColor Cyan
            }
            
            # Try to use it
            $sdkmanager = $sdkmanagers[0].FullName
            Write-Host ""
            Write-Host "Testing sdkmanager..." -ForegroundColor Yellow
            & $sdkmanager --version
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host ""
                Write-Host "[OK] sdkmanager works!" -ForegroundColor Green
            }
        } else {
            Write-Host ""
            Write-Host "[X] sdkmanager.bat not found" -ForegroundColor Red
        }
    } else {
        Write-Host ""
        Write-Host "[X] No bin directories found" -ForegroundColor Red
    }
} else {
    Write-Host "[X] cmdline-tools directory not found" -ForegroundColor Red
}

Write-Host ""
Write-Host "SDK Root: $sdkRoot" -ForegroundColor Cyan
