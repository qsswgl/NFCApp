@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo.
echo ================================================
echo   Post Android Studio Setup
echo ================================================
echo.
echo This script will verify your SDK installation
echo and configure the project.
echo.
echo ================================================

REM Check common SDK locations
set "SDK_PATH="
set "DEFAULT_SDK=%LOCALAPPDATA%\Android\Sdk"

if exist "%DEFAULT_SDK%\platform-tools\adb.exe" (
    set "SDK_PATH=%DEFAULT_SDK%"
    echo [OK] Found Android SDK at:
    echo     !SDK_PATH!
) else (
    echo [INFO] Please enter your Android SDK path:
    echo        Example: C:\Users\YourName\AppData\Local\Android\Sdk
    echo.
    set /p "SDK_PATH=SDK Path: "
)

echo.
echo ================================================
echo   Verifying SDK Components...
echo ================================================
echo.

REM Verify essential components
set "ALL_OK=1"

if exist "!SDK_PATH!\platform-tools\adb.exe" (
    echo [OK] Platform Tools found
) else (
    echo [X] Platform Tools missing
    set "ALL_OK=0"
)

if exist "!SDK_PATH!\build-tools" (
    echo [OK] Build Tools found
) else (
    echo [X] Build Tools missing
    set "ALL_OK=0"
)

if exist "!SDK_PATH!\platforms\android-34" (
    echo [OK] Android 34 Platform found
) else (
    if exist "!SDK_PATH!\platforms\android-33" (
        echo [OK] Android 33 Platform found
    ) else (
        echo [X] Android Platform missing
        set "ALL_OK=0"
    )
)

echo.
if !ALL_OK!==1 (
    echo ================================================
    echo   Creating local.properties...
    echo ================================================
    echo.
    
    REM Create local.properties with SDK path
    set "PROPS_FILE=%~dp0local.properties"
    set "SDK_PATH_ESCAPED=!SDK_PATH:\=\\!"
    
    echo sdk.dir=!SDK_PATH_ESCAPED! > "!PROPS_FILE!"
    
    echo [OK] Created local.properties
    echo     SDK Path: !SDK_PATH!
    echo.
    
    echo ================================================
    echo   Setting ANDROID_HOME...
    echo ================================================
    echo.
    
    setx ANDROID_HOME "!SDK_PATH!" /M >nul 2>&1
    if !ERRORLEVEL!==0 (
        echo [OK] ANDROID_HOME set to:
        echo     !SDK_PATH!
    ) else (
        echo [WARN] Could not set ANDROID_HOME automatically.
        echo        Please set manually or run as Administrator.
    )
    
    echo.
    echo ================================================
    echo   Setup Complete!
    echo ================================================
    echo.
    echo Next steps:
    echo   1. Restart this terminal
    echo   2. Run: .\Check-System-Ready.bat
    echo   3. Open VS Code: code .
    echo   4. Start developing!
    echo.
) else (
    echo ================================================
    echo   Some SDK components are missing
    echo ================================================
    echo.
    echo Please open Android Studio and:
    echo   1. Go to: Tools ^> SDK Manager
    echo   2. Install missing components:
    echo      - Android SDK Platform-Tools
    echo      - Android SDK Build-Tools
    echo      - Android 14.0 (API 34) or Android 13.0 (API 33)
    echo.
)

echo.
pause
