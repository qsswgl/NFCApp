@echo off
REM ============================================================
REM NFC 读写系统 - 快速环境检查
REM ============================================================

echo.
echo ====== NFC 读写系统 环境检查 ======
echo.

echo [1/4] 检查 Java...
java -version 2>&1
if %errorlevel% neq 0 (
    echo ✗ Java 未找到
) else (
    echo ✓ Java 已安装
)

echo.
echo [2/4] 检查 Android SDK...
if defined ANDROID_HOME (
    echo ✓ ANDROID_HOME: %ANDROID_HOME%
) else (
    echo ✗ ANDROID_HOME 未设置
)

echo.
echo [3/4] 检查 Gradle...
if exist gradlew.bat (
    echo ✓ Gradle Wrapper 已找到
) else (
    echo ✗ Gradle Wrapper 未找到
)

echo.
echo [4/4] 检查 local.properties...
if exist local.properties (
    echo ✓ local.properties 已存在
    echo   内容:
    type local.properties
) else (
    echo ✗ local.properties 未找到
    echo   请复制 local.properties.template 并修改其中的 SDK 路径
)

echo.
echo ====== 检查完成 ======
echo.
pause
