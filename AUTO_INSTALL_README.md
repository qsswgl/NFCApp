# 🚀 快速启动自动化安装

## 立即运行（任选一个）

### 方式 1️⃣：使用批处理脚本（最简单）

```powershell
# 在 PowerShell 中
cd k:\NFC\NFCApp
.\Auto-Install-Java-Android.bat
```

**特点**：
- 最简单直接
- 会下载 Java 和 Android Studio
- 自动设置环境变量

---

### 方式 2️⃣：使用 PowerShell 脚本（功能完整）

```powershell
# 在 PowerShell（以管理员身份）中
cd k:\NFC\NFCApp
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1
```

**特点**：
- 功能更完整
- 支持自定义参数
- 更好的错误处理

**参数选项**：
```powershell
# 跳过 Android Studio 安装，只安装 Java
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1 -SkipAndroid

# 安装后不提示重启
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1 -NoRestart

# 同时使用两个参数
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1 -SkipAndroid -NoRestart
```

---

## ⚠️ 重要前置条件

### 1. 必须用管理员权限运行！

**如何以管理员身份运行 PowerShell**：
1. 右键点击 PowerShell
2. 选择 **"以管理员身份运行"**
3. 点击 **"是"** 确认

### 2. 网络连接

需要稳定的网络连接，因为：
- Java：约 160 MB
- Android Studio：约 900 MB

---

## 📊 脚本流程

两个脚本都会执行以下步骤：

```
1️⃣  检查管理员权限
   └─ 没有权限则退出

2️⃣  创建临时下载目录
   └─ %TEMP%\NFC_Setup

3️⃣  下载 Java
   └─ jdk-17_windows-x64_bin.exe (160 MB)

4️⃣  安装 Java
   └─ 安装到 C:\Program Files\Java\jdk-17

5️⃣  设置环境变量
   ├─ JAVA_HOME = C:\Program Files\Java\jdk-17
   ├─ PATH += %JAVA_HOME%\bin
   └─ （如选择）ANDROID_HOME 和 platform-tools

6️⃣  下载 Android Studio（可选）
   └─ android-studio-2024.1.1.31-windows.exe (900 MB+)

7️⃣  安装 Android Studio（可选）
   └─ 通过 GUI 安装程序

8️⃣  提示重启
   └─ 环境变量需要重启才能生效
```

---

## ✅ 完成后的检查

安装完成后和重启电脑后，验证：

```powershell
# 打开新的 PowerShell

# 检查 Java
java -version
# 应该输出：java version "17.0.x"

# 检查环境变量
echo %JAVA_HOME%
# 应该输出：C:\Program Files\Java\jdk-17

# 运行我们的验证脚本
cd k:\NFC\NFCApp
.\Check-System-Ready.bat
```

---

## 🆘 遇到问题？

### 问题 1：下载很慢或超时
**解决**：
- 检查网络连接
- 手动下载并放在 `%TEMP%\NFC_Setup` 目录
- 脚本会自动检测本地文件

### 问题 2：权限不足
**解决**：
- 确保以管理员身份运行
- 右键 PowerShell → "以管理员身份运行"

### 问题 3：安装失败
**解决**：
- 查看错误消息
- 手动运行下载的 .exe 文件
- 或手动下载和安装

### 问题 4：环境变量未生效
**解决**：
- ⚠️ **必须重启电脑**
- 不仅要关闭 PowerShell
- 要重启整个操作系统

---

## 📁 文件位置

| 文件 | 用途 |
|------|------|
| `Auto-Install-Java-Android.bat` | 批处理版（推荐） |
| `Auto-Setup-Environment.ps1` | PowerShell 版 |
| 下载文件 | `%TEMP%\NFC_Setup\` |

---

## 🎯 快速决策

### 你应该选择：

**我是初学者**
→ 使用 `Auto-Install-Java-Android.bat`

**我熟悉命令行**
→ 使用 `Auto-Setup-Environment.ps1`

**我只想安装 Java**
→ 用参数 `-SkipAndroid`

**我想避免自动重启**
→ 用参数 `-NoRestart`

---

## 🚀 现在就开始！

选择一个方式，打开 PowerShell（管理员身份），然后运行：

### 最简单的方式：
```powershell
cd k:\NFC\NFCApp
.\Auto-Install-Java-Android.bat
```

### 或者完整的方式：
```powershell
cd k:\NFC\NFCApp
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1
```

**预计总时间**：
- 只有 Java：5-10 分钟
- Java + Android Studio：20-30 分钟

**加油！** 🚀

---

**注意**：
- 第一次运行可能较慢（需要下载）
- 重启电脑后环境变量才会完全生效
- 完成后运行 `Check-System-Ready.bat` 验证
