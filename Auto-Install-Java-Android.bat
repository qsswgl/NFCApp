@echo off
REM ============================================
REM NFC Project - Automated Java & Android Installation
REM Java 和 Android Studio 自动化安装脚本
REM ============================================

setlocal enabledelayedexpansion

echo.
echo ════════════════════════════════════════════════════════════════
echo     🚀 NFC 项目自动化配置脚本
echo     Java + Android Studio 一键安装
echo ════════════════════════════════════════════════════════════════
echo.

REM 设置变量
set "DOWNLOAD_DIR=%TEMP%\NFC_Setup"
set "JAVA_INSTALLER=jdk-17_windows-x64_bin.exe"
set "JAVA_URL=https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe"
set "JAVA_INSTALL_PATH=C:\Program Files\Java\jdk-17"
set "ANDROID_STUDIO_INSTALLER=android-studio-2024.1.1.31-windows.exe"
set "ANDROID_STUDIO_URL=https://redirector.gvt1.com/edgedl/android/studio/install/2024.1.1.31/android-studio-2024.1.1.31-windows.exe"

REM ============================================
REM Step 1: 检查管理员权限
REM ============================================
echo [1/6] 检查管理员权限...
net session >nul 2>&1
if errorlevel 1 (
    echo.
    echo ❌ 错误：需要管理员权限运行此脚本！
    echo.
    echo 解决方法：
    echo 右键点击此脚本 → 选择"以管理员身份运行"
    echo.
    pause
    exit /b 1
) else (
    echo ✓ 已验证管理员权限
)
echo.

REM ============================================
REM Step 2: 创建下载目录
REM ============================================
echo [2/6] 准备下载目录...
if not exist "!DOWNLOAD_DIR!" (
    mkdir "!DOWNLOAD_DIR!"
    echo ✓ 已创建下载目录：!DOWNLOAD_DIR!
) else (
    echo ✓ 下载目录已存在
)
echo.

REM ============================================
REM Step 3: 下载 Java
REM ============================================
echo [3/6] 下载 Java JDK 17...
if exist "!DOWNLOAD_DIR!\!JAVA_INSTALLER!" (
    echo ✓ Java 安装程序已存在，跳过下载
) else (
    echo 下载中... 这可能需要几分钟...
    echo 源: !JAVA_URL!
    
    REM 使用 PowerShell 下载（比 bitsadmin 更可靠）
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "try { $ProgressPreference = 'SilentlyContinue'; (New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('!JAVA_URL!', '!DOWNLOAD_DIR!\!JAVA_INSTALLER!'); Write-Host 'Download completed'; exit 0 } catch { Write-Host 'Download failed'; exit 1 }"
    
    if errorlevel 1 (
        echo.
        echo ⚠️  Java 下载失败，原因可能是：
        echo    - 网络连接问题
        echo    - 下载链接已失效
        echo.
        echo 解决方案：
        echo    手动下载：https://www.oracle.com/java/technologies/downloads/
        echo    选择 Java SE 17 LTS → Windows x64 Installer
        echo    保存到：!DOWNLOAD_DIR!
        echo.
        pause
        goto :android_step
    )
    echo ✓ Java 下载完成
)
echo.

REM ============================================
REM Step 4: 安装 Java
REM ============================================
echo [4/6] 安装 Java...
if exist "!JAVA_INSTALL_PATH!" (
    echo ✓ Java 已安装，跳过安装步骤
) else (
    if exist "!DOWNLOAD_DIR!\!JAVA_INSTALLER!" (
        echo 运行安装程序...
        cd /d "!DOWNLOAD_DIR!"
        start /wait !JAVA_INSTALLER!
        
        if exist "!JAVA_INSTALL_PATH!" (
            echo ✓ Java 安装成功
        ) else (
            echo ❌ Java 安装失败
            pause
            exit /b 1
        )
    ) else (
        echo ❌ 找不到 Java 安装程序
        pause
        exit /b 1
    )
)
echo.

REM ============================================
REM Step 5: 设置环境变量
REM ============================================
echo [5/6] 设置环境变量...

REM 设置 JAVA_HOME
echo 设置 JAVA_HOME = !JAVA_INSTALL_PATH!
setx JAVA_HOME "!JAVA_INSTALL_PATH!" /M >nul 2>&1
if errorlevel 1 (
    echo ❌ 设置 JAVA_HOME 失败
) else (
    echo ✓ JAVA_HOME 已设置
)

REM 将 JAVA_HOME\bin 添加到 PATH
echo 添加 %%JAVA_HOME%%\bin 到 PATH...
for /f "tokens=2*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH 2^>nul ^| findstr /i path') do set "CURRENT_PATH=%%B"

if not "!CURRENT_PATH:%%JAVA_HOME%%\bin=!" equ "!CURRENT_PATH!" (
    echo ✓ %%JAVA_HOME%%\bin 已在 PATH 中
) else (
    setx PATH "!CURRENT_PATH!;%%JAVA_HOME%%\bin" /M >nul 2>&1
    if errorlevel 1 (
        echo ❌ 添加到 PATH 失败
    ) else (
        echo ✓ 已添加到 PATH
    )
)

echo ✓ 环境变量设置完成
echo.

REM ============================================
REM Step 6: 下载 Android Studio（可选）
REM ============================================
:android_step
echo [6/6] Android Studio 处理...
echo.
echo ℹ️  Android Studio 文件较大（约 900MB）
echo.

set /p INSTALL_ANDROID="是否现在下载和安装 Android Studio? (Y/N): "
if /i not "!INSTALL_ANDROID!"=="Y" (
    goto :final_step
)

echo 下载 Android Studio...
echo 源: !ANDROID_STUDIO_URL!

if not exist "!DOWNLOAD_DIR!\!ANDROID_STUDIO_INSTALLER!" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "try { $ProgressPreference = 'SilentlyContinue'; (New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('!ANDROID_STUDIO_URL!', '!DOWNLOAD_DIR!\!ANDROID_STUDIO_INSTALLER!'); Write-Host 'Download completed'; exit 0 } catch { Write-Host 'Download failed'; exit 1 }"
    
    if errorlevel 1 (
        echo ⚠️  Android Studio 下载失败
        goto :final_step
    )
)

echo 运行 Android Studio 安装程序...
cd /d "!DOWNLOAD_DIR!"
start /wait !ANDROID_STUDIO_INSTALLER!

echo.
echo ✓ Android Studio 安装完成
echo.

REM ============================================
REM Final: 验证和总结
REM ============================================
:final_step
echo.
echo ════════════════════════════════════════════════════════════════
echo 📋 安装总结
echo ════════════════════════════════════════════════════════════════
echo.

echo 下一步操作：
echo.
echo 1️⃣  关闭 PowerShell（如果已打开）
echo.
echo 2️⃣  重启电脑
echo    - 环境变量需要重启才能生效
echo.
echo 3️⃣  重启后，打开 PowerShell 验证：
echo    java -version
echo.
echo 4️⃣  如果已安装 Android Studio：
echo    - 打开 Android Studio
echo    - Tools → SDK Manager
echo    - 复制 "Android SDK Location" 路径
echo.
echo 5️⃣  运行我们的配置脚本：
echo    cd k:\NFC\NFCApp
echo    .\Setup-LocalProperties.bat
echo    .\check_env.bat
echo.

echo 📁 下载文件位置：
echo    !DOWNLOAD_DIR!
echo.

echo 📝 已设置的环境变量：
echo    JAVA_HOME = !JAVA_INSTALL_PATH!
echo.

echo ════════════════════════════════════════════════════════════════
echo.
echo ✨ 配置向导完成！
echo.
pause
