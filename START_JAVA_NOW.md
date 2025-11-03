# 📍 Java 安装 - 现在就开始吧！

## 🎯 你现在需要做什么

我已经为你准备了所有的指南。现在该你行动了！

---

## 📥 第一步：下载 Java（2 分钟）

**直接打开这个链接**：
```
https://www.oracle.com/java/technologies/downloads/
```

**或者**：在浏览器中复制粘贴上面的链接

---

## 🎬 快速三步完成

### Step 1️⃣：找到 Java SE 17
页面上找到 **"Java SE 17"** 部分

### Step 2️⃣：下载 Windows 版本
选择 **"Windows x64 Installer"**

点击下载按钮

### Step 3️⃣：运行安装程序
- 文件会下载到 Downloads 文件夹
- 右键点击 `jdk-17_windows-x64_bin.exe`
- 选择 **"以管理员身份运行"**
- 一路点 **Next** 即可

---

## 📚 详细指南可用

如果需要更详细的步骤说明，你可以查看：

| 文件 | 用途 |
|------|------|
| **JAVA_INSTALL_GUIDE.md** | 完整的分步指南（有截图说明思路） |
| **JAVA_QUICK_STEPS.md** | 快速版本（3 个核心步骤） |
| **NOW_INSTALL_JAVA.md** | 现在就做（最直接） |

---

## ✅ 安装完后

安装完成后（1-2 分钟），打开 PowerShell 验证：

```powershell
java -version
```

**成功的样子**：
```
java version "17.0.x" 2024-xx LTS
Java(TM) SE Runtime Environment ...
```

---

## 🔧 设置环境变量（3 分钟）

### 快速方法：

1. 按 **Windows + X**
2. 点击 **"系统"**
3. 点击 **"高级系统设置"**
4. 点击 **"环境变量"**
5. 在系统变量中点击 **"新建"**
6. 输入：
   ```
   变量名：JAVA_HOME
   变量值：C:\Program Files\Java\jdk-17
   ```
7. 点击 **"确定"** 保存
8. 编辑 **Path** 变量，新建一行：`%JAVA_HOME%\bin`
9. 保存

### 验证：

关闭 PowerShell，重新打开：
```powershell
echo %JAVA_HOME%
```

应该输出：`C:\Program Files\Java\jdk-17`

---

## ⏱️ 总耗时

| 任务 | 时间 |
|------|------|
| 下载 | 2-5 分钟 |
| 安装 | 2-3 分钟 |
| 验证 | 1 分钟 |
| 设置环境变量 | 3 分钟 |
| **总计** | **8-15 分钟** |

---

## 🆘 有问题？

| 问题 | 解决方案 |
|------|--------|
| 找不到下载链接 | 复制粘贴上面的链接到浏览器 |
| 下载很慢 | 稍等或更换网络 |
| java -version 找不到 | 关闭 PowerShell，重新打开 |
| 环境变量设置不了 | 右键"此电脑"用管理员权限打开 |
| 还是不行 | 查看 JAVA_INSTALL_GUIDE.md 的"常见问题" |

---

## 🚀 完成后的下一步

Java 安装完成后：

1. ✅ 安装 Android Studio（15-25 分钟）
   - 打开 `ANDROID_INSTALL_GUIDE.md`

2. ✅ 重启电脑

3. ✅ 运行验证脚本
   ```powershell
   cd k:\NFC\NFCApp
   .\Check-System-Ready.bat
   ```

4. ✅ 在 VS Code 中开始开发！

---

## 💡 关键提示

✨ **保存安装程序**
- 下载完后保存好 .exe 文件
- 以后可能需要卸载或重装

✨ **记住安装路径**
- 通常是：`C:\Program Files\Java\jdk-17`
- 设置环境变量时会用到

✨ **环境变量很重要**
- 不要跳过设置 JAVA_HOME 和 PATH
- 这样才能在任何地方使用 Java

✨ **关闭重新打开 PowerShell**
- 环境变量需要在新的 PowerShell 中生效
- 不是在同一个 PowerShell 中

---

## 📌 记住这个链接

所有时候都可以从这里下载 Java：
```
https://www.oracle.com/java/technologies/downloads/
```

---

**现在就去下载吧！** 🚀

**预计 10-15 分钟完成整个 Java 安装和配置**

**然后继续安装 Android Studio**

**加油！** 💪
