@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo ================================================================
echo   NFC 缴费应用 - 打印机选择功能安装脚本
echo   版本: v2.4 - 多打印机支持
echo   更新时间: 2025-01-16
echo ================================================================
echo.

:: 检查 ADB 是否存在
if not exist "adb.exe" (
    echo [错误] 找不到 adb.exe
    echo 请确保在 NFCApp 目录下运行此脚本
    pause
    exit /b 1
)

:: 检查 APK 是否存在
if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo [错误] 找不到 APK 文件
    echo 请先编译项目: gradlew.bat assembleDebug
    pause
    exit /b 1
)

:: 检查设备连接
echo [步骤 1/4] 检查设备连接...
adb.exe devices | findstr /R /C:"device$" > nul
if errorlevel 1 (
    echo [错误] 未检测到设备
    echo 请确保:
    echo   1. 手机已通过 USB 连接到电脑
    echo   2. 手机已开启 USB 调试
    echo   3. 已授权 USB 调试权限
    echo.
    echo 按任意键重新检测设备...
    pause > nul
    adb.exe devices
    pause
    exit /b 1
)

echo [成功] 设备已连接
echo.

:: 卸载旧版本
echo [步骤 2/4] 卸载旧版本...
adb.exe uninstall com.nfc.app > nul 2>&1
if errorlevel 1 (
    echo [提示] 未发现旧版本或卸载失败，继续安装...
) else (
    echo [成功] 旧版本已卸载
)
echo.

:: 安装新版本
echo [步骤 3/4] 安装新版本...
adb.exe install "app\build\outputs\apk\debug\app-debug.apk"
if errorlevel 1 (
    echo [错误] 安装失败
    echo.
    echo 可能的原因:
    echo   1. APK 文件损坏
    echo   2. 设备存储空间不足
    echo   3. 签名冲突 (请手动卸载旧版本)
    pause
    exit /b 1
)

echo [成功] 应用已安装
echo.

:: 启动应用
echo [步骤 4/4] 启动应用...
adb.exe shell am start -n com.nfc.app/.MainActivity
if errorlevel 1 (
    echo [警告] 自动启动失败，请手动打开应用
) else (
    echo [成功] 应用已启动
)
echo.

:: 显示日志命令
echo ================================================================
echo   安装完成!
echo ================================================================
echo.
echo 测试步骤:
echo   1. 确保打印机 AQ-V258007640 已开机
echo   2. 确保手机蓝牙已开启且打印机已配对
echo   3. 读取 NFC 卡
echo   4. 填写金额
echo   5. 点击 "确认" 按钮
echo   6. 如有多个打印机,选择 AQ-V258007640
echo   7. 观察是否打印2份小票
echo.
echo 查看实时日志 (可选):
echo   adb.exe logcat -v time ^| findstr "PuQuPrinterManager NFCApp MainActivity"
echo.
echo 详细说明文档: PRINTER_SELECTION_UPDATE.md
echo.

pause
