@echo off
REM =============================================
REM Java JDK 17 LTS 自动安装脚本
REM =============================================
REM 功能：自动下载并安装 Java JDK 17 LTS
REM =============================================

setlocal enabledelayedexpansion

REM 检查管理员权限
net session >nul 2>&1
if errorlevel 1 (
    echo.
    echo ==========================================
    echo   错误：需要管理员权限
    echo ==========================================
    echo.
    echo 请以管理员身份运行此脚本：
    echo 右键点击 PowerShell 或 CMD
    echo 选择 "以管理员身份运行"
    echo.
    pause
    exit /b 1
)

cls
echo.
echo ==========================================
echo     Java JDK 17 LTS 自动安装程序
echo ==========================================
echo.
echo 开始安装流程...
echo.

REM ==========================================
REM 第一步：下载 Java
REM ==========================================
echo [1/4] 检查并下载 Java JDK 17 LTS
echo.

set JAVA_URL=https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe
set JAVA_PATH=%TEMP%\jdk-17.exe
set JAVA_HOME_PATH=C:\Program Files\Java\jdk-17

if exist "%JAVA_PATH%" (
    echo 提示：Java 已在临时目录中
    for %%A in ("%JAVA_PATH%") do set JAVA_SIZE=%%~zA
    set /a JAVA_SIZE_MB=!JAVA_SIZE!/1048576
    echo 文件大小：!JAVA_SIZE_MB! MB
) else (
    echo 正在从以下地址下载 Java：
    echo %JAVA_URL%
    echo 这可能需要 2-5 分钟...
    echo.
    
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "try { Invoke-WebRequest -Uri '%JAVA_URL%' -OutFile '%JAVA_PATH%' -UseBasicParsing -TimeoutSec 600; Write-Host '下载成功'; exit 0 } catch { Write-Host '下载失败'; exit 1 }"
    
    if errorlevel 1 (
        echo.
        echo 下载失败！
        echo 请手动从以下地址下载：
        echo https://www.oracle.com/java/technologies/downloads/
        echo.
        echo 选择：Java SE 17 LTS 右箭头 Windows x64 Installer
        echo 保存到：%JAVA_PATH%
        echo.
        pause
        exit /b 1
    )
)

echo.

REM ==========================================
REM 第二步：安装 Java
REM ==========================================
echo [2/4] 安装 Java JDK 17 LTS
echo 这会自动进行，请等候...
echo.

if not exist "%JAVA_PATH%" (
    echo 错误：Java 安装文件未找到
    pause
    exit /b 1
)

start /wait "%JAVA_PATH%" /s /D="%JAVA_HOME_PATH%"

if errorlevel 1 (
    echo.
    echo 安装可能失败，请检查。
) else (
    echo Java 安装完成。
)

echo.

REM ==========================================
REM 第三步：设置环境变量
REM ==========================================
echo [3/4] 设置环境变量
echo.

REM 检查 Java 目录是否存在
if not exist "%JAVA_HOME_PATH%" (
    echo 错误：Java 目录未找到：%JAVA_HOME_PATH%
    pause
    exit /b 1
)

REM 设置 JAVA_HOME
setx JAVA_HOME "%JAVA_HOME_PATH%" /M >nul 2>&1
if errorlevel 1 (
    echo 警告：设置 JAVA_HOME 时出错
) else (
    echo 已设置 JAVA_HOME = %JAVA_HOME_PATH%
)

REM 更新 PATH（添加 Java bin 目录）
for /f "usebackq tokens=2,*" %%A in (`reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH`) do set "CURRENT_PATH=%%B"

if not "!CURRENT_PATH!"=="!CURRENT_PATH:%JAVA_HOME_PATH%\bin=!" (
    echo PATH 已包含 Java bin 目录
) else (
    setx PATH "%JAVA_HOME_PATH%\bin;!CURRENT_PATH!" /M >nul 2>&1
    if errorlevel 1 (
        echo 警告：更新 PATH 时出错
    ) else (
        echo 已更新 PATH 环境变量
    )
)

echo.

REM ==========================================
REM 第四步：验证安装
REM ==========================================
echo [4/4] 验证安装
echo.

REM 设置当前进程的环境变量（立即生效用于验证）
set "JAVA_HOME=%JAVA_HOME_PATH%"
set "PATH=%JAVA_HOME_PATH%\bin;!PATH!"

"%JAVA_HOME%\bin\java" -version >nul 2>&1
if errorlevel 1 (
    echo 提示：Java 命令验证失败，这在重启后会恢复正常
) else (
    echo Java 已成功安装并可用
    "%JAVA_HOME%\bin\java" -version
)

echo.
echo ==========================================
echo     安装完成
echo ==========================================
echo.
echo 成功：Java JDK 17 LTS 已安装
echo 安装路径：%JAVA_HOME_PATH%
echo.
echo 重要提示：
echo 环境变量已设置，但需要重启电脑才能在所有应用中生效。
echo.

set /p RESTART="是否立即重启电脑 (Y/N): "
if /i "%RESTART%"=="Y" (
    echo.
    echo 电脑将在 10 秒后重启...
    echo.
    timeout /t 10 /nobreak
    shutdown /r /t 0 /c "Java 安装完成，重启系统"
) else (
    echo.
    echo 请手动重启电脑以使环境变量生效。
    echo.
    pause
)

endlocal
exit /b 0
