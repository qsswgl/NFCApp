# 🚀 命令行自动化安装 - 完整指南

## 📌 你现在拥有三个自动化脚本

我已经为你创建了 **3 个命令行自动化脚本**，可以完全自动化 Java 和 Android SDK 的安装。

---

## 🎯 快速选择

### 我想要最快的方式 ⚡
```powershell
.\Quick-Auto-Setup.bat
```
**特点**：
- 只下载和安装 Java
- 设置环境变量
- 预计 5-10 分钟完成
- 最简单直接

---

### 我想一次性完成所有 🔧
```powershell
.\Auto-Install-Java-Android.bat
```
**特点**：
- 安装 Java
- 下载并安装 Android Studio
- 设置所有环境变量
- 预计 20-30 分钟完成

---

### 我想要最灵活的选项 ⚙️
```powershell
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1
```
**特点**：
- 完整的 PowerShell 版本
- 支持参数自定义
- 更好的错误处理
- 最专业的方式

---

## 📖 使用步骤

### 第 1️⃣ 步：以管理员身份运行 PowerShell

**方法 A**（推荐）：
1. 按 **Windows 键** + **R**
2. 输入：`powershell`
3. 按 **Ctrl + Shift + Enter** 以管理员身份运行
4. 点击 **"是"** 确认

**方法 B**：
1. 右键点击 **PowerShell** 图标
2. 选择 **"以管理员身份运行"**

**方法 C**：
1. 在任务栏搜索 `PowerShell`
2. 右键结果 → **"以管理员身份运行"**

### 第 2️⃣ 步：进入项目目录

```powershell
cd k:\NFC\NFCApp
```

### 第 3️⃣ 步：运行脚本

**选项 A - 最快（仅 Java）**：
```powershell
.\Quick-Auto-Setup.bat
```

**选项 B - 完整（Java + Android）**：
```powershell
.\Auto-Install-Java-Android.bat
```

**选项 C - 灵活配置**：
```powershell
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1
```

### 第 4️⃣ 步：按照脚本提示操作

脚本会：
1. 自动检查管理员权限
2. 下载必需文件
3. 自动安装软件
4. 设置环境变量
5. 提示是否重启

### 第 5️⃣ 步：重启电脑

⚠️ **非常重要**：环境变量需要重启才能生效

脚本会提示：
```
是否现在重启电脑? (Y/N):
```

选择 **Y** 会在 30 秒后自动重启

或手动重启：
```powershell
shutdown /r /t 60
```

### 第 6️⃣ 步：重启后验证

打开新的 PowerShell 验证：

```powershell
java -version
```

应该看到类似：
```
java version "17.0.x" 2024-xx LTS
```

---

## 🔌 PowerShell 脚本的参数

如果使用 PowerShell 脚本，可以添加参数：

### 跳过 Android Studio（仅安装 Java）
```powershell
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1 -SkipAndroid
```

### 安装后不自动提示重启
```powershell
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1 -NoRestart
```

### 同时使用多个参数
```powershell
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1 -SkipAndroid -NoRestart
```

---

## 📊 脚本流程图

```
┌─────────────────────────────────────┐
│ 以管理员身份运行 PowerShell        │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ cd k:\NFC\NFCApp                   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│ 选择一个脚本运行                                │
├─────────────────────────────────────────────────┤
│ • Quick-Auto-Setup.bat                        │
│ • Auto-Install-Java-Android.bat               │
│ • Auto-Setup-Environment.ps1                  │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 脚本自动：                         │
│ 1. 检查管理员权限                  │
│ 2. 下载 Java (160 MB)             │
│ 3. 安装 Java                       │
│ 4. 设置环境变量                    │
│ 5. （可选）下载 Android (900 MB)  │
│ 6. （可选）安装 Android            │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 提示重启电脑                        │
└────────────┬────────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
  [Y]               [N]
    │                 │
重启系统         手动重启
    │                 │
    └────────┬────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ 重启后验证                         │
│ java -version                      │
│ echo %JAVA_HOME%                   │
└─────────────────────────────────────┘
```

---

## ✅ 完成后的验证

### 1. 检查 Java 版本
```powershell
java -version
```

### 2. 检查环境变量
```powershell
echo %JAVA_HOME%
# 应该输出：C:\Program Files\Java\jdk-17

echo %PATH%
# 应该包含：C:\Program Files\Java\jdk-17\bin
```

### 3. 运行项目检查脚本
```powershell
cd k:\NFC\NFCApp
.\Check-System-Ready.bat
# 应该显示 5/5 通过
```

---

## 🆘 常见问题

### Q1: "需要管理员权限"
**A**: 右键选择 "以管理员身份运行" PowerShell

### Q2: 下载很慢
**A**: 
- 检查网络连接
- 脚本会将文件保存在 `%TEMP%\NFC_Setup`
- 可以手动放入文件后重新运行脚本

### Q3: 安装失败
**A**:
- 检查磁盘空间（至少需要 2GB）
- 关闭防病毒软件
- 查看安装日志

### Q4: java -version 仍然找不到
**A**:
- 一定要重启电脑（不仅是 PowerShell）
- 重启后打开新的 PowerShell 窗口
- 再试一次

### Q5: PowerShell 脚本执行被拒绝
**A**:
使用正确的命令：
```powershell
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1
```

---

## 📋 脚本对比

| 特性 | Quick-Auto-Setup | Auto-Install | PowerShell |
|------|-----------------|--------------|-----------|
| 下载 Java | ✅ | ✅ | ✅ |
| 安装 Java | ✅ | ✅ | ✅ |
| 环境变量 | ✅ | ✅ | ✅ |
| Android Studio | ❌ | ✅ | ✅ |
| 参数支持 | ❌ | ❌ | ✅ |
| 错误处理 | 基础 | 完整 | 完整 |
| 易用程度 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| 功能完整 | 基础 | 完整 | 最完整 |

---

## 🎯 推荐方案

### 我是初学者 👨‍💻
→ 使用 `Quick-Auto-Setup.bat`

### 我需要完整配置 🔧
→ 使用 `Auto-Install-Java-Android.bat`

### 我懂技术想自定义 ⚙️
→ 使用 `Auto-Setup-Environment.ps1`

---

## 🚀 现在就开始！

### 最快的启动方式：

```powershell
# 1. 以管理员身份打开 PowerShell

# 2. 进入项目目录
cd k:\NFC\NFCApp

# 3. 运行最快的脚本
.\Quick-Auto-Setup.bat

# 4. 等待完成并重启电脑

# 5. 重启后验证
java -version
```

**总耗时**：5-10 分钟（仅 Java）或 20-30 分钟（包括 Android）

---

## 📚 相关文件

| 文件 | 用途 |
|------|------|
| `Quick-Auto-Setup.bat` | 🚀 最快方式 |
| `Auto-Install-Java-Android.bat` | 完整方式 |
| `Auto-Setup-Environment.ps1` | 高级方式 |
| `AUTO_INSTALL_README.md` | 详细文档 |
| `Verify-Java-Installation.bat` | 验证脚本 |

---

**现在就去运行脚本吧！** 🚀

**记得用管理员身份运行 PowerShell！** ⚠️

**加油！** 💪
