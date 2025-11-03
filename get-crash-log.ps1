# ========================================
# 获取应用崩溃日志
# ========================================
# 请在物理主机的PowerShell中执行以下命令

$adb = "K:\tool\adb\adb.exe"

Write-Host "清除旧日志..." -ForegroundColor Yellow
& $adb logcat -c

Write-Host "启动应用并捕获日志..." -ForegroundColor Yellow
& $adb shell am start -n com.nfc.app/.MainActivity

Write-Host "等待3秒..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

Write-Host "获取崩溃日志..." -ForegroundColor Yellow
& $adb logcat -d *:E | Select-String "com.nfc.app|AndroidRuntime|FATAL"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "完整日志（包含所有级别）：" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
& $adb logcat -d | Select-String "com.nfc.app" | Select-Object -Last 50
