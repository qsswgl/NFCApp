# 🎉 命令行自动化安装 - 完成准备！

## ✨ 现在你拥有完整的自动化方案！

我为你创建了 **3 个命令行脚本**，可以完全自动化 Java 和 Android SDK 的安装和配置。

---

## 🚀 立即运行（三选一）

### 方式 1️⃣：最快方式（仅 Java，推荐！）⚡
```powershell
# 以管理员身份打开 PowerShell，然后：
cd k:\NFC\NFCApp
.\Quick-Auto-Setup.bat
```
**特点**：
- 下载并安装 Java
- 自动设置环境变量
- **预计 5-10 分钟**
- 最简单直接

---

### 方式 2️⃣：完整方式（Java + Android）🔧
```powershell
cd k:\NFC\NFCApp
.\Auto-Install-Java-Android.bat
```
**特点**：
- 安装 Java
- 下载并安装 Android Studio
- 设置所有环境变量
- **预计 20-30 分钟**

---

### 方式 3️⃣：高级方式（PowerShell 版）⚙️
```powershell
cd k:\NFC\NFCApp
powershell -ExecutionPolicy Bypass -File Auto-Setup-Environment.ps1
```
**特点**：
- 最完整的功能
- 支持参数自定义
- 更好的错误处理

---

## 📋 三步快速开始

### 1️⃣ 以管理员身份打开 PowerShell
- **Windows + R** → 输入 `powershell`
- 按 **Ctrl + Shift + Enter** 以管理员身份打开
- 或右键点击 PowerShell 选择 "以管理员身份运行"

### 2️⃣ 进入项目目录
```powershell
cd k:\NFC\NFCApp
```

### 3️⃣ 选择一个脚本运行
```powershell
.\Quick-Auto-Setup.bat
```

---

## ✅ 脚本会自动做什么

```
1. 检查管理员权限
   └─ 没有权限则退出

2. 下载 Java JDK 17
   └─ 约 160 MB（网速决定时间）

3. 自动安装 Java
   └─ 到 C:\Program Files\Java\jdk-17

4. 设置环境变量
   ├─ JAVA_HOME = C:\Program Files\Java\jdk-17
   └─ PATH 添加 %JAVA_HOME%\bin

5. 提示重启
   └─ 脚本会提示是否立即重启
```

---

## 🔄 重启与验证

脚本运行完后，会问你：
```
是否现在重启电脑? (Y/N):
```

### 选择 Y（推荐）
- 脚本自动在 30 秒后重启
- 环境变量会立即生效

### 选择 N
- 你需要**手动重启电脑**
- 环境变量需要重启才能生效

### 重启后验证
```powershell
# 打开新的 PowerShell
java -version

# 应该看到：
java version "17.0.x" ...
```

---

## 📚 可用的脚本

| 脚本文件 | 大小 | 功能 | 运行时间 |
|---------|------|------|--------|
| `Quick-Auto-Setup.bat` | 小 | Java + 环境变量 | 5-10分钟 |
| `Auto-Install-Java-Android.bat` | 中 | Java + Android | 20-30分钟 |
| `Auto-Setup-Environment.ps1` | 中 | 高级功能 | 20-30分钟 |
| `COMMAND_LINE_AUTO_INSTALL.md` | - | 详细文档 | 阅读用 |
| `AUTO_INSTALL_README.md` | - | 参考手册 | 阅读用 |

---

## 🆘 常见问题速解

| 问题 | 解决方案 |
|------|--------|
| "需要管理员权限" | 右键 PowerShell → "以管理员身份运行" |
| 下载很慢 | 等等或检查网络，脚本会保存到 %TEMP% |
| java -version 找不到 | 重启电脑后用新 PowerShell 再试 |
| 脚本执行被拒绝 | 使用 `-ExecutionPolicy Bypass` 参数 |
| 需要 Android Studio | 选择 `Auto-Install-Java-Android.bat` 或 PowerShell 版本 |

---

## 🎯 推荐方案

| 用户类型 | 推荐脚本 | 理由 |
|---------|--------|------|
| 新手小白 | `Quick-Auto-Setup.bat` | 最简单快速 |
| 需要完整配置 | `Auto-Install-Java-Android.bat` | 一步到位 |
| 懂技术想自定义 | `Auto-Setup-Environment.ps1` | 功能完整 |

---

## 🔗 所有文件清单

**自动化脚本**（命令行运行）：
- ✅ `Quick-Auto-Setup.bat`
- ✅ `Auto-Install-Java-Android.bat`
- ✅ `Auto-Setup-Environment.ps1`

**文档指南**（阅读参考）：
- ✅ `COMMAND_LINE_AUTO_INSTALL.md` ⭐ 最详细
- ✅ `AUTO_INSTALL_README.md`

**验证脚本**（配置后运行）：
- ✅ `Verify-Java-Installation.bat`
- ✅ `Check-System-Ready.bat`
- ✅ `check_env.bat`

---

## 📊 时间对比

### 手动方式：
1. 打开浏览器下载 → 5 分钟
2. 找到安装文件运行 → 3 分钟
3. 一步步点击安装 → 5 分钟
4. 手动设置环境变量 → 10 分钟
5. 手动重启 → 5 分钟
**总计：28 分钟**

### 自动化方式：
1. 运行脚本 → 自动完成一切 → 5-10 分钟
**总计：5-10 分钟**

**节省时间：50% 以上！** ⚡

---

## 🚀 现在就开始！

### 最快的启动方式：

```powershell
# 1. 以管理员身份打开 PowerShell
#    (右键 → 以管理员身份运行)

# 2. 复制粘贴这三行命令：
cd k:\NFC\NFCApp
.\Quick-Auto-Setup.bat

# 3. 按 Enter 执行
# 4. 脚本自动完成所有事情
# 5. 选择 Y 重启（推荐）
# 6. 重启后验证：java -version
```

---

## 💡 小提示

✨ **脚本会自动**：
- 检测管理员权限
- 下载所需文件
- 安装软件
- 设置环境变量
- 提示重启

✨ **你需要**：
- 确保网络连接
- 准备 2GB+ 磁盘空间
- 以管理员身份运行
- 重启电脑使配置生效

---

## 📌 关键点

⚠️ **必须**：
1. 以**管理员身份**运行 PowerShell
2. **重启电脑**（不仅关闭 PowerShell）
3. 重启后用**新的** PowerShell 验证

✅ **验证成功的标志**：
```powershell
java -version
# 输出：java version "17.0.x" ...

echo %JAVA_HOME%
# 输出：C:\Program Files\Java\jdk-17
```

---

## 🎉 完成后的下一步

1. ✅ Java 安装完成
2. 📖 （可选）安装 Android Studio
3. 🔄 重启电脑
4. ✓ 运行验证脚本
5. 🚀 在 VS Code 中开始开发！

---

## 📞 需要帮助？

查看详细文档：
- `COMMAND_LINE_AUTO_INSTALL.md` - 最完整的指南
- `AUTO_INSTALL_README.md` - 参考手册

---

**现在就去运行脚本吧！** 🚀

**记得用管理员身份！** ⚠️

**预计 5-10 分钟完成！** ⏱️

**加油！** 💪
