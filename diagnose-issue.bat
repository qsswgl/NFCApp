@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo ================================================================
echo   打印机选择功能 - 问题诊断工具
echo   时间: %date% %time%
echo ================================================================
echo.

:: 1. 检查设备连接
echo [1/6] 检查设备连接状态...
echo ----------------------------------------
adb.exe devices
echo.

:: 2. 检查应用是否已安装
echo [2/6] 检查应用安装状态...
echo ----------------------------------------
adb.exe shell pm list packages | findstr "com.nfc.app"
if errorlevel 1 (
    echo [警告] 应用未安装
) else (
    echo [成功] 应用已安装
)
echo.

:: 3. 检查蓝牙权限
echo [3/6] 检查应用权限...
echo ----------------------------------------
adb.exe shell dumpsys package com.nfc.app | findstr "android.permission.BLUETOOTH"
echo.

:: 4. 检查已配对的蓝牙设备
echo [4/6] 检查已配对的蓝牙设备...
echo ----------------------------------------
echo 正在获取蓝牙设备信息...
adb.exe shell dumpsys bluetooth_manager | findstr -i "AQ-V"
echo.

:: 5. 获取最近的应用日志
echo [5/6] 获取最近的应用日志 (最近100行)...
echo ----------------------------------------
echo 查找关键错误...
adb.exe logcat -d -v time | findstr /i "PuQuPrinterManager NFCApp MainActivity" | findstr /i "error fail 失败 错误 exception" > recent_errors.txt
type recent_errors.txt
echo.
echo 完整日志已保存到: recent_errors.txt
echo.

:: 6. 检查打印机管理器状态
echo [6/6] 检查打印机相关日志...
echo ----------------------------------------
echo 查找打印机搜索记录...
adb.exe logcat -d -v time | findstr "找到.*个已配对的打印机"
echo.

echo ================================================================
echo   诊断完成
echo ================================================================
echo.
echo 请提供以下信息以便进一步分析:
echo.
echo 1. 您是否看到 "应用已安装" 的提示?
echo    [ ] 是  [ ] 否
echo.
echo 2. 设备列表中是否显示您的手机?
echo    [ ] 是  [ ] 否
echo.
echo 3. 是否看到 AQ-V 开头的蓝牙设备?
echo    [ ] 是  [ ] 否 - 设备名称: _______________
echo.
echo 4. 具体的失败现象是什么?
echo    [ ] 找不到打印机
echo    [ ] 连接超时
echo    [ ] 打印失败
echo    [ ] 应用崩溃
echo    [ ] 其他: _______________
echo.
echo 5. 打印机 AQ-V258007640 当前状态:
echo    [ ] 已开机
echo    [ ] 已在手机蓝牙设置中配对
echo    [ ] 蓝牙距离 _____ 米内
echo.

pause
