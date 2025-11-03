@echo off
REM Pre-Setup-Check.bat
REM 在安装前检查系统是否已做好准备

setlocal enabledelayedexpansion

echo.
echo =====================================================
echo NFC 项目 - 安装前环境检查
echo =====================================================
echo.
echo 本脚本检查以下项目是否已准备好：
echo  □ Java JDK
echo  □ Android Studio / SDK
echo  □ 环境变量配置
echo.
echo =====================================================
echo.

REM 计数器
set /a totalChecks=0
set /a passedChecks=0

REM ===== 检查 1: Java =====
echo [检查 1/5] Java 安装状态...
if exist "C:\Program Files\Java" (
    echo   ✓ 找到 Java 安装文件夹
    set /a passedChecks+=1
) else (
    echo   ✗ 未找到 Java
    echo   📥 请从以下地址下载：
    echo      https://www.oracle.com/java/technologies/downloads/
    echo      选择 Java SE 11 或更新版本（Windows x64 Installer）
)
set /a totalChecks+=1

echo.

REM ===== 检查 2: JAVA_HOME =====
echo [检查 2/5] JAVA_HOME 环境变量...
if defined JAVA_HOME (
    echo   ✓ JAVA_HOME 已设置
    echo   路径：%JAVA_HOME%
    
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo   ✓ java.exe 已找到
        set /a passedChecks+=1
    ) else (
        echo   ✗ java.exe 未找到（路径可能不正确）
    )
) else (
    echo   ✗ JAVA_HOME 未设置
    echo   📝 需要手动设置：
    echo      1. 右键"此电脑" → 属性
    echo      2. 点击"高级系统设置"
    echo      3. 点击"环境变量"
    echo      4. 新建系统变量：JAVA_HOME = C:\Program Files\Java\jdk-XX
    echo      5. 重启电脑
)
set /a totalChecks+=1

echo.

REM ===== 检查 3: Android SDK =====
echo [检查 3/5] Android SDK 安装状态...
if exist "%USERPROFILE%\AppData\Local\Android\sdk" (
    echo   ✓ 找到 Android SDK 文件夹
    set /a passedChecks+=1
) else if exist "C:\Android\sdk" (
    echo   ✓ 找到 Android SDK 文件夹
    set /a passedChecks+=1
) else (
    echo   ✗ 未找到 Android SDK
    echo   📥 请安装 Android Studio：
    echo      https://developer.android.com/studio
    echo      完成安装和初始化即可自动安装 SDK
)
set /a totalChecks+=1

echo.

REM ===== 检查 4: ANDROID_HOME =====
echo [检查 4/5] ANDROID_HOME 环境变量...
if defined ANDROID_HOME (
    echo   ✓ ANDROID_HOME 已设置
    echo   路径：%ANDROID_HOME%
    
    if exist "%ANDROID_HOME%\platform-tools\adb.exe" (
        echo   ✓ ADB 已找到
        set /a passedChecks+=1
    ) else (
        echo   ✗ ADB 未找到（SDK 可能不完整）
        echo      请在 Android Studio 中完成 SDK 平台工具安装
    )
) else (
    echo   ✗ ANDROID_HOME 未设置
    echo   📝 需要手动设置：
    echo      1. 打开 Android Studio
    echo      2. Tools → SDK Manager
    echo      3. 复制顶部显示的 "Android SDK Location" 路径
    echo      4. 右键"此电脑" → 属性
    echo      5. 点击"高级系统设置" → 环境变量
    echo      6. 新建系统变量：ANDROID_HOME = （粘贴上述路径）
    echo      7. 重启电脑
)
set /a totalChecks+=1

echo.

REM ===== 检查 5: PATH 配置 =====
echo [检查 5/5] PATH 环境变量配置...
setlocal enabledelayedexpansion
if "!PATH:JAVA_HOME=!" neq "!PATH!" (
    echo   ✓ PATH 中包含 Java 配置
    set /a passedChecks+=1
) else if exist "C:\Program Files\Java" (
    echo   ⚠ 未在 PATH 中检测到 Java 路径
    echo      建议添加：%%JAVA_HOME%%\bin 到 PATH
) else (
    echo   ℹ Java 未安装，跳过检查
)

endlocal

set /a totalChecks+=1

echo.
echo =====================================================
echo 检查总结：%passedChecks% / %totalChecks% 项已就绪
echo =====================================================
echo.

if %passedChecks% equ %totalChecks% (
    echo ✅ 太棒了！所有检查都通过了！
    echo.
    echo 你现在可以：
    echo  1. 运行 Setup-LocalProperties.bat 配置 SDK 路径
    echo  2. 运行 check_env.bat 做最终验证
    echo  3. 在 VS Code 中开始开发
) else (
    echo ❌ 还有 !passedChecks! 项未完成
    echo.
    echo 请按上面的说明完成：
    echo  1. 安装 Java
    echo  2. 安装 Android Studio
    echo  3. 设置环境变量
    echo  4. 重启电脑
    echo.
    echo 完成后重新运行此脚本检查。
)

echo.
echo 📚 更多帮助，请查看：
echo    SETUP_GUIDE.md - 详细的配置步骤
echo    QUICK_SETUP.md - 快速检查清单
echo.
pause
