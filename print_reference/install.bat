@echo off
chcp 65001 >nul
echo ================================
echo BLE蓝牙打印机 - 安装向导
echo ================================
echo.

echo 检查Android设备连接...
adb devices
echo.

echo 请选择操作:
echo 1. 打开Android Studio项目目录 (推荐)
echo 2. 打开安装说明文档
echo 3. 查看设备日志
echo.

set /p choice="请输入选择 (1-3): "

if "%choice%"=="1" (
    echo.
    echo 正在打开项目目录...
    explorer k:\Print
    echo.
    echo 请在Android Studio中:
    echo 1. File -^> Open -^> 选择 k:\Print
    echo 2. 等待Gradle同步完成
    echo 3. 点击运行按钮 [绿色三角形] 或按 Shift+F10
    echo 4. 应用将自动安装到设备
    pause
) else if "%choice%"=="2" (
    echo.
    start notepad INSTALL.md
) else if "%choice%"=="3" (
    echo.
    echo 开始监控日志... (按 Ctrl+C 停止)
    adb logcat -s BluetoothScanner:D BluetoothConnection:D MainActivity:D
) else (
    echo 无效选择
)

echo.
pause
