@echo off
chcp 65001 >nul
echo ========================================
echo NFC应用 - 安装和运行脚本
echo ========================================
echo.

set ADB=C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe
set APK=app\build\outputs\apk\debug\app-debug.apk

echo [步骤1] 重启ADB服务...
%ADB% kill-server
timeout /t 2 /nobreak >nul
%ADB% start-server
echo.

echo [步骤2] 检查已连接的设备...
echo.
%ADB% devices
echo.

echo ----------------------------------------
echo 设备状态说明：
echo - device          : 设备已就绪，可以安装
echo - unauthorized   : 请在手机上点击"允许USB调试"
echo - offline        : 设备离线，请重新插拔USB
echo - 无设备显示      : 请检查USB连接和驱动
echo ----------------------------------------
echo.

echo 如果上方显示有设备且状态为'device'，按任意键继续安装...
echo 否则请按Ctrl+C退出，解决连接问题后重试
pause

echo.
echo 正在卸载旧版本（如果存在）...
%ADB% uninstall com.nfc.app
echo.

echo 正在安装APK...
%ADB% install -r %APK%
if %errorlevel% == 0 (
    echo.
    echo ✓ 安装成功！
    echo.
    echo 正在启动应用...
    %ADB% shell am start -n com.nfc.app/.MainActivity
    echo.
    echo ✓ 应用已启动！
) else (
    echo.
    echo ✗ 安装失败！
    echo 请检查：
    echo 1. 设备是否已连接
    echo 2. 是否已启用USB调试
    echo 3. 是否已授权此电脑的调试权限
)

echo.
echo 按任意键退出...
pause
