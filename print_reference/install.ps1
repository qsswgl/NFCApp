# BLE打印机 - 快速构建安装脚本

Write-Host "================================" -ForegroundColor Cyan
Write-Host "BLE蓝牙打印机 - 快速安装" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 检查设备连接
Write-Host "检查Android设备连接..." -ForegroundColor Yellow
$devices = adb devices | Select-String "device$"
if ($devices.Count -eq 0) {
    Write-Host "❌ 未检测到Android设备，请连接设备后重试" -ForegroundColor Red
    exit 1
}
Write-Host "✅ 检测到Android设备" -ForegroundColor Green
Write-Host ""

# 提示用户选择构建方式
Write-Host "请选择构建方式:" -ForegroundColor Yellow
Write-Host "1. 使用Android Studio (推荐)"
Write-Host "2. 使用命令行构建 (需要配置好的Gradle)"
Write-Host "3. 只查看安装说明"
Write-Host ""

$choice = Read-Host "请输入选择 (1-3)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "使用Android Studio安装步骤:" -ForegroundColor Cyan
        Write-Host "1. 打开Android Studio" -ForegroundColor White
        Write-Host "2. File -> Open -> 选择 k:\Print" -ForegroundColor White
        Write-Host "3. 等待Gradle同步完成" -ForegroundColor White
        Write-Host "4. 点击运行按钮 ▶️ (Shift+F10)" -ForegroundColor White
        Write-Host ""
        Write-Host "是否现在打开项目目录？(Y/N)" -ForegroundColor Yellow
        $openDir = Read-Host
        if ($openDir -eq "Y" -or $openDir -eq "y") {
            explorer "k:\Print"
        }
    }
    "2" {
        Write-Host ""
        Write-Host "尝试使用Gradle构建..." -ForegroundColor Yellow
        
        # 检查是否有gradlew.bat
        if (Test-Path ".\gradlew.bat") {
            Write-Host "使用项目Gradle Wrapper..." -ForegroundColor Yellow
            .\gradlew.bat assembleDebug
        } else {
            Write-Host "使用系统Gradle..." -ForegroundColor Yellow
            gradle assembleDebug
        }
        
        # 检查APK是否生成
        $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
        if (Test-Path $apkPath) {
            Write-Host "✅ APK构建成功！" -ForegroundColor Green
            Write-Host "正在安装到设备..." -ForegroundColor Yellow
            adb install -r $apkPath
            Write-Host ""
            Write-Host "✅ 安装完成！请在设备上查找'BLE打印机'应用" -ForegroundColor Green
        } else {
            Write-Host "❌ APK构建失败，请使用Android Studio构建" -ForegroundColor Red
        }
    }
    "3" {
        Write-Host ""
        notepad "INSTALL.md"
    }
    default {
        Write-Host "无效选择" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "详细说明请查看: INSTALL.md" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
