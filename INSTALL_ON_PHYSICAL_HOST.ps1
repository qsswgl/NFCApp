# ========================================
# 物理主机快速安装脚本
# ========================================
# 
# 前提：
# 1. 手机已通过USB连接到物理主机
# 2. 已执行 adb tcpip 5555
# 3. APK文件位于虚拟机的共享目录
#
# 使用方法：
# 1. 复制APK到物理主机
# 2. 在PowerShell中执行此脚本中的命令

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "NFC应用 - 物理主机安装指令" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "步骤1: 将APK复制到物理主机" -ForegroundColor Yellow
Write-Host "APK位置: K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk"
Write-Host "建议复制到: C:\Temp\app-debug.apk"
Write-Host ""

Write-Host "步骤2: 在物理主机PowerShell中执行以下命令:" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 设置ADB路径（根据实际情况修改）" -ForegroundColor Gray
Write-Host '$adb = "K:\tool\adb\adb.exe"' -ForegroundColor Green
Write-Host ""

Write-Host "# 检查设备连接" -ForegroundColor Gray
Write-Host '& $adb devices' -ForegroundColor Green
Write-Host ""

Write-Host "# 卸载旧版本（如果存在）" -ForegroundColor Gray
Write-Host '& $adb uninstall com.nfc.app' -ForegroundColor Green
Write-Host ""

Write-Host "# 安装APK（使用虚拟机共享路径）" -ForegroundColor Gray
Write-Host '& $adb install -r "K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk"' -ForegroundColor Green
Write-Host ""

Write-Host "# 或者，如果已复制到本地：" -ForegroundColor Gray
Write-Host '& $adb install -r "C:\Temp\app-debug.apk"' -ForegroundColor Green
Write-Host ""

Write-Host "# 启动应用" -ForegroundColor Gray
Write-Host '& $adb shell am start -n com.nfc.app/.MainActivity' -ForegroundColor Green
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "提示：" -ForegroundColor Yellow
Write-Host "如果K盘在物理主机不可访问，请先将APK复制到物理主机的本地目录"
Write-Host "========================================" -ForegroundColor Cyan
