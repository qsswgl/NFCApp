# ============================================
# Java 自动化安装脚本 (PowerShell 版)
# 功能：自动下载并安装 Java JDK 17 LTS
# ============================================

param(
    [switch]$NoRestart = $false
)

# 颜色定义
$colors = @{
    Green  = 'Green'
    Red    = 'Red'
    Yellow = 'Yellow'
    Cyan   = 'Cyan'
    Gray   = 'Gray'
}

function Write-ColorLine($text, $color = 'White') {
    Write-Host $text -ForegroundColor $color
}

function Write-Title($text) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "  $text" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
}

function Check-Admin {
    $admin = ([System.Security.Principal.WindowsPrincipal] [System.Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([System.Security.Principal.WindowsBuiltInRole]::Administrator)
    return $admin
}

function Download-Java {
    Write-ColorLine "正在下载 Java JDK 17 LTS..." $colors.Cyan
    Write-ColorLine "（这可能需要 2-5 分钟，具体取决于网络速度）" $colors.Gray
    Write-Host ""
    
    $javaUrl = "https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe"
    $tempPath = "$env:TEMP\jdk-17.exe"
    
    # 检查是否已下载
    if (Test-Path $tempPath) {
        $fileSize = (Get-Item $tempPath).Length / 1MB
        Write-ColorLine "✓ Java 已在临时目录中 ($fileSize MB)" $colors.Green
        return $tempPath
    }
    
    try {
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $javaUrl -OutFile $tempPath -UseBasicParsing -Timeout 300
        $fileSize = (Get-Item $tempPath).Length / 1MB
        Write-ColorLine "✓ 下载成功！文件大小: $fileSize MB" $colors.Green
        return $tempPath
    }
    catch {
        Write-ColorLine "✗ 下载失败: $_" $colors.Red
        Write-ColorLine ""
        Write-ColorLine "请手动下载：https://www.oracle.com/java/technologies/downloads/" $colors.Yellow
        Write-ColorLine "选择：Java SE 17 LTS → Windows x64 Installer" $colors.Yellow
        Write-ColorLine "保存到：$tempPath" $colors.Yellow
        return $null
    }
}

function Install-Java($javaPath) {
    Write-ColorLine "正在安装 Java JDK 17 LTS..." $colors.Cyan
    Write-ColorLine "（这会自动进行，请等候...）" $colors.Gray
    Write-Host ""
    
    try {
        $process = Start-Process -FilePath $javaPath -ArgumentList "/s /D=C:\Program Files\Java\jdk-17" -NoNewWindow -Wait -PassThru
        
        if ($process.ExitCode -eq 0) {
            Write-ColorLine "✓ Java 安装成功！" $colors.Green
            return $true
        }
        else {
            Write-ColorLine "✗ 安装返回错误代码: $($process.ExitCode)" $colors.Red
            return $false
        }
    }
    catch {
        Write-ColorLine "✗ 安装失败: $_" $colors.Red
        return $false
    }
}

function Set-JavaEnvironment {
    Write-ColorLine "正在设置环境变量..." $colors.Cyan
    Write-Host ""
    
    $javaHome = "C:\Program Files\Java\jdk-17"
    
    # 检查 Java 是否已安装
    if (-not (Test-Path $javaHome)) {
        Write-ColorLine "✗ Java 目录未找到: $javaHome" $colors.Red
        return $false
    }
    
    try {
        # 设置 JAVA_HOME
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
        Write-ColorLine "✓ 已设置 JAVA_HOME = $javaHome" $colors.Green
        
        # 更新当前进程的 PATH
        $currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
        $javaBinPath = "$javaHome\bin"
        
        if ($currentPath -notlike "*$javaBinPath*") {
            $newPath = "$javaBinPath;$currentPath"
            [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
            Write-ColorLine "✓ 已更新 PATH 环境变量" $colors.Green
        }
        else {
            Write-ColorLine "✓ PATH 已包含 Java bin 目录" $colors.Green
        }
        
        # 刷新当前进程的环境变量
        $env:JAVA_HOME = $javaHome
        $env:PATH = "$javaBinPath;$env:PATH"
        
        return $true
    }
    catch {
        Write-ColorLine "✗ 设置环境变量失败: $_" $colors.Red
        return $false
    }
}

function Verify-Installation {
    Write-ColorLine "正在验证安装..." $colors.Cyan
    Write-Host ""
    
    try {
        $javaVersion = & java -version 2>&1 | Select-Object -First 1
        if ($LASTEXITCODE -eq 0) {
            Write-ColorLine "✓ Java 已成功安装！" $colors.Green
            Write-ColorLine "  版本信息：$javaVersion" $colors.Gray
            return $true
        }
    }
    catch {
        Write-ColorLine "✗ Java 命令未找到，可能需要重启" $colors.Yellow
        return $false
    }
    
    return $false
}

# ============================================
# 主程序开始
# ============================================

Write-Title "Java JDK 17 LTS 自动化安装程序"

# 检查管理员权限
if (-not (Check-Admin)) {
    Write-ColorLine "✗ 错误：需要管理员权限！" $colors.Red
    Write-ColorLine ""
    Write-ColorLine "请以管理员身份运行此脚本：" $colors.Yellow
    Write-ColorLine "右键点击 PowerShell → 以管理员身份运行" $colors.Yellow
    Write-ColorLine ""
    Write-ColorLine "然后运行：cd k:\NFC\NFCApp" $colors.Gray
    Write-ColorLine "最后运行：powershell -ExecutionPolicy Bypass -File AutoInstall-Java.ps1" $colors.Gray
    exit 1
}

Write-ColorLine "✓ 已获得管理员权限" $colors.Green
Write-Host ""

# 第一步：下载 Java
$javaPath = Download-Java
if ($null -eq $javaPath) {
    Write-ColorLine "安装已取消。" $colors.Yellow
    exit 1
}

Write-Host ""

# 第二步：安装 Java
if (-not (Install-Java $javaPath)) {
    Write-ColorLine "安装失败，请检查错误信息。" $colors.Red
    exit 1
}

Write-Host ""

# 第三步：设置环境变量
if (-not (Set-JavaEnvironment)) {
    Write-ColorLine "环境变量设置失败。" $colors.Red
    exit 1
}

Write-Host ""

# 第四步：验证安装
Write-ColorLine "等待 2 秒后尝试验证..." $colors.Gray
Start-Sleep -Seconds 2
Verify-Installation

Write-Host ""
Write-Title "安装完成！"

Write-ColorLine "✓ Java JDK 17 LTS 已成功安装！" $colors.Green
Write-ColorLine ""
Write-ColorLine "安装路径：C:\Program Files\Java\jdk-17" $colors.Cyan
Write-ColorLine "JAVA_HOME：已自动设置" $colors.Cyan
Write-ColorLine "PATH：已自动更新" $colors.Cyan
Write-ColorLine ""

# 询问是否重启
if (-not $NoRestart) {
    Write-Host ""
    Write-ColorLine "⚠️  重要：环境变量需要重启系统才能在所有应用中生效！" $colors.Yellow
    Write-ColorLine ""
    $response = Read-Host "是否立即重启电脑？(Y/N)"
    
    if ($response -eq 'Y' -or $response -eq 'y') {
        Write-ColorLine ""
        Write-ColorLine "电脑将在 10 秒后重启..." $colors.Cyan
        Start-Sleep -Seconds 10
        Restart-Computer -Force
    }
    else {
        Write-ColorLine ""
        Write-ColorLine "⚠️  提醒：请手动重启电脑以使环境变量生效！" $colors.Yellow
    }
}

Write-ColorLine ""
Write-ColorLine "按任意键退出..." $colors.Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
