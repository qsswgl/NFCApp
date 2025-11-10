# BLE Scan Test Script
Write-Host "==================== BLE Scan Test ====================" -ForegroundColor Cyan
Write-Host "1. Clear logs..." -ForegroundColor Yellow
.\adb logcat -c

Write-Host "2. Start app..." -ForegroundColor Yellow
.\adb shell am start -n com.nfc.app/.MainActivity
Start-Sleep -Seconds 2

Write-Host "3. Grant permissions..." -ForegroundColor Yellow
.\adb shell pm grant com.nfc.app android.permission.BLUETOOTH_SCAN 2>$null
.\adb shell pm grant com.nfc.app android.permission.BLUETOOTH_CONNECT 2>$null
.\adb shell pm grant com.nfc.app android.permission.ACCESS_FINE_LOCATION 2>$null

Write-Host "`nPlease click [BLE Scan] button on phone..." -ForegroundColor Green
Write-Host "Waiting 12 seconds..." -ForegroundColor Yellow
Start-Sleep -Seconds 12

Write-Host "`n4. Capture logs..." -ForegroundColor Yellow
$log = .\adb logcat -d

Write-Host "`n==================== BLEPrinter Logs ====================" -ForegroundColor Cyan
$bleLog = $log | Select-String "BLEPrinter"
if ($bleLog) {
    $bleLog | ForEach-Object { Write-Host $_.Line }
}
else {
    Write-Host "No BLEPrinter logs found!" -ForegroundColor Red
}

Write-Host "`n==================== Permission Logs ====================" -ForegroundColor Cyan
$permLog = $log | Select-String "Permission|BLUETOOTH|nfc.app" | Select-Object -Last 30
if ($permLog) {
    $permLog | ForEach-Object { Write-Host $_.Line }
}

Write-Host "`n==================== Error Logs ====================" -ForegroundColor Cyan
$errorLog = $log | Select-String " E " | Select-String "nfc.app|BLE|Bluetooth" | Select-Object -Last 20
if ($errorLog) {
    $errorLog | ForEach-Object { Write-Host $_.Line -ForegroundColor Red }
}
else {
    Write-Host "No obvious errors found" -ForegroundColor Green
}

Write-Host "`n==================== Test Complete ====================" -ForegroundColor Cyan
