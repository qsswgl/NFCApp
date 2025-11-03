# ============================================
# Java 安装完整指南 - 多种方案
# ============================================

## 问题诊断

当前终端网络下载似乎存在问题。这是完全正常的 - 我们有其他解决方案！

---

## 方案 A：手动下载 + 自动安装（推荐）⭐

### 第一步：手动下载 Java

选择以下任意一个源下载：

#### 选项 1：Amazon Corretto 17 LTS（推荐 ✓ 最稳定）
- **下载页面**：https://corretto.aws/downloads/latest/
- **选择**：Windows x64 JDK（.zip 格式）
- **文件名**：amazon-corretto-17.0.x-windows-x64-jdk.zip
- **大小**：约 190 MB
- **保存到**：`C:\Users\[YourName]\Downloads\`

#### 选项 2：Eclipse Adoptium / Temurin 17 LTS
- **下载页面**：https://adoptium.net/installation/
- **选择**：Java 17 → Windows → x64 → JDK
- **文件名**：OpenJDK17U-jdk_x64_windows_hotspot_17.0.x_x.zip
- **大小**：约 185 MB
- **保存到**：`C:\Users\[YourName]\Downloads\`

#### 选项 3：Oracle JDK 17 LTS
- **下载页面**：https://www.oracle.com/java/technologies/downloads/
- **选择**：Java SE 17 LTS → Installer（.exe）
- **文件名**：jdk-17_windows-x64_bin.exe
- **大小**：约 165 MB
- **保存到**：`C:\Users\[YourName]\Downloads\`

### 第二步：运行自动安装脚本

下载完成后，运行这个自动安装脚本：

```powershell
# 以管理员身份打开 PowerShell，然后运行：
cd k:\NFC\NFCApp
powershell -ExecutionPolicy Bypass -File install-java-manual.ps1
```

脚本会自动：
- ✓ 查找你下载的 Java 文件
- ✓ 提取 ZIP 或安装 EXE
- ✓ 设置环境变量
- ✓ 验证安装

---

## 方案 B：使用 Chocolatey 包管理器（最简单）

如果你已经安装了 Chocolatey：

```powershell
# 以管理员身份打开 PowerShell，然后运行：
choco install openjdk17 -y
```

完成！环境变量会自动设置。

查看是否已安装 Chocolatey：
```powershell
choco --version
```

没有安装？在管理员 PowerShell 中运行：
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

---

## 方案 C：使用 Windows Package Manager (winget)

Windows 11 自带，Windows 10 可以从 Microsoft Store 安装：

```powershell
# 以管理员身份打开 PowerShell，然后运行：
winget install Oracle.JDK.17
```

完成！

---

## 推荐流程（综合最优）

### 如果你想要最快速的方案：
1. **使用 winget**（如果是 Windows 11）
   ```powershell
   winget install Oracle.JDK.17
   ```
   完成！只需 5 分钟。

### 如果你想要最稳定的方案：
1. **手动下载** Amazon Corretto
2. **运行** install-java-manual.ps1
3. **重启电脑**
4. **验证** java -version

### 如果你有网络问题：
1. **手动下载**（在有网络的电脑上）
2. **复制文件**到这台电脑
3. **运行** install-java-manual.ps1

---

## 快速命令参考

### 验证 Java 是否已安装
```powershell
java -version
javac -version
echo $env:JAVA_HOME
```

### 设置 Java 环境变量（手动）
```powershell
# 以管理员身份运行
setx JAVA_HOME "C:\Program Files\Java\jdk-17" /M
setx PATH "%PATH%;%JAVA_HOME%\bin" /M
```

### 卸载 Java
```powershell
# 以管理员身份运行
Remove-Item "C:\Program Files\Java\jdk-17" -Recurse -Force
```

---

## 立即开始的 3 个选择

### ✅ 选择 1：最简单（Windows 11 用户）
```powershell
winget install Oracle.JDK.17
```

### ✅ 选择 2：最稳定（所有人）
1. 访问 https://corretto.aws/downloads/latest/
2. 下载 Windows x64 JDK (ZIP)
3. 运行 `install-java-manual.ps1`

### ✅ 选择 3：包管理器（有 Chocolatey）
```powershell
choco install openjdk17 -y
```

---

## 需要帮助？

### 检查清单
- [ ] 已下载 Java（ZIP 或 EXE）
- [ ] 保存在 Downloads 文件夹
- [ ] 以管理员身份打开 PowerShell
- [ ] 运行 install-java-manual.ps1 或 winget
- [ ] 重启电脑
- [ ] 运行 `java -version` 验证

### 常见问题
Q：winget 找不到命令？
A：你的 Windows 版本可能太旧，使用方案 B（Chocolatey）或方案 A（手动下载）

Q：下载很慢？
A：选择离你最近的镜像：
- 国内用户：可以考虑使用 VPN 或等待
- 使用 Chocolatey 会从其他源下载，可能更快

Q：如何检查安装成功？
A：运行 `java -version` 应该显示版本信息

---

## 下一步

1. **选择上面的一个方案**
2. **安装 Java**
3. **重启电脑**
4. **验证**：`java -version`
5. **运行**：`Check-System-Ready.bat`
6. **开始开发**！

---

**加油！** 💪
