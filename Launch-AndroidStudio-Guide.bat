@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo.
echo ================================================
echo   Launch Android Studio Setup Guide
echo ================================================
echo.
echo [STEP 1] Launching Android Studio...
echo.

REM Try to find and launch Android Studio
set STUDIO_PATH=C:\Program Files\Android\Android Studio\bin\studio64.exe

if exist "%STUDIO_PATH%" (
    echo [OK] Found Android Studio at:
    echo     %STUDIO_PATH%
    echo.
    echo [INFO] Starting Android Studio...
    start "" "%STUDIO_PATH%"
    echo.
    echo ================================================
    echo   Android Studio is starting...
    echo ================================================
    echo.
    echo Please follow these steps:
    echo.
    echo [1] Wait for Android Studio to open
    echo [2] Click "Next" on welcome screen
    echo [3] Choose "Standard" installation
    echo [4] Accept license agreements
    echo [5] Click "Finish" and wait for SDK download
    echo.
    echo This will take 5-10 minutes depending on your network.
    echo.
    echo ================================================
    echo   What to note down:
    echo ================================================
    echo.
    echo When setup completes, Android Studio will show:
    echo   "Android SDK Location: C:\Users\...\AppData\Local\Android\Sdk"
    echo.
    echo Please copy that path and tell me!
    echo.
    echo ================================================
    echo.
    pause
) else (
    echo [ERROR] Android Studio not found at expected location.
    echo.
    echo Please manually locate and run:
    echo   Android Studio ^> bin ^> studio64.exe
    echo.
    echo Common locations:
    echo   C:\Program Files\Android\Android Studio\bin\studio64.exe
    echo   C:\Program Files (x86)\Android\Android Studio\bin\studio64.exe
    echo.
    pause
)
