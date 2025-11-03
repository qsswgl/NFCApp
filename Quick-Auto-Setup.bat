@echo off
REM Quick-Auto-Setup.bat
REM 快速自动化配置 - Java 和环境变量（最精简版本）

setlocal enabledelayedexpansion

cls
color 0A
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                                                            ║
echo ║        NFC 项目 - 快速自动化配置                          ║
echo ║                                                            ║
echo ║        Java JDK 17 + 环境变量一键配置                     ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM 检查管理员权限
net session >nul 2>&1
if errorlevel 1 (
    echo.
    color 0C
    echo [ERROR] 需要管理员权限！
    color 0A
    echo.
    echo 解决方法：
    echo  1. 右键点击此脚本
    echo  2. 选择 "以管理员身份运行"
    echo  3. 点击 "是" 确认
    echo.
    pause
    exit /b 1
)

echo [✓] 已检测到管理员权限
echo.

REM 设置路径
set "JAVA_INSTALL_PATH=C:\Program Files\Java\jdk-17"
set "DOWNLOAD_URL=https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe"
set "TEMP_FILE=%TEMP%\jdk-17.exe"

REM 步骤 1: 检查 Java 是否已安装
echo [1/4] 检查 Java 是否已安装...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] Java 已安装
    echo.
    goto :set_env
)

REM 步骤 2: 下载 Java
echo [✓] 开始下载 Java JDK 17...
echo 这可能需要几分钟，请耐心等待...
echo.

if exist "!TEMP_FILE!" (
    echo [✓] 本地已有安装文件，使用本地文件
) else (
    echo 从以下地址下载：
    echo !DOWNLOAD_URL!
    echo.
    
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "try { [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('!DOWNLOAD_URL!', '!TEMP_FILE!'); Write-Host '下载完成'; exit 0 } catch { Write-Host '下载失败'; exit 1 }" >nul 2>&1
    
    if errorlevel 1 (
        echo.
        echo [ERROR] 下载失败，可能原因：
        echo  - 网络连接不稳定
        echo  - 下载链接已失效
        echo.
        echo 解决方法：
        echo  1. 手动下载：https://www.oracle.com/java/technologies/downloads/
        echo  2. 选择 Java SE 17 LTS → Windows x64 Installer
        echo  3. 将文件放在：!TEMP_FILE!
        echo  4. 重新运行此脚本
        echo.
        pause
        exit /b 1
    )
)

echo [✓] 准备安装 Java...
echo.

REM 步骤 3: 安装 Java
echo [2/4] 安装 Java...
if exist "!JAVA_INSTALL_PATH!" (
    echo [✓] Java 已安装在：!JAVA_INSTALL_PATH!
) else (
    echo 运行安装程序（可能需要 2-3 分钟）...
    echo.
    
    if exist "!TEMP_FILE!" (
        start /wait "Java Installation" "!TEMP_FILE!" /s
        
        timeout /t 2 >nul
        
        if exist "!JAVA_INSTALL_PATH!" (
            echo [✓] Java 安装成功！
        ) else (
            echo [WARNING] Java 可能未安装，请手动检查
            echo 安装路径：!JAVA_INSTALL_PATH!
        )
    ) else (
        echo [ERROR] 找不到安装文件
        pause
        exit /b 1
    )
)
echo.

REM 步骤 4: 设置环境变量
:set_env
echo [3/4] 设置环境变量...
echo.

echo 设置 JAVA_HOME...
setx JAVA_HOME "!JAVA_INSTALL_PATH!" /M >nul 2>&1
if errorlevel 1 (
    echo [!] JAVA_HOME 设置可能失败
) else (
    echo [✓] JAVA_HOME = !JAVA_INSTALL_PATH!
)

echo 添加 %%JAVA_HOME%%\bin 到 PATH...

REM 获取当前 PATH
for /f "tokens=2*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH 2^>nul ^| findstr /i path') do set "CURRENT_PATH=%%B"

REM 检查是否已存在
if "!CURRENT_PATH:%%JAVA_HOME%%\bin=!" equ "!CURRENT_PATH!" (
    REM 不存在，需要添加
    setx PATH "!CURRENT_PATH!;%%JAVA_HOME%%\bin" /M >nul 2>&1
    if errorlevel 1 (
        echo [!] 添加到 PATH 可能失败
    ) else (
        echo [✓] 已添加 %%JAVA_HOME%%\bin 到 PATH
    )
) else (
    echo [✓] %%JAVA_HOME%%\bin 已在 PATH 中
)

echo.

REM 步骤 5: 最终提示
echo [4/4] 完成！
echo.
echo ════════════════════════════════════════════════════════════
echo ✨ 自动化配置完成！
echo ════════════════════════════════════════════════════════════
echo.
echo 📋 后续步骤：
echo.
echo 1️⃣  重启电脑（非常重要！）
echo     - 按 Windows 键 → 关机 → 重启
echo     - 或在命令行输入：shutdown /r /t 60
echo.
echo 2️⃣  重启后验证安装
echo     - 打开 PowerShell
echo     - 输入：java -version
echo     - 应该看到版本信息
echo.
echo 3️⃣  运行项目验证脚本
echo     - cd k:\NFC\NFCApp
echo     - .\Check-System-Ready.bat
echo.
echo 4️⃣  下一步
echo     - 安装 Android Studio（如需要）
echo     - 查看 AUTO_INSTALL_README.md
echo.
echo 📝 已设置的环境变量：
echo    - JAVA_HOME = !JAVA_INSTALL_PATH!
echo    - PATH 包含 %%JAVA_HOME%%\bin
echo.
echo 📁 临时文件：
echo    - !TEMP_FILE!
echo    （安装完后可以删除）
echo.

set /p RESTART="是否现在重启电脑? (Y/N): "
if /i "%RESTART%"=="Y" (
    echo.
    echo 30 秒后重启... (按 Ctrl+C 可取消)
    echo.
    shutdown /r /t 30 /c "NFC Setup - Restarting to apply environment variables"
) else (
    echo.
    echo ⚠️  请记得手动重启电脑！环境变量需要重启才能生效。
    echo.
)

pause
