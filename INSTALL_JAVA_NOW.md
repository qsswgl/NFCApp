# 🚀 Java 17 安装 - 立即开始！

## ⚡ 最快开始方式（5 分钟）

### 第一步：选择安装方案

#### 🥇 推荐方案 1（Windows 11 用户）
```powershell
# 以管理员身份打开 PowerShell，然后运行：
winget install Oracle.JDK.17
```

**优点：最简单，只需一条命令，自动设置环境变量**
**耗时：5-10 分钟**

---

#### 🥈 推荐方案 2（所有 Windows 用户）

**步骤 1：下载 Java**
- 访问：https://corretto.aws/downloads/latest/
- 下载：`amazon-corretto-17-windows-x64-jdk.zip`
- 保存到：`C:\Users\[YourName]\Downloads\`

**步骤 2：自动安装**
```batch
以管理员身份运行：
.\install-java-manual.bat
```

**优点：最稳定，支持所有 Windows 版本**
**耗时：10-15 分钟**

---

#### 🥉 其他选项

**方案 3（有 Chocolatey）**
```powershell
choco install openjdk17 -y
```

**方案 4（Eclipse Adoptium）**
1. 访问：https://adoptium.net/installation/
2. 下载 Java 17 LTS
3. 安装（或用 `install-java-manual.bat`）

---

## ✅ 安装后的 3 个必做步骤

### 1️⃣ 重启电脑 ⚠️（非常重要！）
```
环境变量需要重启才能生效
不仅关闭 PowerShell 是不够的
```

### 2️⃣ 验证安装
```powershell
# 重启后打开 PowerShell，运行：
java -version
javac -version
echo %JAVA_HOME%
```

**成功标志：显示版本号，无报错**

### 3️⃣ 运行环境检查
```batch
cd k:\NFC\NFCApp
.\Check-System-Ready.bat
.\Setup-LocalProperties.bat
.\check_env.bat
```

---

## 📁 相关文件位置

所有文件都在：`k:\NFC\NFCApp\`

| 文件 | 用途 |
|------|------|
| `install-java-manual.bat` | 自动安装脚本 |
| `Check-System-Ready.bat` | 系统检查 |
| `Setup-LocalProperties.bat` | SDK 配置 |
| `check_env.bat` | 环境验证 |
| `JAVA_INSTALLATION_GUIDE.md` | 详细指南 |
| `QUICK_INSTALL_CARD.md` | 快速参考 |

---

## 🎯 完整流程总结

```
1. 选择方案 1 或 2 安装 Java
   │
   ├─ 方案 1：winget install Oracle.JDK.17
   │
   └─ 方案 2：下载 ZIP + 运行 install-java-manual.bat
   │
2. 重启电脑 ⚠️（必须！）
   │
3. 验证：java -version
   │
4. 运行检查脚本
   │
5. 开始开发！🎉
```

---

## ❓ 常见问题

**Q：应该选哪个方案？**
A：优先方案 1（Windows 11）或方案 2（最稳定）

**Q：winget 找不到？**
A：你的 Windows 版本太旧，用方案 2

**Q：下载很慢？**
A：这很正常，Java 有 160-200 MB，等等或用 VPN

**Q：如何检查安装成功？**
A：重启后运行 `java -version`，有输出即成功

**Q：为什么一定要重启？**
A：环境变量需要系统级别重新加载才能生效

**Q：如果还是找不到 java 命令？**
A：检查 `%JAVA_HOME%` 是否正确设置（运行 `echo %JAVA_HOME%`）

---

## 🔧 手动设置环境变量（如果脚本失败）

```powershell
# 以管理员身份运行：

setx JAVA_HOME "C:\Program Files\Java\jdk-17" /M
setx PATH "%PATH%;%JAVA_HOME%\bin" /M

# 然后重启电脑
```

---

## 📚 更多帮助

- **详细指南**：查看 `JAVA_INSTALLATION_GUIDE.md`
- **快速参考**：查看 `QUICK_INSTALL_CARD.md`
- **所有文档**：查看 `START_NOW.md`

---

## 🎉 现在就开始！

**记住：方案 1 最快，方案 2 最稳定**

选择一个方案，安装 Java，重启电脑，验证成功，开始开发！

---

**祝你配置顺利！** 💪
