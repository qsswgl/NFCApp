# Complete BLE Test Script
Write-Host "`n==================== Building APK ====================" -ForegroundColor Cyan
& .\gradlew.bat assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED!" -ForegroundColor Red
    exit 1
}

Write-Host "`n==================== Installing APK ====================" -ForegroundColor Cyan
.\adb install -r .\app\build\outputs\apk\debug\app-debug.apk

Write-Host "`n==================== Granting Permissions ====================" -ForegroundColor Cyan
.\adb shell pm grant com.nfc.app android.permission.BLUETOOTH_SCAN 2>$null
.\adb shell pm grant com.nfc.app android.permission.BLUETOOTH_CONNECT 2>$null
.\adb shell pm grant com.nfc.app android.permission.ACCESS_FINE_LOCATION 2>$null

Write-Host "`n==================== Clear Logs ====================" -ForegroundColor Cyan
.\adb logcat -c

Write-Host "`n==================== Launching App ====================" -ForegroundColor Cyan
.\adb shell am start -n com.nfc.app/.MainActivity
Start-Sleep -Seconds 3

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "Please click [BLE Scan] button now!" -ForegroundColor Green -BackgroundColor Black
Write-Host "========================================`n" -ForegroundColor Green
Write-Host "Waiting 15 seconds for scanning..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host "`n==================== Capturing Logs ====================" -ForegroundColor Cyan
$log = .\adb logcat -d

Write-Host "`n==================== BLEPrinter Logs ====================" -ForegroundColor Cyan
$bleLog = $log | Select-String "BLEPrinter|NFCApp.*BLE|===== BLE"
if ($bleLog) {
    $bleLog | ForEach-Object { 
        $line = $_.Line
        if ($line -match "error|fail|Error|失败") {
            Write-Host $line -ForegroundColor Red
        } elseif ($line -match "success|Success|成功|发现") {
            Write-Host $line -ForegroundColor Green
        } else {
            Write-Host $line
        }
    }
} else {
    Write-Host "NO BLEPrinter logs found - scanning may not have started!" -ForegroundColor Red
}

Write-Host "`n==================== Error Logs ====================" -ForegroundColor Cyan
$errorLog = $log | Select-String " E " | Select-String "nfc.app|BLE|Bluetooth" | Select-Object -Last 15
if ($errorLog) {
    $errorLog | ForEach-Object { Write-Host $_.Line -ForegroundColor Red }
} else {
    Write-Host "No errors detected" -ForegroundColor Green
}

Write-Host "`n==================== Test Complete ====================" -ForegroundColor Cyan
