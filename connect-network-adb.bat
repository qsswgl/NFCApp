@echo off
chcp 65001 >nul
echo ==========================================
echo 虚拟机 - 通过网络连接Android设备
echo ==========================================
echo.

set ADB=C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe

echo 使用说明：
echo 1. 确保手机和虚拟机在同一网络
echo 2. 在物理主机上已通过USB启用 "adb tcpip 5555"
echo 3. 输入手机的IP地址
echo.
echo ==========================================
echo.

set /p PHONE_IP=请输入手机IP地址（例如：192.168.1.100）: 

if "%PHONE_IP%"=="" (
    echo [错误] 未输入IP地址！
    pause
    exit /b 1
)

echo.
echo [步骤1] 尝试连接到 %PHONE_IP%:5555 ...
%ADB% connect %PHONE_IP%:5555
echo.

echo [步骤2] 检查连接状态...
%ADB% devices
echo.

echo ==========================================
echo 如果上方显示 "%PHONE_IP%:5555    device"
echo 则连接成功！现在可以正常安装应用了。
echo.
echo 运行以下命令安装应用：
echo .\install-and-run.bat
echo ==========================================
echo.

pause
