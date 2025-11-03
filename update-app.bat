@echo off
echo ========================================
echo NFC应用 - 更新安装脚本
echo ========================================
echo.

set ADB=K:\tool\adb\adb.exe
set APK=K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk

echo [1/5] 检查设备连接...
%ADB% devices
echo.

echo [2/5] 停止应用（如果正在运行）...
%ADB% shell am force-stop com.nfc.app
timeout /t 2 /nobreak >nul
echo.

echo [3/5] 卸载旧版本...
%ADB% uninstall com.nfc.app
timeout /t 2 /nobreak >nul
echo.

echo [4/5] 安装新版本...
%ADB% install -r %APK%
echo.

if %errorlevel% == 0 (
    echo [5/5] 启动应用...
    %ADB% shell am start -n com.nfc.app/.MainActivity
    echo.
    echo ========================================
    echo ✓ 更新成功！应用已启动
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ✗ 安装失败
    echo ========================================
)

echo.
pause
