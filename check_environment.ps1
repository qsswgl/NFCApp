#!/usr/bin/env pwsh
# ============================================================
# NFC 读写系统 - 环境检查脚本 (PowerShell 版)
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  NFC 读写系统 - 开发环境检查工具 (PowerShell)" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

$passed = 0
$failed = 0
$warnings = 0

# ============================================================
# 1. 检查 Java
# ============================================================
Write-Host "[1/6] 检查 Java Development Kit..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "✓ Java 已安装: $javaVersion" -ForegroundColor Green
        $passed++
    }
} catch {
    Write-Host "✗ Java 未安装或未添加到 PATH" -ForegroundColor Red
    Write-Host "  请从 https://www.oracle.com/java/technologies/downloads/ 下载 JDK 11+" -ForegroundColor Gray
    Write-Host "  安装后将 JAVA_HOME 添加到系统环境变量" -ForegroundColor Gray
    $failed++
}

# ============================================================
# 2. 检查 Android SDK
# ============================================================
Write-Host ""
Write-Host "[2/6] 检查 Android SDK..." -ForegroundColor Yellow

$androidHome = $env:ANDROID_HOME
$androidSdkRoot = $env:ANDROID_SDK_ROOT

if ($androidHome -and (Test-Path $androidHome)) {
    Write-Host "✓ Android SDK 已找到: $androidHome" -ForegroundColor Green
    $passed++
} elseif ($androidSdkRoot -and (Test-Path $androidSdkRoot)) {
    Write-Host "✓ Android SDK 已找到: $androidSdkRoot" -ForegroundColor Green
    $passed++
} else {
    Write-Host "✗ Android SDK 未找到" -ForegroundColor Red
    Write-Host "  请从 https://developer.android.com/studio 下载 Android Studio" -ForegroundColor Gray
    Write-Host "  安装后将 ANDROID_HOME 添加到系统环境变量" -ForegroundColor Gray
    $failed++
}

# ============================================================
# 3. 检查 Gradle
# ============================================================
Write-Host ""
Write-Host "[3/6] 检查 Gradle..." -ForegroundColor Yellow

try {
    $gradleVersion = gradle --version 2>&1 | Select-String "Gradle"
    if ($gradleVersion) {
        Write-Host "✓ Gradle 已安装: $gradleVersion" -ForegroundColor Green
        $passed++
    }
} catch {
    Write-Host "ℹ Gradle 未全局安装 (这是正常的，项目包含 Gradle Wrapper)" -ForegroundColor Cyan
    if (Test-Path "gradlew.bat") {
        Write-Host "✓ Gradle Wrapper 已找到: gradlew.bat" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "✗ Gradle Wrapper 未找到" -ForegroundColor Red
        $failed++
    }
}

# ============================================================
# 4. 检查 ADB
# ============================================================
Write-Host ""
Write-Host "[4/6] 检查 Android Debug Bridge (ADB)..." -ForegroundColor Yellow

try {
    $adbVersion = adb version 2>&1 | Select-String "version"
    if ($adbVersion) {
        Write-Host "✓ ADB 已安装" -ForegroundColor Green
        Write-Host "  已连接的设备:" -ForegroundColor Gray
        adb devices | Select-Object -Skip 1 | ForEach-Object {
            if ($_ -match "\S") {
                Write-Host "  $_" -ForegroundColor Gray
            }
        }
        $passed++
    }
}
catch {
    Write-Host "✗ ADB 未找到" -ForegroundColor Red
    Write-Host "  ADB 通常包含在 Android SDK Platform Tools 中" -ForegroundColor Gray
    Write-Host "  请确保已安装 Android SDK 并将其 platform-tools 文件夹添加到 PATH" -ForegroundColor Gray
    $failed++
}

# ============================================================
# 5. 检查项目结构
# ============================================================
Write-Host ""
Write-Host "[5/6] 检查项目结构..." -ForegroundColor Yellow

$allFilesFound = $true
$requiredFiles = @(
    "build.gradle.kts",
    "settings.gradle.kts",
    "app\build.gradle.kts",
    "app\src\main\AndroidManifest.xml"
)

foreach ($file in $requiredFiles) {
    if (-not (Test-Path $file)) {
        Write-Host "✗ $file 未找到" -ForegroundColor Red
        $allFilesFound = $false
    }
}

if (-not (Test-Path "local.properties")) {
    Write-Host "⚠ local.properties 未找到 (需要配置)" -ForegroundColor Yellow
    $warnings++
}

if ($allFilesFound) {
    Write-Host "✓ 项目结构完整" -ForegroundColor Green
    $passed++
} else {
    Write-Host "✗ 某些项目文件缺失" -ForegroundColor Red
    $failed++
}

# ============================================================
# 6. 检查 local.properties
# ============================================================
Write-Host ""
Write-Host "[6/6] 检查 local.properties 配置..." -ForegroundColor Yellow

if (Test-Path "local.properties") {
    Write-Host "✓ local.properties 已找到" -ForegroundColor Green
    Write-Host "  内容预览:" -ForegroundColor Gray
    Get-Content "local.properties" | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
    $passed++
} else {
    Write-Host "✗ local.properties 未找到" -ForegroundColor Red
    Write-Host "  需要创建 local.properties 文件，设置 Android SDK 路径" -ForegroundColor Gray
    $failed++
}

# ============================================================
# 显示总结
# ============================================================
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  检查结果总结" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  通过: $passed 项" -ForegroundColor Green
Write-Host "  失败: $failed 项" -ForegroundColor Red
Write-Host "  警告: $warnings 项" -ForegroundColor Yellow
Write-Host ""

if ($failed -eq 0 -and $warnings -eq 0) {
    Write-Host "✓ 所有检查通过！您可以开始开发了。" -ForegroundColor Green
    Write-Host ""
    Write-Host "后续步骤:" -ForegroundColor Cyan
    Write-Host "  1. 在 VS Code 中打开项目: k:\NFC\NFCApp" -ForegroundColor Gray
    Write-Host "  2. 按 Ctrl+Shift+P 打开任务运行器" -ForegroundColor Gray
    Write-Host "  3. 选择 'Build, Install and Run' 构建并运行应用" -ForegroundColor Gray
    Write-Host ""
} elseif ($failed -eq 0 -and $warnings -gt 0) {
    Write-Host "✓ 主要检查通过，但还有 $warnings 项警告需要处理。" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "请按照下面的配置说明完成设置。" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host "✗ 还有 $failed 项需要配置。" -ForegroundColor Red
    Write-Host ""
    Write-Host "请按照下面的配置说明进行设置。" -ForegroundColor Cyan
    Write-Host ""
}

# ============================================================
# 快速配置指南
# ============================================================
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "快速配置指南" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[配置 Java]" -ForegroundColor Yellow
Write-Host "  1. 从 Oracle 官网下载 JDK 11 或更新版本" -ForegroundColor Gray
Write-Host "  2. 安装到 C:\Program Files\Java\jdk-xx" -ForegroundColor Gray
Write-Host "  3. 添加系统环境变量:" -ForegroundColor Gray
Write-Host "     JAVA_HOME = C:\Program Files\Java\jdk-xx" -ForegroundColor Gray
Write-Host "  4. 将 %JAVA_HOME%\bin 添加到 PATH" -ForegroundColor Gray
Write-Host ""

Write-Host "[配置 Android SDK]" -ForegroundColor Yellow
Write-Host "  1. 从 Android Studio 官网下载 Android Studio" -ForegroundColor Gray
Write-Host "  2. 安装 Android Studio 并完成初始设置" -ForegroundColor Gray
Write-Host "  3. 添加系统环境变量:" -ForegroundColor Gray
Write-Host "     ANDROID_HOME = C:\Users\用户名\AppData\Local\Android\sdk" -ForegroundColor Gray
Write-Host "  4. 将 %ANDROID_HOME%\platform-tools 添加到 PATH" -ForegroundColor Gray
Write-Host ""

Write-Host "[配置 local.properties]" -ForegroundColor Yellow
Write-Host "  1. 在项目根目录创建 local.properties 文件" -ForegroundColor Gray
Write-Host "  2. 添加一行: sdk.dir=你的Android SDK路径" -ForegroundColor Gray
Write-Host "  例如: sdk.dir=C:\Users\用户名\AppData\Local\Android\sdk" -ForegroundColor Gray
Write-Host ""

Write-Host "[配置完成后验证]" -ForegroundColor Yellow
Write-Host "  1. 重启 PowerShell" -ForegroundColor Gray
Write-Host "  2. 运行此脚本确认所有检查通过" -ForegroundColor Gray
Write-Host "  3. 在 VS Code 中打开项目开始开发" -ForegroundColor Gray
Write-Host ""

Write-Host "============================================================" -ForegroundColor Cyan
