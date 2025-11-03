@echo off
REM ============================================================
REM NFC 读写系统 - 环境检查和配置脚本
REM ============================================================
REM 此脚本将检查所有必要的开发工具和环境变量

echo.
echo ============================================================
echo   NFC 读写系统 - 开发环境检查工具
echo ============================================================
echo.

setlocal enabledelayedexpansion

REM 初始化计数器
set /a passed=0
set /a failed=0

REM ============================================================
REM 1. 检查 Java
REM ============================================================
echo [1/6] 检查 Java Development Kit...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    for /f tokens^=2 %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VER=%%i
    echo ✓ Java 已安装: !JAVA_VER!
    set /a passed+=1
) else (
    echo ✗ Java 未安装或未添加到 PATH
    echo   请从 https://www.oracle.com/java/technologies/downloads/ 下载 JDK 11+
    echo   安装后将 JAVA_HOME 添加到系统环境变量
    set /a failed+=1
)

REM ============================================================
REM 2. 检查 Android SDK
REM ============================================================
echo.
echo [2/6] 检查 Android SDK...
if exist "%ANDROID_HOME%" (
    echo ✓ Android SDK 已找到: %ANDROID_HOME%
    set /a passed+=1
) else if exist "%ANDROID_SDK_ROOT%" (
    echo ✓ Android SDK 已找到: %ANDROID_SDK_ROOT%
    set /a passed+=1
) else (
    echo ✗ Android SDK 未找到
    echo   请从 https://developer.android.com/studio 下载 Android Studio
    echo   安装后将 ANDROID_HOME 添加到系统环境变量
    set /a failed+=1
)

REM ============================================================
REM 3. 检查 Gradle
REM ============================================================
echo.
echo [3/6] 检查 Gradle...
gradle --version >nul 2>&1
if %errorlevel% equ 0 (
    for /f %%i in ('gradle --version ^| find "Gradle"') do set GRADLE_VER=%%i
    echo ✓ Gradle 已安装: !GRADLE_VER!
    set /a passed+=1
) else (
    echo ℹ Gradle 未全局安装 (这是正常的，项目包含 Gradle Wrapper)
    if exist "gradlew.bat" (
        echo ✓ Gradle Wrapper 已找到: gradlew.bat
        set /a passed+=1
    ) else (
        echo ✗ Gradle Wrapper 未找到
        set /a failed+=1
    )
)

REM ============================================================
REM 4. 检查 ADB
REM ============================================================
echo.
echo [4/6] 检查 Android Debug Bridge (ADB)...
adb version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ ADB 已安装
    echo   已连接的设备:
    adb devices
    set /a passed+=1
) else (
    echo ✗ ADB 未找到
    echo   ADB 通常包含在 Android SDK Platform Tools 中
    echo   请确保已安装 Android SDK 并将其 platform-tools 文件夹添加到 PATH
    set /a failed+=1
)

REM ============================================================
REM 5. 检查项目结构
REM ============================================================
echo.
echo [5/6] 检查项目结构...
set all_files_found=1

if not exist "build.gradle.kts" (
    echo ✗ build.gradle.kts 未找到
    set all_files_found=0
)
if not exist "settings.gradle.kts" (
    echo ✗ settings.gradle.kts 未找到
    set all_files_found=0
)
if not exist "local.properties" (
    echo ✗ local.properties 未找到
    echo   (这可能需要创建 - 见下面的配置说明)
    set all_files_found=0
)
if not exist "app\build.gradle.kts" (
    echo ✗ app\build.gradle.kts 未找到
    set all_files_found=0
)

if !all_files_found! equ 1 (
    echo ✓ 项目结构完整
    set /a passed+=1
) else (
    echo ✗ 某些项目文件缺失
    set /a failed+=1
)

REM ============================================================
REM 6. 检查 local.properties
REM ============================================================
echo.
echo [6/6] 检查 local.properties 配置...
if exist "local.properties" (
    echo ✓ local.properties 已找到
    echo   内容预览:
    type local.properties
    set /a passed+=1
) else (
    echo ✗ local.properties 未找到
    echo   需要创建 local.properties 文件，设置 Android SDK 路径
    echo   参考下面的配置说明
    set /a failed+=1
)

REM ============================================================
REM 显示总结
REM ============================================================
echo.
echo ============================================================
echo   检查结果总结
echo ============================================================
echo   通过: !passed! 项
echo   失败: !failed! 项
echo.

if !failed! equ 0 (
    echo ✓ 所有检查通过！您可以开始开发了。
    echo.
    echo 后续步骤:
    echo   1. 在 VS Code 中打开项目: k:\NFC\NFCApp
    echo   2. 按 Ctrl+Shift+P 打开任务运行器
    echo   3. 选择 "Build, Install and Run" 构建并运行应用
    echo.
) else (
    echo ✗ 还有 !failed! 项需要配置。
    echo.
    echo 请按照上面的提示进行配置。
    echo.
)

echo ============================================================
echo 快速配置指南
echo ============================================================
echo.
echo [配置 Java]
echo   1. 从 Oracle 官网下载 JDK 11 或更新版本
echo   2. 安装到 C:\Program Files\Java\jdk-xx
echo   3. 添加系统环境变量:
echo      JAVA_HOME = C:\Program Files\Java\jdk-xx
echo   4. 将 %%JAVA_HOME%%\bin 添加到 PATH
echo.

echo [配置 Android SDK]
echo   1. 从 Android Studio 官网下载 Android Studio
echo   2. 安装 Android Studio 并完成初始设置
echo   3. 添加系统环境变量:
echo      ANDROID_HOME = C:\Users\用户名\AppData\Local\Android\sdk
echo   4. 将 %%ANDROID_HOME%%\platform-tools 添加到 PATH
echo.

echo [配置 local.properties]
echo   1. 在项目根目录创建 local.properties 文件
echo   2. 添加一行: sdk.dir=你的Android SDK路径
echo   例如: sdk.dir=C:\Users\用户名\AppData\Local\Android\sdk
echo.

echo [配置完成后验证]
echo   1. 重启 PowerShell/CMD
echo   2. 运行此脚本确认所有检查通过
echo   3. 在 VS Code 中打开项目开始开发
echo.

echo ============================================================
pause
