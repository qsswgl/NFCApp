@echo off
echo ==========================================
echo 物理主机 - NFC应用快速安装脚本
echo ==========================================
echo.
echo 使用说明：
echo 1. 将此脚本和 app-debug.apk 复制到物理主机
echo 2. 确保手机已通过USB连接到物理主机
echo 3. 确保手机已启用USB调试
echo 4. 运行此脚本
echo.
echo ==========================================
echo.

REM 检查是否有platform-tools
set ADB_PATH=
if exist "C:\platform-tools\adb.exe" set ADB_PATH=C:\platform-tools\adb.exe
if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" set ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
if exist "C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe" set ADB_PATH=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe

if "%ADB_PATH%"=="" (
    echo [错误] 未找到ADB工具！
    echo.
    echo 请下载Android Platform Tools：
    echo https://developer.android.com/tools/releases/platform-tools
    echo.
    echo 解压到 C:\platform-tools\ 然后重新运行此脚本
    echo.
    pause
    exit /b 1
)

echo [信息] 找到ADB工具：%ADB_PATH%
echo.

REM 查找APK文件
set APK_PATH=
if exist "app-debug.apk" set APK_PATH=app-debug.apk
if exist "app\build\outputs\apk\debug\app-debug.apk" set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

if "%APK_PATH%"=="" (
    echo [错误] 未找到 app-debug.apk 文件！
    echo.
    echo 请将APK文件放在以下位置之一：
    echo 1. 与此脚本同一目录
    echo 2. app\build\outputs\apk\debug\app-debug.apk
    echo.
    pause
    exit /b 1
)

echo [信息] 找到APK文件：%APK_PATH%
echo.

echo [步骤1] 重启ADB服务...
"%ADB_PATH%" kill-server
timeout /t 2 /nobreak >nul
"%ADB_PATH%" start-server
echo.

echo [步骤2] 检查已连接的设备...
echo.
"%ADB_PATH%" devices
echo.

echo [步骤3] 尝试安装应用...
echo.
"%ADB_PATH%" install -r "%APK_PATH%"
echo.

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo ✓ 安装成功！
    echo ========================================
    echo.
    
    REM 尝试启用网络ADB
    echo [可选] 是否启用网络ADB以便虚拟机连接？[Y/N]
    set /p ENABLE_NETWORK=
    
    if /i "%ENABLE_NETWORK%"=="Y" (
        echo.
        echo [步骤4] 启用网络ADB...
        "%ADB_PATH%" tcpip 5555
        echo.
        echo [步骤5] 获取手机IP地址...
        "%ADB_PATH%" shell ip -f inet addr show wlan0
        echo.
        echo ========================================
        echo 请记下上方显示的IP地址（例如：192.168.1.100）
        echo.
        echo 然后在虚拟机中运行：
        echo adb connect 手机IP:5555
        echo ========================================
    )
    
    echo.
    echo [步骤6] 启动应用...
    "%ADB_PATH%" shell am start -n com.nfc.app/.MainActivity
    echo.
    echo ✓ 应用已启动！
    echo.
) else (
    echo.
    echo ========================================
    echo ✗ 安装失败！
    echo ========================================
    echo.
    echo 可能的原因：
    echo 1. 设备未连接或未授权USB调试
    echo 2. 手机上已有签名不同的同名应用
    echo.
    echo 解决方法：
    echo 1. 在手机上卸载旧版本应用
    echo 2. 或使用此命令先卸载：
    echo    %ADB_PATH% uninstall com.nfc.app
    echo.
)

echo.
pause
