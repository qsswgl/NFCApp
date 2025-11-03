# 🛠️ 三步快速配置指南

## 步骤 1️⃣：安装 Java（5 分钟）

### 方法 A：手动安装（推荐新手）

1. **下载 Java**
   - 访问：https://www.oracle.com/java/technologies/downloads/
   - 选择 **Java SE 11** 或更新版本
   - 下载 **Windows x64 Installer** (.exe)

2. **安装**
   - 双击运行安装程序
   - 点击"Next"一直到完成
   - 默认安装路径通常是 `C:\Program Files\Java\jdk-11.x.x` 或类似

3. **验证安装**
   ```powershell
   java -version
   ```
   应该输出类似：
   ```
   java version "11.x.x" 
   ```

4. **设置环境变量**
   - 右键点击"此电脑" → 属性
   - 点击"高级系统设置"
   - 点击"环境变量"
   - 在"系统变量"中点击"新建"
   - 变量名：`JAVA_HOME`
   - 变量值：`C:\Program Files\Java\jdk-11` (根据实际路径调整)
   - 确定保存

5. **将 Java bin 添加到 PATH**
   - 在"系统变量"中找到 `Path`，双击编辑
   - 点击"新建"
   - 输入：`%JAVA_HOME%\bin`
   - 确定保存

6. **重启 PowerShell 验证**
   ```powershell
   $env:JAVA_HOME
   # 应该输出你的 Java 安装路径
   
   java -version
   # 应该成功输出版本信息
   ```

### 方法 B：使用 PowerShell 脚本自动检查

```powershell
# 检查 Java 是否已安装
if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "✅ Java 已安装" -ForegroundColor Green
    java -version
} else {
    Write-Host "❌ Java 未安装，请访问："
    Write-Host "https://www.oracle.com/java/technologies/downloads/"
    Write-Host "下载 Java SE 11 或更新版本"
}
```

---

## 步骤 2️⃣：安装/配置 Android SDK（10 分钟）

### 方法 A：通过 Android Studio 安装（推荐）

1. **下载 Android Studio**
   - 访问：https://developer.android.com/studio
   - 点击"Download Android Studio"
   - 下载 Windows 版本

2. **安装**
   - 双击运行安装程序
   - 选择"Next"继续
   - 勾选 "Android SDK" 和 "Android Emulator"
   - 完成安装

3. **启动 Android Studio**
   - 首次启动会下载 SDK 组件
   - 完成初始化（可能需要几分钟）

4. **查找 Android SDK 路径**
   - 在 Android Studio 中：Tools → SDK Manager
   - 顶部显示的 "Android SDK Location" 就是你的 SDK 路径
   - 通常是：`C:\Users\你的用户名\AppData\Local\Android\sdk`
   - 复制这个路径

5. **设置环境变量**
   - 右键"此电脑" → 属性
   - 点击"高级系统设置"
   - 点击"环境变量"
   - 在"系统变量"中点击"新建"
   - 变量名：`ANDROID_HOME`
   - 变量值：粘贴上面复制的 SDK 路径
   - 确定保存

6. **将 platform-tools 添加到 PATH**
   - 在"系统变量"中找到 `Path`，双次编辑
   - 点击"新建"
   - 输入：`%ANDROID_HOME%\platform-tools`
   - 确定保存

7. **重启 PowerShell 验证**
   ```powershell
   $env:ANDROID_HOME
   # 应该输出你的 Android SDK 路径
   
   adb version
   # 应该输出 adb 版本信息
   ```

### 方法 B：使用命令快速查找 SDK 路径

如果 Android Studio 已安装，运行此命令查找 SDK：

```powershell
# 在 Android Studio 的安装目录中查找 SDK
$androidRoot = "C:\Users\$env:USERNAME\AppData\Local\Android"
if (Test-Path $androidRoot) {
    Get-ChildItem $androidRoot -Recurse | Where-Object { $_.Name -eq "platform-tools" }
}
```

---

## 步骤 3️⃣：配置 local.properties（2 分钟）

### 方法 A：使用 PowerShell 脚本自动配置

运行以下命令在 PowerShell 中：

```powershell
# 进入项目目录
cd k:\NFC\NFCApp

# 从模板复制
Copy-Item .\local.properties.template .\local.properties

# 编辑 local.properties（使用你最喜欢的编辑器）
# 或直接在 VS Code 中打开
code .\local.properties
```

然后在编辑器中：
- 找到这一行：`sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\sdk`
- 替换为你的实际 Android SDK 路径，使用 `\\` 作为路径分隔符

例如：
```properties
sdk.dir=C:\\Users\\JohnDoe\\AppData\\Local\\Android\\sdk
```

### 方法 B：手动编辑

1. 打开文件：`k:\NFC\NFCApp\local.properties`
2. 修改第一行：
   ```properties
   sdk.dir=你的Android SDK路径（用 \\ 作为分隔符）
   ```

---

## 🔧 一键配置脚本

如果你想一步到位，可以使用这个 PowerShell 脚本：

**创建文件：`k:\NFC\NFCApp\Setup-Environment.ps1`**

```powershell
# Setup-Environment.ps1
# NFC 项目环境自动配置脚本

Write-Host "=== NFC 项目环境配置 ===" -ForegroundColor Cyan

# 第1步：验证 Java
Write-Host "`n[1/3] 验证 Java..." -ForegroundColor Yellow
if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "✅ Java 已安装" -ForegroundColor Green
    java -version
} else {
    Write-Host "❌ Java 未安装！" -ForegroundColor Red
    Write-Host "请先从以下地址下载安装：" -ForegroundColor Red
    Write-Host "https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Cyan
    exit 1
}

# 第2步：验证 Android SDK
Write-Host "`n[2/3] 验证 Android SDK..." -ForegroundColor Yellow
if ($env:ANDROID_HOME) {
    Write-Host "✅ ANDROID_HOME 已设置：$env:ANDROID_HOME" -ForegroundColor Green
    if (Test-Path "$env:ANDROID_HOME\platform-tools\adb.exe") {
        Write-Host "✅ ADB 已找到" -ForegroundColor Green
    } else {
        Write-Host "❌ ADB 未找到，请检查 Android SDK 是否完整" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ ANDROID_HOME 未设置！" -ForegroundColor Red
    Write-Host "请先安装 Android Studio：https://developer.android.com/studio" -ForegroundColor Red
    exit 1
}

# 第3步：配置 local.properties
Write-Host "`n[3/3] 配置 local.properties..." -ForegroundColor Yellow
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$localPropsTemplate = Join-Path $projectDir "local.properties.template"
$localProps = Join-Path $projectDir "local.properties"

if (Test-Path $localPropsTemplate) {
    Copy-Item $localPropsTemplate $localProps -Force
    
    # 读取并修改 local.properties
    $content = Get-Content $localProps
    $sdkPath = $env:ANDROID_HOME -replace '\\', '\\'  # 转义反斜杠
    $content = $content -replace 'sdk.dir=.*', "sdk.dir=$sdkPath"
    Set-Content $localProps -Value $content
    
    Write-Host "✅ local.properties 已配置：$localProps" -ForegroundColor Green
    Write-Host "   SDK 路径：$($env:ANDROID_HOME)" -ForegroundColor Green
} else {
    Write-Host "❌ 找不到 local.properties.template" -ForegroundColor Red
    exit 1
}

Write-Host "`n✅ 所有配置完成！" -ForegroundColor Green
Write-Host "`n下一步：" -ForegroundColor Yellow
Write-Host "1. 重启 PowerShell（关闭并重新打开）"
Write-Host "2. 运行：cd k:\NFC\NFCApp && .\check_env.bat"
Write-Host "3. 确认所有检查项都通过"
```

运行方法：
```powershell
cd k:\NFC\NFCApp
powershell -ExecutionPolicy Bypass -File Setup-Environment.ps1
```

---

## ⚠️ 常见问题

### 问题：安装 Java 后仍显示"Java 未找到"
**原因**：环境变量需要在新的 PowerShell 窗口中重新加载
**解决**：
1. 关闭所有 PowerShell 窗口
2. 重新打开 PowerShell
3. 重新尝试

### 问题：找不到 Android SDK 路径
**解决**：
1. 打开 Android Studio
2. 进入 Tools → SDK Manager
3. 顶部显示的 "Android SDK Location" 就是你的 SDK 路径

### 问题：local.properties 配置不对
**检查清单**：
- [ ] 路径使用 `\\` 作为分隔符（而不是单个 `\`）
- [ ] 没有在路径末尾添加额外的 `\`
- [ ] 路径和你的 Android SDK 实际位置一致

### 问题：Gradle 构建时说找不到 Java
**解决**：
1. 检查 JAVA_HOME 是否正确设置：`echo %JAVA_HOME%`
2. 检查 PATH 中是否包含 `%JAVA_HOME%\bin`：`echo %PATH%`
3. 重启电脑使环境变量完全生效

---

## ✅ 配置检查清单

完成配置后，逐项检查：

- [ ] Java 已安装：`java -version` 成功输出版本
- [ ] JAVA_HOME 已设置：`echo %JAVA_HOME%` 输出正确路径
- [ ] PATH 包含 Java bin：`echo %PATH%` 包含 `%JAVA_HOME%\bin`
- [ ] Android SDK 已安装
- [ ] ANDROID_HOME 已设置：`echo %ANDROID_HOME%` 输出正确路径
- [ ] ADB 可用：`adb version` 成功输出版本
- [ ] local.properties 已配置：文件存在且路径正确
- [ ] 运行 `.\check_env.bat` 所有项都通过 ✓

---

## 🎯 电脑重启注意事项

**为什么需要重启？**
- Windows 环境变量修改后，需要重启所有应用（包括 IDE、PowerShell 等）才能读取新值

**重启前的最后检查**：
1. 已设置 JAVA_HOME
2. 已设置 ANDROID_HOME  
3. 已将两者的 bin/platform-tools 添加到 PATH
4. local.properties 文件已创建

**重启后的第一件事**：
```powershell
cd k:\NFC\NFCApp
.\check_env.bat
```

---

## 🚀 完成后开始开发

所有配置完成后：

```powershell
# 打开项目
code k:\NFC\NFCApp

# 或者在已打开的 VS Code 中运行：
# Ctrl+Shift+P → Tasks: Run Task → Build, Install and Run
```

加油！🎉
