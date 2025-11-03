#!/usr/bin/env powershell
# Auto-Setup-Environment.ps1
# NFC Project - 完整的 Java 和 Android SDK 自动化配置脚本

param(
    [switch]$SkipAndroid = $false,  # 跳过 Android Studio 安装
    [switch]$NoRestart = $false      # 不提示重启
)

# 设置变量
$ProgressPreference = 'SilentlyContinue'
$ErrorActionPreference = 'Continue'

$JAVA_VERSION = "17"
$JAVA_DOWNLOAD_URL = "https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe"
$JAVA_INSTALL_PATH = "C:\Program Files\Java\jdk-17"
$ANDROID_STUDIO_URL = "https://redirector.gvt1.com/edgedl/android/studio/install/2024.1.1.31/android-studio-2024.1.1.31-windows.exe"
$DOWNLOAD_DIR = "$env:TEMP\NFC_Setup"

# 颜色定义
$Colors = @{
    Success = "Green"
    Error   = "Red"
    Warning = "Yellow"
    Info    = "Cyan"
}

function Write-Status {
    param([string]$Message, [string]$Type = "Info")
    $color = $Colors[$Type]
    Write-Host $Message -ForegroundColor $color
}

function Test-Admin {
    $currentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object System.Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([System.Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Download-File {
    param(
        [string]$Url,
        [string]$OutPath
    )
    
    try {
        [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12
        Write-Host "   下载中... " -NoNewline
        $webClient = New-Object System.Net.WebClient
        $webClient.DownloadFile($Url, $OutPath)
        Write-Host "完成 ✓" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "失败 ✗" -ForegroundColor Red
        Write-Host "   错误: $_"
        return $false
    }
}

function Set-EnvironmentVariable {
    param(
        [string]$Name,
        [string]$Value,
        [string]$Scope = "Machine"
    )
    
    try {
        [Environment]::SetEnvironmentVariable($Name, $Value, $Scope)
        Write-Status "   ✓ $Name = $Value" "Success"
        return $true
    } catch {
        Write-Status "   ✗ 设置 $Name 失败: $_" "Error"
        return $false
    }
}

function Add-ToPath {
    param([string]$NewPath)
    
    try {
        $currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
        
        if ($currentPath -notlike "*$NewPath*") {
            $newPathValue = "$currentPath;$NewPath"
            [Environment]::SetEnvironmentVariable("PATH", $newPathValue, "Machine")
            Write-Status "   ✓ 已添加到 PATH: $NewPath" "Success"
            return $true
        } else {
            Write-Status "   ✓ 已在 PATH 中: $NewPath" "Success"
            return $true
        }
    } catch {
        Write-Status "   ✗ 添加到 PATH 失败: $_" "Error"
        return $false
    }
}

# 主程序
Write-Host @"

╔════════════════════════════════════════════════════════════╗
║                                                            ║
║        🚀 NFC 项目自动化环境配置                          ║
║                                                            ║
║        Java JDK 17 + Android Studio + 环境变量            ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝

"@ -ForegroundColor Cyan

# 检查管理员权限
Write-Status "`n[1/5] 检查管理员权限..." "Info"
if (-not (Test-Admin)) {
    Write-Status "✗ 需要管理员权限运行此脚本" "Error"
    Write-Status "请右键选择 '以管理员身份运行 PowerShell'" "Warning"
    Read-Host "按 Enter 退出"
    exit 1
}
Write-Status "✓ 已验证管理员权限" "Success"

# 创建下载目录
Write-Status "`n[2/5] 准备下载目录..." "Info"
if (-not (Test-Path $DOWNLOAD_DIR)) {
    New-Item -ItemType Directory -Path $DOWNLOAD_DIR -Force | Out-Null
    Write-Status "✓ 已创建: $DOWNLOAD_DIR" "Success"
} else {
    Write-Status "✓ 目录已存在" "Success"
}

# 下载和安装 Java
Write-Status "`n[3/5] Java JDK 17 处理..." "Info"

$javaInstallerPath = "$DOWNLOAD_DIR\jdk-17_windows-x64_bin.exe"

if (Test-Path $JAVA_INSTALL_PATH) {
    Write-Status "✓ Java 已安装在: $JAVA_INSTALL_PATH" "Success"
} else {
    if (-not (Test-Path $javaInstallerPath)) {
        Write-Status "   下载 Java 安装程序..." "Info"
        if (Download-File $JAVA_DOWNLOAD_URL $javaInstallerPath) {
            Write-Status "✓ 下载完成" "Success"
        } else {
            Write-Status "✗ 下载失败，请手动下载" "Error"
            Write-Status "下载地址: https://www.oracle.com/java/technologies/downloads/" "Warning"
            Read-Host "按 Enter 继续"
        }
    }
    
    if (Test-Path $javaInstallerPath) {
        Write-Status "   运行安装程序..." "Info"
        & $javaInstallerPath /s | Out-Null
        
        # 等待安装完成
        Start-Sleep -Seconds 2
        
        if (Test-Path $JAVA_INSTALL_PATH) {
            Write-Status "✓ Java 安装成功" "Success"
        } else {
            Write-Status "✗ Java 安装失败，请检查安装日志" "Error"
        }
    }
}

# 设置环境变量
Write-Status "`n[4/5] 设置环境变量..." "Info"

Write-Status "   JAVA_HOME..." "Info"
Set-EnvironmentVariable "JAVA_HOME" $JAVA_INSTALL_PATH "Machine"

Write-Status "   PATH..." "Info"
Add-ToPath "%JAVA_HOME%\bin"

# Android Studio（可选）
Write-Status "`n[5/5] Android Studio 处理..." "Info"

if ($SkipAndroid) {
    Write-Status "跳过 Android Studio 安装（已指定 -SkipAndroid）" "Info"
} else {
    $response = Read-Host "是否下载和安装 Android Studio? (Y/N)"
    
    if ($response -eq "Y" -or $response -eq "y") {
        $androidInstallerPath = "$DOWNLOAD_DIR\android-studio-2024.1.1.31-windows.exe"
        
        if (-not (Test-Path $androidInstallerPath)) {
            Write-Status "   下载 Android Studio (900MB+，可能需要几分钟)..." "Info"
            if (Download-File $ANDROID_STUDIO_URL $androidInstallerPath) {
                Write-Status "✓ 下载完成" "Success"
                Write-Status "   运行安装程序..." "Info"
                & $androidInstallerPath | Out-Null
            } else {
                Write-Status "✗ Android Studio 下载失败" "Error"
                Write-Status "请从 https://developer.android.com/studio 手动下载" "Warning"
            }
        } else {
            Write-Status "   运行 Android Studio 安装程序..." "Info"
            & $androidInstallerPath | Out-Null
        }
    }
}

# 验证
Write-Status "`n════════════════════════════════════════════════════════════" "Info"
Write-Status "📋 配置完成！" "Success"
Write-Status "════════════════════════════════════════════════════════════" "Info"

# 显示后续步骤
Write-Host @"

✨ 后续步骤：

1️⃣  重启电脑（重要！）
    环境变量需要重启系统才能在所有应用中生效
    
2️⃣  重启后验证：
    打开 PowerShell 输入：java -version
    应该看到版本信息
    
3️⃣  如果安装了 Android Studio：
    打开 Android Studio
    Tools → SDK Manager
    复制 "Android SDK Location" 路径
    
4️⃣  配置项目：
    cd k:\NFC\NFCApp
    .\Setup-LocalProperties.bat
    .\check_env.bat
    
5️⃣  开始开发！
    code .

📁 下载文件保存在：$DOWNLOAD_DIR

📝 已设置的环境变量：
   • JAVA_HOME = $JAVA_INSTALL_PATH
   • PATH 包含 %JAVA_HOME%\bin

"@ -ForegroundColor Green

if (-not $NoRestart) {
    Write-Status "`n⚠️  重要：建议立即重启电脑" "Warning"
    $response = Read-Host "是否现在重启电脑? (Y/N)"
    
    if ($response -eq "Y" -or $response -eq "y") {
        Write-Status "30 秒后重启... (按 Ctrl+C 取消)" "Warning"
        Start-Sleep -Seconds 3
        shutdown /r /t 27 /c "NFC Setup - Restarting to apply environment variables"
    }
}

Write-Host ""
Read-Host "按 Enter 关闭此窗口"
