# ========================================
# 获取详细崩溃日志
# ========================================

$adb = "K:\tool\adb\adb.exe"

Write-Host "清除旧日志..." -ForegroundColor Yellow
& $adb logcat -c

Write-Host "启动应用..." -ForegroundColor Yellow
& $adb shell am start -n com.nfc.app/.MainActivity

Write-Host "等待5秒捕获崩溃..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

Write-Host "`n========================================" -ForegroundColor Red
Write-Host "崩溃日志 (FATAL):" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
& $adb logcat -d | Select-String "FATAL" -Context 5,20

Write-Host "`n========================================" -ForegroundColor Yellow
Write-Host "异常日志 (Exception):" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow
& $adb logcat -d | Select-String "Exception|Error" | Select-String "com.nfc.app" -Context 2,5

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "完整AndroidRuntime日志:" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
& $adb logcat -d AndroidRuntime:E *:S

Write-Host "`n完成！请将上述日志复制发给我。" -ForegroundColor Green
