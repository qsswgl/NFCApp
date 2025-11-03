# ========================================
# NFC应用 - 最终修复版安装
# ========================================
# 
# 修复内容：
# 1. NFC适配器的null安全检查
# 2. Room数据库使用kapt编译器
# 3. 升级JVM目标到Java 17
# 4. 移除不存在的drawable资源
#
# ========================================

$adb = "K:\tool\adb\adb.exe"
$apk = "K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "NFC应用 - 安装最终修复版" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 步骤1: 检查设备
Write-Host "[1/6] 检查设备连接..." -ForegroundColor Yellow
& $adb devices
Write-Host ""

# 步骤2: 停止应用
Write-Host "[2/6] 停止应用..." -ForegroundColor Yellow
& $adb shell am force-stop com.nfc.app
Start-Sleep -Seconds 1

# 步骤3: 清除应用数据
Write-Host "[3/6] 清除应用数据..." -ForegroundColor Yellow
& $adb shell pm clear com.nfc.app
Start-Sleep -Seconds 1

# 步骤4: 卸载
Write-Host "[4/6] 卸载旧版本..." -ForegroundColor Yellow
& $adb uninstall com.nfc.app
Start-Sleep -Seconds 2
Write-Host ""

# 步骤5: 安装
Write-Host "[5/6] 安装新版本..." -ForegroundColor Yellow
& $adb install $apk
Write-Host ""

if ($LASTEXITCODE -eq 0) {
    # 步骤6: 启动
    Write-Host "[6/6] 启动应用..." -ForegroundColor Yellow
    & $adb shell am start -n com.nfc.app/.MainActivity
    Start-Sleep -Seconds 2
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "✓ 安装成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "如果应用仍然崩溃，请运行以下命令查看日志：" -ForegroundColor Yellow
    Write-Host "& $adb logcat -d *:E | Select-String 'com.nfc.app'" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "✗ 安装失败" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
}

Write-Host ""
Write-Host "按任意键退出..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
