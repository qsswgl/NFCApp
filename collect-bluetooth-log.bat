@echo off
chcp 65001 >nul
echo ========================================
echo   NFC读写系统 - 蓝牙打印日志收集工具
echo ========================================
echo.

echo [1/3] 清除旧日志...
K:\tool\adb\adb.exe logcat -c
echo ✓ 日志已清除
echo.

echo [2/3] 请在手机上执行打印操作...
echo     1. 打开应用
echo     2. 输入卡号、车号、金额
echo     3. 点击打印按钮
echo.
echo 按任意键开始收集日志...
pause >nul
echo.

echo [3/3] 正在收集日志 (按 Ctrl+C 停止)...
echo ========================================
echo.

REM 创建日志文件
set LOG_FILE=bluetooth_print_log_%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%.txt
set LOG_FILE=%LOG_FILE: =0%

echo 日志将保存到: %LOG_FILE%
echo.

REM 实时显示并保存日志
K:\tool\adb\adb.exe logcat -s NFCApp:V BluetoothPrinter:V | tee %LOG_FILE%
