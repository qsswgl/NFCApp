@echo off
REM =============================================
REM Android SDK Command-Line Installation Script
REM =============================================

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo   Android SDK Command-Line Setup
echo ==========================================
echo.

REM Set SDK path
set "SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
set "CMDLINE_TOOLS_DIR=%SDK_ROOT%\cmdline-tools"
set "LATEST_DIR=%CMDLINE_TOOLS_DIR%\latest"

echo SDK will be installed to: %SDK_ROOT%
echo.

REM Create SDK directories
echo [1/5] Creating SDK directories...
if not exist "%SDK_ROOT%" mkdir "%SDK_ROOT%"
if not exist "%CMDLINE_TOOLS_DIR%" mkdir "%CMDLINE_TOOLS_DIR%"

REM Download command-line tools
echo.
echo [2/5] Downloading Android command-line tools...
set "CMDLINE_URL=https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
set "DOWNLOAD_PATH=%TEMP%\commandlinetools.zip"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%CMDLINE_URL%' -OutFile '%DOWNLOAD_PATH%' -UseBasicParsing; Write-Host 'Download complete' } catch { Write-Host 'Download failed'; exit 1 }"

if not exist "%DOWNLOAD_PATH%" (
    echo ERROR: Download failed
    pause
    exit /b 1
)

echo Download successful!

REM Extract command-line tools
echo.
echo [3/5] Extracting command-line tools...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "Expand-Archive -Path '%DOWNLOAD_PATH%' -DestinationPath '%CMDLINE_TOOLS_DIR%' -Force"

REM Rename to 'latest'
if exist "%CMDLINE_TOOLS_DIR%\cmdline-tools" (
    if exist "%LATEST_DIR%" rmdir /s /q "%LATEST_DIR%"
    move "%CMDLINE_TOOLS_DIR%\cmdline-tools" "%LATEST_DIR%"
)

echo Extraction complete!

REM Set ANDROID_HOME environment variable
echo.
echo [4/5] Setting ANDROID_HOME environment variable...
setx ANDROID_HOME "%SDK_ROOT%" /M >nul 2>&1
if errorlevel 1 (
    echo WARNING: Could not set system ANDROID_HOME, trying user level...
    setx ANDROID_HOME "%SDK_ROOT%" >nul 2>&1
)
echo ANDROID_HOME set to: %SDK_ROOT%

REM Update PATH
echo.
echo [5/5] Updating PATH...
for /f "tokens=2*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH 2^>nul') do set "SYSTEM_PATH=%%B"
if not "!SYSTEM_PATH!"=="!SYSTEM_PATH:Android\Sdk=!" (
    echo PATH already contains Android SDK
) else (
    setx PATH "!SYSTEM_PATH!;%SDK_ROOT%\platform-tools;%SDK_ROOT%\cmdline-tools\latest\bin" /M >nul 2>&1
    if errorlevel 1 (
        echo WARNING: Could not update system PATH
    ) else (
        echo PATH updated successfully
    )
)

REM Set environment for current session
set "ANDROID_HOME=%SDK_ROOT%"
set "PATH=%SDK_ROOT%\cmdline-tools\latest\bin;%SDK_ROOT%\platform-tools;%PATH%"

echo.
echo ==========================================
echo   Command-line tools installed!
echo ==========================================
echo.
echo Next: Install SDK packages using sdkmanager
echo.
echo Run: install-sdk-packages.bat
echo.
pause
