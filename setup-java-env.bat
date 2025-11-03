@echo off
REM Set Java environment variables
setx JAVA_HOME "C:\Program Files\Java\jdk-17" /M
if errorlevel 1 (
    echo ERROR: Failed to set JAVA_HOME
    exit /b 1
)

echo JAVA_HOME has been set to: C:\Program Files\Java\jdk-17

REM Update PATH
reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH > nul
if errorlevel 1 (
    echo ERROR: Could not read PATH
    exit /b 1
)

for /f "tokens=2*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH') do set "OLD_PATH=%%B"

if not "%OLD_PATH%"=="" (
    if not "%OLD_PATH:jdk-17=%"=="%OLD_PATH%" (
        echo PATH already contains jdk-17
    ) else (
        setx PATH "%OLD_PATH%;C:\Program Files\Java\jdk-17\bin" /M
        echo PATH has been updated
    )
) else (
    echo WARNING: Could not update PATH
)

echo.
echo Java installation completed!
echo Please restart your computer for changes to take effect.
echo.
pause
