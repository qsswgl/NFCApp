@echo off
REM Check-System-Ready.bat
REM Simple system readiness check

setlocal enabledelayedexpansion

echo.
echo ============================================
echo NFC Project - System Check
echo ============================================
echo.

set /a checks=0
set /a passed=0

REM Check 1: Java
echo [1] Checking Java installation...
if exist "C:\Program Files\Java" (
    echo [OK] Java folder found at C:\Program Files\Java
    set /a passed+=1
) else (
    echo [MISSING] Java not found
    echo         Download from: https://www.oracle.com/java/technologies/downloads/
)
set /a checks+=1

echo.

REM Check 2: JAVA_HOME
echo [2] Checking JAVA_HOME environment variable...
if defined JAVA_HOME (
    echo [OK] JAVA_HOME = %JAVA_HOME%
    set /a passed+=1
) else (
    echo [MISSING] JAVA_HOME not set
    echo          Please set it manually (see SETUP_GUIDE.md)
)
set /a checks+=1

echo.

REM Check 3: Android SDK
echo [3] Checking Android SDK...
if exist "%USERPROFILE%\AppData\Local\Android\sdk" (
    echo [OK] Android SDK found at %USERPROFILE%\AppData\Local\Android\sdk
    set /a passed+=1
) else (
    echo [MISSING] Android SDK not found
    echo          Download Android Studio: https://developer.android.com/studio
)
set /a checks+=1

echo.

REM Check 4: ANDROID_HOME
echo [4] Checking ANDROID_HOME environment variable...
if defined ANDROID_HOME (
    echo [OK] ANDROID_HOME = %ANDROID_HOME%
    set /a passed+=1
) else (
    echo [MISSING] ANDROID_HOME not set
    echo          Please set it manually (see SETUP_GUIDE.md)
)
set /a checks+=1

echo.

REM Check 5: Gradle Wrapper
echo [5] Checking Gradle Wrapper...
if exist "gradlew.bat" (
    echo [OK] Gradle Wrapper found
    set /a passed+=1
) else (
    echo [MISSING] Gradle Wrapper not found
)
set /a checks+=1

echo.
echo ============================================
echo Summary: %passed%/%checks% checks passed
echo ============================================
echo.

if %passed% equ %checks% (
    echo [SUCCESS] All systems ready!
    echo.
    echo Next steps:
    echo 1. Run Setup-LocalProperties.bat
    echo 2. Restart your computer
    echo 3. Run check_env.bat to verify
    echo 4. Open VS Code and start developing
) else (
    set /a remaining=%checks%-%passed%
    echo [ACTION NEEDED] %remaining% items still need setup
    echo.
    echo Please complete the setup steps from SETUP_GUIDE.md:
    echo 1. Install Java
    echo 2. Install Android Studio
    echo 3. Set environment variables
    echo 4. Restart computer
)

echo.
pause
