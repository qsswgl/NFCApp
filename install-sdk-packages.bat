@echo off
REM =============================================
REM Android SDK Packages Installation Script
REM =============================================

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo   Installing Android SDK Packages
echo ==========================================
echo.

REM Set paths
set "SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
set "SDKMANAGER=%SDK_ROOT%\cmdline-tools\latest\bin\sdkmanager.bat"

REM Check if sdkmanager exists
if not exist "%SDKMANAGER%" (
    echo ERROR: sdkmanager not found
    echo Please run install-cmdline-tools.bat first
    pause
    exit /b 1
)

echo Using SDK at: %SDK_ROOT%
echo.

REM Accept licenses
echo [1/3] Accepting licenses...
echo y | "%SDKMANAGER%" --licenses >nul 2>&1
echo Licenses accepted!

REM Install essential packages
echo.
echo [2/3] Installing essential SDK packages...
echo This may take 5-10 minutes, please wait...
echo.

REM Install packages one by one with progress
echo Installing platform-tools...
call "%SDKMANAGER%" "platform-tools"

echo.
echo Installing build-tools...
call "%SDKMANAGER%" "build-tools;34.0.0"

echo.
echo Installing Android 14.0 (API 34)...
call "%SDKMANAGER%" "platforms;android-34"

echo.
echo Installing emulator...
call "%SDKMANAGER%" "emulator"

echo.
echo Installing system images...
call "%SDKMANAGER%" "system-images;android-34;google_apis;x86_64"

echo.
echo Installing CMake (for native builds)...
call "%SDKMANAGER%" "cmake;3.22.1"

echo.
echo Installing NDK...
call "%SDKMANAGER%" "ndk;25.1.8937393"

REM List installed packages
echo.
echo [3/3] Verifying installation...
echo.
echo Installed packages:
call "%SDKMANAGER%" --list_installed

echo.
echo ==========================================
echo   SDK Installation Complete!
echo ==========================================
echo.
echo SDK Location: %SDK_ROOT%
echo.
echo Installed components:
echo   - Platform Tools (adb, fastboot)
echo   - Build Tools 34.0.0
echo   - Android 14.0 API Level 34
echo   - Android Emulator
echo   - System Images
echo   - CMake and NDK
echo.
pause
