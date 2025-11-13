@echo off
chcp 65001 > nul
cls

echo ╔══════════════════════════════════════════════════════════════╗
echo ║                                                              ║
echo ║    NFC缴费应用 - 打印修复版本 快速测试                      ║
echo ║    修复: 线程调度和连接检查问题                             ║
echo ║    版本: v2.4.1 (修复版)                                     ║
echo ║                                                              ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

:: 检查设备连接
echo [1/5] 检查设备连接...
adb.exe devices | findstr /R /C:"device$" > nul
if errorlevel 1 (
    echo ❌ 未检测到设备
    echo.
    echo 请确保:
    echo   1. 手机已通过USB连接
    echo   2. 已开启USB调试
    echo   3. 已授权USB调试权限
    echo.
    pause
    exit /b 1
)
echo ✓ 设备已连接
echo.

:: 安装新版本
echo [2/5] 安装修复版本...
adb.exe install -r app\build\outputs\apk\debug\app-debug.apk > nul 2>&1
if errorlevel 1 (
    echo ⚠️ 安装失败,尝试先卸载...
    adb.exe uninstall com.nfc.app > nul 2>&1
    adb.exe install app\build\outputs\apk\debug\app-debug.apk
    if errorlevel 1 (
        echo ❌ 安装失败
        pause
        exit /b 1
    )
)
echo ✓ 安装成功
echo.

:: 启动应用
echo [3/5] 启动应用...
adb.exe shell am start -n com.nfc.app/.MainActivity > nul 2>&1
if errorlevel 1 (
    echo ⚠️ 自动启动失败,请手动打开应用
) else (
    echo ✓ 应用已启动
)
echo.

:: 清除旧日志
echo [4/5] 清除旧日志...
adb.exe logcat -c
echo ✓ 日志已清除
echo.

:: 开始监控日志
echo [5/5] 监控打印日志 (实时)
echo ════════════════════════════════════════════════════════════════
echo.
echo 📱 请在手机上执行以下操作:
echo   1. 读取NFC卡
echo   2. 填写金额
echo   3. 点击 "确认" 按钮
echo.
echo 关键日志标识:
echo   ✓  - 成功步骤
echo   ❌ - 错误信息
echo   🔵 - SDK连接
echo   🖨️ - 打印操作
echo.
echo ════════════════════════════════════════════════════════════════
echo.

:: 启动日志监控
adb.exe logcat -v time | findstr /C:"PuQuPrinterManager" /C:"MainActivity" /C:"开始打印" /C:"打印成功" /C:"打印失败" /C:"连接成功" /C:"连接失败" /C:"连接超时"
