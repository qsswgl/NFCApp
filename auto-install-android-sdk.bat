@echo off
REM =============================================
REM One-Click Android SDK Setup
REM =============================================

setlocal enabledelayedexpansion

cls
echo.
echo ╔════════════════════════════════════════════╗
echo ║                                            ║
echo ║   Android SDK 自动安装程序                ║
echo ║                                            ║
echo ╚════════════════════════════════════════════╝
echo.

REM Check admin rights
net session >nul 2>&1
if errorlevel 1 (
    echo [!] 需要管理员权限
    echo.
    echo 请以管理员身份运行此脚本：
    echo 右键点击脚本 → 以管理员身份运行
    echo.
    pause
    exit /b 1
)

echo [√] 管理员权限已获取
echo.

REM Set SDK path
set "SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"

echo 此脚本将会：
echo   1. 下载 Android 命令行工具（约 150 MB）
echo   2. 安装 Android SDK（约 2-3 GB）
echo   3. 设置环境变量
echo.
echo SDK 安装位置：%SDK_ROOT%
echo.
echo 预计耗时：10-15 分钟
echo.

set /p CONFIRM="继续安装？(Y/N): "
if /i not "%CONFIRM%"=="Y" (
    echo 安装已取消
    pause
    exit /b 0
)

echo.
echo ==========================================
echo   第一步：安装命令行工具
echo ==========================================
echo.

REM Create SDK directories
if not exist "%SDK_ROOT%" mkdir "%SDK_ROOT%"
if not exist "%SDK_ROOT%\cmdline-tools" mkdir "%SDK_ROOT%\cmdline-tools"

REM Download command-line tools
echo 正在下载 Android 命令行工具...
echo （这可能需要 2-5 分钟）
echo.

set "CMDLINE_URL=https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
set "DOWNLOAD_PATH=%TEMP%\android-cmdline-tools.zip"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; ^
   $ProgressPreference = 'SilentlyContinue'; ^
   try { ^
     Invoke-WebRequest -Uri '%CMDLINE_URL%' -OutFile '%DOWNLOAD_PATH%' -UseBasicParsing; ^
     Write-Host '下载成功！' -ForegroundColor Green ^
   } catch { ^
     Write-Host '下载失败：' $_.Exception.Message -ForegroundColor Red; ^
     exit 1 ^
   }"

if not exist "%DOWNLOAD_PATH%" (
    echo.
    echo [X] 下载失败
    echo.
    echo 可能的原因：
    echo   - 网络连接问题
    echo   - 防火墙阻止
    echo.
    echo 解决方案：
    echo   1. 检查网络连接
    echo   2. 使用 VPN
    echo   3. 手动下载后放到 %TEMP% 目录
    echo.
    pause
    exit /b 1
)

REM Extract
echo.
echo 正在解压...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "Expand-Archive -Path '%DOWNLOAD_PATH%' -DestinationPath '%SDK_ROOT%\cmdline-tools' -Force; ^
   Write-Host '解压完成！' -ForegroundColor Green"

REM Rename to 'latest'
if exist "%SDK_ROOT%\cmdline-tools\cmdline-tools" (
    if exist "%SDK_ROOT%\cmdline-tools\latest" rmdir /s /q "%SDK_ROOT%\cmdline-tools\latest"
    move "%SDK_ROOT%\cmdline-tools\cmdline-tools" "%SDK_ROOT%\cmdline-tools\latest" >nul
)

echo.
echo ==========================================
echo   第二步：设置环境变量
echo ==========================================
echo.

REM Set ANDROID_HOME
echo 设置 ANDROID_HOME...
setx ANDROID_HOME "%SDK_ROOT%" /M >nul 2>&1
if errorlevel 1 (
    setx ANDROID_HOME "%SDK_ROOT%" >nul 2>&1
)
echo [√] ANDROID_HOME = %SDK_ROOT%

REM Update PATH
echo 更新 PATH...
setx PATH "%PATH%;%SDK_ROOT%\platform-tools;%SDK_ROOT%\cmdline-tools\latest\bin" /M >nul 2>&1
if errorlevel 1 (
    echo [!] 警告：无法更新系统 PATH
) else (
    echo [√] PATH 已更新
)

REM Set for current session
set "ANDROID_HOME=%SDK_ROOT%"
set "PATH=%SDK_ROOT%\cmdline-tools\latest\bin;%SDK_ROOT%\platform-tools;%PATH%"

echo.
echo ==========================================
echo   第三步：安装 SDK 组件
echo ==========================================
echo.

set "SDKMANAGER=%SDK_ROOT%\cmdline-tools\latest\bin\sdkmanager.bat"

REM Accept licenses
echo 接受许可协议...
echo y | "%SDKMANAGER%" --licenses >nul 2>&1
echo [√] 许可协议已接受

echo.
echo 正在安装 SDK 组件...
echo （这可能需要 5-10 分钟，请耐心等待）
echo.

REM Install packages
echo [1/7] Platform Tools...
call "%SDKMANAGER%" "platform-tools" >nul 2>&1
echo [√] Platform Tools 已安装

echo [2/7] Build Tools...
call "%SDKMANAGER%" "build-tools;34.0.0" >nul 2>&1
echo [√] Build Tools 已安装

echo [3/7] Android 14.0 (API 34)...
call "%SDKMANAGER%" "platforms;android-34" >nul 2>&1
echo [√] Android 14.0 已安装

echo [4/7] Emulator...
call "%SDKMANAGER%" "emulator" >nul 2>&1
echo [√] Emulator 已安装

echo [5/7] System Images...
call "%SDKMANAGER%" "system-images;android-34;google_apis;x86_64" >nul 2>&1
echo [√] System Images 已安装

echo [6/7] CMake...
call "%SDKMANAGER%" "cmake;3.22.1" >nul 2>&1
echo [√] CMake 已安装

echo [7/7] NDK...
call "%SDKMANAGER%" "ndk;25.1.8937393" >nul 2>&1
echo [√] NDK 已安装

echo.
echo ==========================================
echo   安装完成！
echo ==========================================
echo.
echo [√] Android SDK 已成功安装到：
echo     %SDK_ROOT%
echo.
echo [√] 已安装的组件：
echo     • Platform Tools (adb, fastboot)
echo     • Build Tools 34.0.0
echo     • Android 14.0 API Level 34
echo     • Android Emulator
echo     • System Images (x86_64)
echo     • CMake 3.22.1
echo     • NDK 25.1.8937393
echo.
echo [√] 环境变量已设置：
echo     • ANDROID_HOME = %SDK_ROOT%
echo     • PATH 已更新
echo.
echo ==========================================
echo.
echo 下一步：
echo   1. 关闭并重新打开终端
echo   2. 运行：adb version（验证安装）
echo   3. 运行：.\Setup-LocalProperties.bat（配置项目）
echo.
pause
