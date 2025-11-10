@echo off
chcp 65001 >nul
echo ========================================
echo   NFC读写系统 - 手机安装脚本
echo ========================================
echo.

echo [1/3] 检查设备连接...
K:\tool\adb\adb.exe devices
echo.

echo [2/3] 安装APK到手机...
K:\tool\adb\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 安装失败！请检查：
    echo    1. 手机是否通过USB连接到本机
    echo    2. 手机是否开启USB调试
    echo    3. 是否授权了USB调试
    echo.
    pause
    exit /b 1
)
echo.

echo [3/3] 启动应用...
K:\tool\adb\adb.exe shell am start -n com.nfc.app/.MainActivity
echo.

echo ========================================
echo ✅ 安装完成！应用已在手机上启动
echo ========================================
echo.
pause
