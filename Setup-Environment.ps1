#!/usr/bin/env powershell
# Setup-Environment.ps1
# NFC 项目环境一键配置脚本
# 使用方法：powershell -ExecutionPolicy Bypass -File Setup-Environment.ps1

param(
    [switch]$AutoFix = $false  # 是否自动修复配置
)

# 颜色定义
$colors = @{
    Success = "Green"
    Error   = "Red"
    Warning = "Yellow"
    Info    = "Cyan"
}

function Write-Status {
    param([string]$Message, [string]$Type = "Info")
    $color = $colors[$Type]
    Write-Host $Message -ForegroundColor $color
}

function Test-Java {
    Write-Status "`n[1/3] 检查 Java..." -Type "Info"
    
    if (Get-Command java -ErrorAction SilentlyContinue) {
        Write-Status "✅ Java 已安装" -Type "Success"
        java -version 2>&1 | ForEach-Object { Write-Host "   $_" }
        return $true
    } else {
        Write-Status "❌ Java 未安装" -Type "Error"
        Write-Status "📥 请访问下载：https://www.oracle.com/java/technologies/downloads/" -Type "Warning"
        Write-Status "   选择 Java SE 11 或更新版本（Windows x64）" -Type "Warning"
        return $false
    }
}

function Test-AndroidSDK {
    Write-Status "`n[2/3] 检查 Android SDK..." -Type "Info"
    
    if ($env:ANDROID_HOME) {
        Write-Status "✅ ANDROID_HOME 已设置" -Type "Success"
        Write-Host "   路径: $env:ANDROID_HOME"
        
        if (Test-Path "$env:ANDROID_HOME\platform-tools\adb.exe") {
            Write-Status "✅ ADB 可用" -Type "Success"
            
            # 验证 ADB 功能
            $adbVersion = & adb version 2>&1 | Select-Object -First 1
            Write-Host "   $adbVersion"
            return $true
        } else {
            Write-Status "⚠️  ADB 未找到（SDK 可能不完整）" -Type "Warning"
            return $false
        }
    } else {
        Write-Status "❌ ANDROID_HOME 未设置" -Type "Error"
        Write-Status "📥 请访问下载：https://developer.android.com/studio" -Type "Warning"
        Write-Status "   完成 Android Studio 安装后会自动设置" -Type "Warning"
        return $false
    }
}

function Setup-LocalProperties {
    Write-Status "`n[3/3] 配置 local.properties..." -Type "Info"
    
    $projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    $templatePath = Join-Path $projectDir "local.properties.template"
    $localPropsPath = Join-Path $projectDir "local.properties"
    
    if (-not (Test-Path $templatePath)) {
        Write-Status "❌ 找不到 local.properties.template" -Type "Error"
        return $false
    }
    
    try {
        # 复制模板
        Copy-Item $templatePath $localPropsPath -Force
        Write-Status "✅ 已复制 local.properties.template → local.properties" -Type "Success"
        
        # 更新 SDK 路径
        if ($env:ANDROID_HOME) {
            $sdkPath = $env:ANDROID_HOME -replace '\\', '\\'
            $content = Get-Content $localPropsPath -Raw
            $updatedContent = $content -replace 'sdk\.dir\s*=\s*.*', "sdk.dir=$sdkPath"
            Set-Content $localPropsPath -Value $updatedContent -NoNewline
            
            Write-Status "✅ SDK 路径已更新" -Type "Success"
            Write-Host "   文件: $localPropsPath"
            Write-Host "   SDK 路径: $env:ANDROID_HOME"
        }
        
        return $true
    } catch {
        Write-Status "❌ 配置 local.properties 失败：$_" -Type "Error"
        return $false
    }
}

function Test-Environment {
    Write-Status "`n=== 🔍 NFC 项目环境检查 ===" -Type "Info"
    
    $javaOk = Test-Java
    $sdkOk = Test-AndroidSDK
    Setup-LocalProperties
    
    Write-Status "`n=== 📋 检查总结 ===" -Type "Info"
    
    $status = @(
        @{ Item = "Java"; Status = $javaOk }
        @{ Item = "Android SDK"; Status = $sdkOk }
    )
    
    foreach ($check in $status) {
        $icon = if ($check.Status) { "✅" } else { "❌" }
        $color = if ($check.Status) { "Success" } else { "Error" }
        Write-Status "$icon $($check.Item)" -Type $color
    }
    
    return ($javaOk -and $sdkOk)
}

function Show-NextSteps {
    Write-Status "`n=== 🚀 后续步骤 ===" -Type "Info"
    Write-Host @"
1️⃣  如果上面有 ❌ 项，请先完成相应的安装：
    • Java: https://www.oracle.com/java/technologies/downloads/
    • Android Studio: https://developer.android.com/studio

2️⃣  重启电脑（重要！环境变量需要重新加载）

3️⃣  重启后，验证配置：
    cd k:\NFC\NFCApp
    .\check_env.bat

4️⃣  所有检查通过后，在 VS Code 中开始开发：
    code k:\NFC\NFCApp

5️⃣  构建和运行应用：
    Ctrl+Shift+P → Tasks: Run Task → Build, Install and Run

"@
}

# 主程序
Write-Host @"

╔════════════════════════════════════════════════════════════╗
║                                                            ║
║       🔧 NFC 项目环境配置脚本                             ║
║                                                            ║
║       这个脚本将检查并配置开发环境所需的所有工具         ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝

"@ -ForegroundColor Cyan

$allOk = Test-Environment
Show-NextSteps

if ($allOk) {
    Write-Status "`n✨ 所有检查都通过了！请重启电脑以使环境变量生效。" -Type "Success"
    exit 0
} else {
    Write-Status "`n⚠️  还有未完成的项目，请按上面的步骤完成安装。" -Type "Warning"
    exit 1
}
