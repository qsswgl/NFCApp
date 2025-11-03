#!/usr/bin/env pwsh
# ============================================================
# NFC 读写系统 - 简化环境检查脚本
# ============================================================

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "  NFC 读写系统 - 环境检查" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

$passed = 0
$failed = 0

# 检查 Java
Write-Host "[1/5] 检查 Java..." -ForegroundColor Yellow
$javaCheck = & {java -version 2>&1} 
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Java 已安装`n" -ForegroundColor Green
    $passed++
} else {
    Write-Host "✗ Java 未找到 - 请从 https://www.oracle.com/java/technologies/downloads/ 下载 JDK 11+`n" -ForegroundColor Red
    $failed++
}

# 检查 Android SDK
Write-Host "[2/5] 检查 Android SDK..." -ForegroundColor Yellow
$sdkPath = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { $env:ANDROID_SDK_ROOT }
if ($sdkPath -and (Test-Path $sdkPath)) {
    Write-Host "✓ Android SDK 已找到: $sdkPath`n" -ForegroundColor Green
    $passed++
} else {
    Write-Host "✗ Android SDK 未找到 - 请安装 Android Studio`n" -ForegroundColor Red
    $failed++
}

# 检查 Gradle
Write-Host "[3/5] 检查 Gradle..." -ForegroundColor Yellow
if (Test-Path "gradlew.bat") {
    Write-Host "✓ Gradle Wrapper 已找到`n" -ForegroundColor Green
    $passed++
} else {
    Write-Host "✗ Gradle Wrapper 未找到`n" -ForegroundColor Red
    $failed++
}

# 检查项目结构
Write-Host "[4/5] 检查项目结构..." -ForegroundColor Yellow
if ((Test-Path "build.gradle.kts") -and (Test-Path "app/build.gradle.kts")) {
    Write-Host "✓ 项目结构完整`n" -ForegroundColor Green
    $passed++
} else {
    Write-Host "✗ 项目结构不完整`n" -ForegroundColor Red
    $failed++
}

# 检查 local.properties
Write-Host "[5/5] 检查 local.properties..." -ForegroundColor Yellow
if (Test-Path "local.properties") {
    Write-Host "✓ local.properties 已配置`n" -ForegroundColor Green
    $passed++
} else {
    Write-Host "⚠ local.properties 未找到 - 需要创建`n" -ForegroundColor Yellow
}

# 总结
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "结果: $passed 项通过, $failed 项失败`n" -ForegroundColor Cyan

if ($failed -eq 0) {
    Write-Host "✓ 环境检查完成！可以开始开发。`n" -ForegroundColor Green
} else {
    Write-Host "✗ 请完成上述配置。`n" -ForegroundColor Red
}
