# 🎯 Java 安装 - 现在就开始！

## 📍 你现在的位置

✅ 已准备：所有工具和文档
⏳ 当前：**安装 Java** ← 你在这里
⏳ 下一步：安装 Android Studio

---

## 🚀 现在就做这 3 件事

### 1️⃣ **打开下载链接**

**在浏览器中打开**：
```
https://www.oracle.com/java/technologies/downloads/
```

### 2️⃣ **选择 Java SE 17 LTS**

在页面上找到 **"Java SE 17"** 并点击

然后向下滚动，选择：
```
Windows x64 Installer
```

点击下载（约 160 MB，可能需要几分钟）

### 3️⃣ **运行安装程序**

1. 打开 Downloads 文件夹（或你的下载位置）
2. 找到 `jdk-17_windows-x64_bin.exe` 文件
3. **右键** → 选择 **"以管理员身份运行"**
4. 点击 **"Yes"** 确认
5. 一路点 **Next** → **Install** → **Close**

---

## ✅ 安装完后验证

打开 PowerShell：
```powershell
# 按 Windows 键，输入 powershell，按 Enter

java -version
```

**看到类似输出就成功了**：
```
java version "17.0.x" ...
```

---

## 🆘 遇到问题？

### 问题：找不到下载链接？
**→** 直接复制这个链接到浏览器：
```
https://www.oracle.com/java/technologies/downloads/
```

### 问题：安装程序下载很慢？
**→** 可以稍后再试，或用代理

### 问题：安装程序无法运行？
**→** 确保以**管理员身份运行**

### 问题：java -version 找不到？
**→** 关闭 PowerShell，等 5 秒，重新打开新窗口

### 问题：安装失败？
**→** 尝试：
1. 卸载现有的 Java（如果有的话）
2. 重新下载安装程序
3. 确保管理员权限
4. 检查磁盘空间足够

---

## 📋 完整步骤（详细版）

看 **JAVA_INSTALL_GUIDE.md** 获得完整的分步指南

看 **JAVA_QUICK_STEPS.md** 获得快速版本

---

## 🔧 设置环境变量（安装完后）

### 简单方法：

1. 按 **Windows + X**
2. 选择 **"系统"**
3. 点击 **"高级系统设置"**
4. 点击 **"环境变量"** 按钮
5. 在"系统变量"中点击 **"新建"**
6. 输入：
   ```
   变量名：JAVA_HOME
   变量值：C:\Program Files\Java\jdk-17
   ```
7. 点击 **"确定"**

### 编辑 PATH：

1. 在"系统变量"中找到 **"Path"**，双击
2. 点击 **"新建"**
3. 输入：`%JAVA_HOME%\bin`
4. 点击 **"确定"**

### 验证：

关闭并重新打开 PowerShell：
```powershell
echo %JAVA_HOME%
# 应该输出：C:\Program Files\Java\jdk-17

java -version
# 应该输出：Java 版本信息
```

---

## ⏱️ 时间表

| 任务 | 时间 |
|------|------|
| 打开下载页面 | 1 分钟 |
| 下载文件 | 2-5 分钟（取决于网速） |
| 安装 | 2-3 分钟 |
| 验证 | 1 分钟 |
| 设置环境变量 | 3 分钟 |
| **总计** | **10-15 分钟** |

---

## 🎯 成功标志

✅ 下载了 JDK 安装程序
✅ 以管理员身份运行安装程序
✅ 安装完成
✅ `java -version` 输出版本信息
✅ JAVA_HOME 已设置
✅ PATH 包含 `%JAVA_HOME%\bin`
✅ 关闭重新打开 PowerShell 后仍然有效

---

## 🔄 重启注意事项

⚠️ **重要**：设置完环境变量后，需要：
1. 关闭所有 PowerShell 窗口
2. 等待 5 秒
3. 重新打开 PowerShell
4. 验证 `java -version`

---

## 📚 更多帮助

- **详细步骤**：打开 `JAVA_INSTALL_GUIDE.md`
- **快速版本**：打开 `JAVA_QUICK_STEPS.md`
- **验证脚本**：运行 `Verify-Java-Installation.bat`
- **常见问题**：查看上面的"遇到问题"部分

---

## 下一步

✅ Java 安装完成后

→ 安装 Android Studio（15-25 分钟）

→ 打开 **ANDROID_INSTALL_GUIDE.md** 了解详细步骤

---

**准备好了吗？现在就去下载 Java 吧！** 🚀

链接：https://www.oracle.com/java/technologies/downloads/

加油！💪
