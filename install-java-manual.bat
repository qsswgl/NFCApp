@echo off
REM =============================================
REM Java Manual Installation Script
REM Supports: ZIP and EXE files
REM =============================================

setlocal enabledelayedexpansion

REM Check admin
net session >nul 2>&1
if errorlevel 1 (
    echo ERROR: Admin rights required
    echo Please run as Administrator
    pause
    exit /b 1
)

cls
echo.
echo ==========================================
echo     Java Manual Installation
echo ==========================================
echo.

REM Search for Java files in common locations
set "FOUND_FILE="
set "SEARCH_PATHS=C:\Users\%USERNAME%\Downloads C:\Users\%USERNAME%\Desktop %TEMP%"

echo Searching for Java installation files...
echo.

for %%P in (%SEARCH_PATHS%) do (
    if exist "%%P\amazon-corretto*.zip" (
        set "FOUND_FILE=%%P\amazon-corretto*.zip"
        echo Found: !FOUND_FILE!
        goto install
    )
    if exist "%%P\OpenJDK17U*.zip" (
        set "FOUND_FILE=%%P\OpenJDK17U*.zip"
        echo Found: !FOUND_FILE!
        goto install
    )
    if exist "%%P\jdk-17*.exe" (
        set "FOUND_FILE=%%P\jdk-17*.exe"
        echo Found: !FOUND_FILE!
        goto install
    )
)

echo.
echo No Java files found in:
echo - Downloads
echo - Desktop
echo - Temp
echo.
echo Please download Java first:
echo 1. Visit: https://corretto.aws/downloads/latest/
echo 2. Download: Windows x64 JDK (ZIP)
echo 3. Save to: C:\Users\!USERNAME!\Downloads
echo.
pause
exit /b 1

:install
echo.
echo Starting installation...
echo.

REM Install based on file type
if "!FOUND_FILE!"=="*.exe" (
    echo Installing from EXE...
    start /wait "!FOUND_FILE!" /s /D="C:\Program Files\Java\jdk-17"
) else (
    echo Installing from ZIP...
    echo Please extract to: C:\Program Files\Java
    echo File path will be: C:\Program Files\Java\jdk-17
    echo.
    pause
)

REM Set environment variables
echo.
echo Setting environment variables...

setx JAVA_HOME "C:\Program Files\Java\jdk-17" /M
setx PATH "%PATH%;C:\Program Files\Java\jdk-17\bin" /M

echo.
echo Installation complete!
echo Please restart your computer.
echo.

set /p RESTART="Restart now (Y/N): "
if /i "%RESTART%"=="Y" (
    timeout /t 10
    shutdown /r /t 0
) else (
    pause
)

endlocal
exit /b 0
