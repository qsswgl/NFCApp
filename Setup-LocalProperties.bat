@echo off
REM Setup-LocalProperties.bat
REM 自动配置 local.properties 的批处理脚本

setlocal enabledelayedexpansion

echo.
echo ============================================
echo NFC 项目 local.properties 自动配置
echo ============================================
echo.

REM 获取脚本所在目录
set "SCRIPT_DIR=%~dp0"

REM 检查模板文件
if not exist "%SCRIPT_DIR%local.properties.template" (
    echo [错误] 找不到 local.properties.template
    echo.
    pause
    exit /b 1
)

REM 复制模板
echo [1/3] 复制模板文件...
copy "%SCRIPT_DIR%local.properties.template" "%SCRIPT_DIR%local.properties" >nul
if errorlevel 1 (
    echo [错误] 无法复制文件
    pause
    exit /b 1
)
echo [✓] 已复制 local.properties

REM 更新 SDK 路径
echo [2/3] 配置 Android SDK 路径...

if defined ANDROID_HOME (
    REM 将路径中的 \ 替换为 \\
    set "SDK_PATH=!ANDROID_HOME:\=\\!"
    
    REM 使用 PowerShell 修改文件（更可靠）
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "$content = Get-Content '%SCRIPT_DIR%local.properties'; " ^
        "$content -replace 'sdk\.dir\s*=\s*.*', ('sdk.dir=' + '!SDK_PATH!') | Set-Content '%SCRIPT_DIR%local.properties'" >nul
    
    if errorlevel 1 (
        echo [✓] 已手动更新 local.properties
        echo.
        echo [信息] 如果自动配置失败，请手动编辑：
        echo        %SCRIPT_DIR%local.properties
        echo.
        echo        将此行：
        echo        sdk.dir=C:\Users\YourUsername\AppData\Local\Android\sdk
        echo.
        echo        改为（注意使用 \\ 作为分隔符）：
        echo        sdk.dir=!SDK_PATH!
    ) else (
        echo [✓] SDK 路径已配置
        echo    路径：!ANDROID_HOME!
    )
) else (
    echo [警告] 未检测到 ANDROID_HOME 环境变量
    echo        请先：
    echo        1. 安装 Android Studio
    echo        2. 设置 ANDROID_HOME 环境变量
    echo        3. 重启电脑
    echo        4. 重新运行此脚本
    echo.
    pause
    exit /b 1
)

REM 验证配置
echo [3/3] 验证配置...
if exist "%SCRIPT_DIR%local.properties" (
    echo [✓] local.properties 已成功配置
    echo.
    echo 文件内容预览：
    echo ----------------------------------------
    type "%SCRIPT_DIR%local.properties"
    echo ----------------------------------------
    echo.
    echo [✓] 配置完成！
    echo.
    echo 下一步：
    echo 1. 重启电脑
    echo 2. 运行 check_env.bat 验证环境
    echo 3. 打开 VS Code 开始开发
) else (
    echo [错误] local.properties 创建失败
    pause
    exit /b 1
)

echo.
pause
