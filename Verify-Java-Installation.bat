@echo off
REM Verify-Java-Installation.bat
REM Java 安装验证脚本

setlocal enabledelayedexpansion

echo.
echo ============================================
echo Java 安装验证脚本
echo ============================================
echo.

REM 检查 Java 是否已安装
echo [1] 检查 Java 是否已安装...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Java 已安装
    echo.
    echo 版本信息：
    java -version
    echo.
) else (
    echo [ERROR] Java 未找到或未安装
    echo.
    echo 可能的原因：
    echo 1. Java 还未安装
    echo 2. 环境变量未设置
    echo 3. PowerShell 缓存，需要关闭重新打开
    echo.
    pause
    exit /b 1
)

echo [2] 检查 JAVA_HOME 环境变量...
if defined JAVA_HOME (
    echo [OK] JAVA_HOME 已设置
    echo 路径：%JAVA_HOME%
    echo.
) else (
    echo [WARNING] JAVA_HOME 未设置
    echo 但 Java 已可用，可能已添加到 PATH
    echo.
)

echo [3] 检查 Java 可执行文件位置...
for /f "delims=" %%i in ('where java') do (
    echo [OK] Java 路径：%%i
)
echo.

echo ============================================
echo 验证结果：✓ Java 安装成功！
echo ============================================
echo.
echo 下一步：
echo 1. 如果 JAVA_HOME 未设置，请手动设置
echo 2. 设置 Android SDK 路径（ANDROID_HOME）
echo 3. 重启电脑
echo 4. 运行 check_env.bat 进行最终验证
echo.
pause
