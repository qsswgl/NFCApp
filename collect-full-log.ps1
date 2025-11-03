# ========================================
# 详细崩溃日志收集脚本
# ========================================

$adb = "K:\tool\adb\adb.exe"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "开始收集详细日志" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 清除旧日志
Write-Host "[1/6] 清除旧日志..." -ForegroundColor Yellow
& $adb logcat -c

# 2. 启动应用
Write-Host "[2/6] 启动应用..." -ForegroundColor Yellow
& $adb shell am start -n com.nfc.app/.MainActivity

# 3. 等待崩溃
Write-Host "[3/6] 等待5秒捕获日志..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 4. 获取FATAL错误
Write-Host "[4/6] 查找FATAL错误..." -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
& $adb logcat -d | Select-String "FATAL|AndroidRuntime" | Select-Object -First 50
Write-Host "========================================" -ForegroundColor Red
Write-Host ""

# 5. 获取应用的所有日志
Write-Host "[5/6] 查找应用日志..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow
& $adb logcat -d | Select-String "com.nfc.app|NFCApp" | Select-Object -Last 50
Write-Host "========================================" -ForegroundColor Yellow
Write-Host ""

# 6. 获取异常堆栈
Write-Host "[6/6] 查找异常堆栈..." -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
& $adb logcat -d | Select-String "Exception|Error|Caused by" | Select-String "com.nfc" -Context 0,3 | Select-Object -Last 30
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""

Write-Host "日志收集完成！" -ForegroundColor Green
Write-Host "请将上述所有输出复制并发给我。" -ForegroundColor Green
Write-Host ""
pause
