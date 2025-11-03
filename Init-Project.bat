@echo off
REM Init-Project.bat
REM Initialize Gradle Wrapper and check Gradle installation

echo.
echo ============================================
echo NFC Project - Initialize
echo ============================================
echo.

REM Check if gradle is available
echo [1] Checking Gradle installation...
gradle --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Gradle found
    echo.
    echo [2] Generating Gradle Wrapper...
    gradle wrapper
    echo [OK] Gradle Wrapper generated
) else (
    echo [INFO] Gradle not in PATH
    echo.
    echo Gradle Wrapper files may already exist, or will be generated on first build.
    echo.
    echo You can:
    echo 1. Continue without Gradle Wrapper (will install on first build)
    echo 2. Install Gradle manually from https://gradle.org/
    echo 3. Use the Gradle included with Android Studio
)

echo.
echo [3] Checking project structure...
if exist "build.gradle.kts" (
    echo [OK] build.gradle.kts found
) else (
    echo [ERROR] build.gradle.kts not found
)

if exist "settings.gradle.kts" (
    echo [OK] settings.gradle.kts found
) else (
    echo [ERROR] settings.gradle.kts not found
)

if exist "app\" (
    echo [OK] app folder found
) else (
    echo [ERROR] app folder not found
)

echo.
echo [4] Project initialization complete!
echo.
echo Next steps:
echo 1. Complete Java and Android SDK installation
echo 2. Run Check-System-Ready.bat
echo 3. Run Setup-LocalProperties.bat
echo 4. Restart computer
echo 5. Run check_env.bat for final verification
echo.
pause
