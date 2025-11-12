# 物理机安装命令 - PowerShell
# 使用方法: 在物理机 PowerShell 中运行此脚本

Write-Host "========== NFC APP 安装工具 ==========" -ForegroundColor Cyan
Write-Host ""

# 1. 检查设备连接
Write-Host "步骤1: 检查设备连接..." -ForegroundColor Yellow
.\adb devices
Write-Host ""

# 2. 卸载旧版本
Write-Host "步骤2: 卸载旧版本..." -ForegroundColor Yellow
.\adb uninstall com.nfc.app
Write-Host ""

# 3. 安装新版本
Write-Host "步骤3: 安装新版本..." -ForegroundColor Yellow
$apkPath = "K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk"

if (Test-Path $apkPath) {
    Write-Host "APK路径: $apkPath" -ForegroundColor Green
    .\adb install $apkPath
} else {
    Write-Host "❌ APK文件不存在: $apkPath" -ForegroundColor Red
    Write-Host "请将APK复制到物理机,或使用本地路径" -ForegroundColor Yellow
    Write-Host "示例: .\adb install `"C:\Users\Administrator\Desktop\app-debug.apk`"" -ForegroundColor Cyan
}
Write-Host ""

# 4. 启动APP
Write-Host "步骤4: 启动APP..." -ForegroundColor Yellow
.\adb shell am start -n com.nfc.app/.MainActivity
Write-Host ""

Write-Host "========== 安装完成 ==========" -ForegroundColor Green
Write-Host "提示: 如需查看日志,运行:" -ForegroundColor Cyan
Write-Host "  .\adb logcat -v time PuQuPrinterManager:D NFCApp:D AndroidRuntime:E *:S" -ForegroundColor White
