# 🔧 Java JDK 安装完整指南

## 📥 第一步：下载 Java

### 1.1 打开下载链接
**访问官方下载页**：
```
https://www.oracle.com/java/technologies/downloads/
```

### 1.2 选择正确的版本

在页面上你会看到几个选项，**选择 Java SE 11 或更新版本**：

**推荐选择**：
- ✅ **Java SE 17 LTS**（最新长期支持版本）
- 或
- ✅ **Java SE 11 LTS**（稳定版本）

### 1.3 选择 Windows x64 版本

点击你选择的版本后，在下方找到：
```
Windows x64 Installer
```

点击下载，通常文件名类似：
```
jdk-17_windows-x64_bin.exe
或
jdk-11.x.x_windows-x64_bin.exe
```

**文件大小**：约 160-200 MB

---

## 💾 第二步：安装 Java

### 2.1 运行安装程序

1. **找到下载的文件**
   - 通常在 `Downloads` 文件夹中
   - 文件名：`jdk-XX_windows-x64_bin.exe`

2. **右键选择 "以管理员身份运行"**
   - 或直接双击（可能会提示权限）

3. **点击 "Yes" 确认 UAC（用户账户控制）**

### 2.2 安装步骤

**第 1 屏**：欢迎界面
```
[ Next > ]  点击下一步
```

**第 2 屏**：安装位置选择
```
保持默认路径：C:\Program Files\Java\jdk-17
[ Next > ]  点击下一步
```

**第 3 屏**：确认安装
```
显示要安装的内容
[ Install ]  点击安装
```

**第 4 屏**：安装中...
```
进度条完成
自动继续
```

**第 5 屏**：安装完成
```
✓ Successfully installed
[ Close ]  点击关闭
```

**预计时间**：1-2 分钟

---

## ✅ 第三步：验证安装

### 3.1 打开 PowerShell

1. **按 Windows 键**
2. **输入**：`powershell`
3. **点击打开**（或按 Enter）

### 3.2 验证 Java 版本

在 PowerShell 中输入：
```powershell
java -version
```

**成功的输出**（应该看到类似）：
```
java version "17.0.x" 2024-xx-xx LTS
Java(TM) SE Runtime Environment (build 17.0.x+x-LTS-xxxxx)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.x+x-LTS-xxxxx, mixed mode, sharing)
```

### 3.3 如果看不到版本信息

**可能原因 1**：安装还没完成
- 再等 1-2 分钟

**可能原因 2**：PowerShell 缓存
- 关闭 PowerShell 窗口
- 重新打开一个新的 PowerShell
- 再试一次 `java -version`

**可能原因 3**：安装失败
- 重新运行安装程序
- 确保以管理员身份运行

---

## 🔧 第四步：设置环境变量

### 4.1 打开环境变量设置

**方法 1**（最简单）：
1. 按 **Windows + X**
2. 选择 **"系统"**
3. 点击左边 **"高级系统设置"**
4. 点击右下角 **"环境变量"**

**方法 2**（如果上面不出现）：
1. 搜索框输入：**"环境变量"**
2. 点击 **"编辑系统环境变量"**
3. 点击右下角 **"环境变量"** 按钮

### 4.2 设置 JAVA_HOME

在打开的"环境变量"窗口中：

**上半部分**："用户变量" - 这里不用管

**下半部分**："系统变量" - 在这里点击 **"新建"**

**填入以下信息**：
```
变量名：  JAVA_HOME
变量值：  C:\Program Files\Java\jdk-17
```

**注意**：根据你安装的版本调整，可能是：
- `jdk-17`
- `jdk-11`
- 等等

点击 **"确定"** 保存

### 4.3 更新 PATH 变量

**在系统变量中找到**：`Path`

**双击 Path** 打开编辑窗口

**点击 "新建"** 添加一行

**输入**：
```
%JAVA_HOME%\bin
```

点击 **"确定"** 保存

---

## 🔄 第五步：重新加载环境

### 5.1 关闭所有 PowerShell 窗口

- 关闭所有已打开的 PowerShell
- 完全退出（不是最小化）

### 5.2 重新打开 PowerShell

- 按 Windows 键
- 输入：`powershell`
- 打开新窗口

### 5.3 再次验证

输入命令：
```powershell
java -version
```

应该成功输出 Java 版本信息

---

## 🎯 检查清单

完成后，确保：

- [ ] 下载了 JDK 安装程序（.exe 文件）
- [ ] 以管理员身份运行安装程序
- [ ] 选择了默认安装位置
- [ ] 安装完成
- [ ] 打开 PowerShell 验证：`java -version` 成功输出
- [ ] 设置了 JAVA_HOME 环境变量
- [ ] 在 PATH 中添加了 `%JAVA_HOME%\bin`
- [ ] 关闭并重新打开 PowerShell
- [ ] 再次验证 `java -version` 成功输出

---

## 🆘 常见问题和解决方案

### Q1: 在哪里能看到已安装的 Java？

**A**: 
```powershell
# 输入此命令查看路径
where java

# 或者输入此命令
echo %JAVA_HOME%
```

应该输出类似：
```
C:\Program Files\Java\jdk-17\bin\java.exe
```

### Q2: 下载很慢怎么办？

**A**: Oracle 官网有时速度慢，可以：
- 换个时间段下载
- 用代理加速
- 或从其他来源下载（OpenJDK 等）

### Q3: 安装时出现权限错误？

**A**: 
- 确保以管理员身份运行
- 右键安装程序 → "以管理员身份运行"
- 点击"是"确认 UAC

### Q4: java -version 还是找不到？

**A**: 
1. 关闭 PowerShell，完全退出（不是最小化）
2. 等待 5 秒
3. 重新打开新的 PowerShell
4. 再试一次

如果还是不行：
```powershell
# 检查 PATH
echo %PATH%

# 检查 JAVA_HOME
echo %JAVA_HOME%

# 尝试使用完整路径
C:\Program Files\Java\jdk-17\bin\java.exe -version
```

### Q5: 我不知道我安装的是哪个版本？

**A**: 
```powershell
# 输入查看版本
java -version

# 或查看完整路径
where java
```

---

## ✨ 成功标志

✅ 看到 `java version "XX.X.X"` 输出
✅ JAVA_HOME 环境变量已设置
✅ PATH 包含 `%JAVA_HOME%\bin`
✅ 关闭重新打开 PowerShell 后仍然有效

---

## 📋 下一步

Java 安装完成后：

1. **运行 Check-System-Ready.bat 检查**
   ```powershell
   cd k:\NFC\NFCApp
   .\Check-System-Ready.bat
   ```
   应该看到 Java 的检查项是 **[OK]**

2. **继续安装 Android Studio**
   - 按照 START_NOW.md 的步骤 2

3. **设置更多环境变量**
   - ANDROID_HOME

4. **重启电脑**
   - 使所有环境变量生效

---

## 💡 小提示

💡 **记住你安装的路径**
- 默认：`C:\Program Files\Java\jdk-17`
- 你会在设置环境变量时需要它

💡 **保存安装程序**
- 以后可能需要卸载或重装
- 或把路径记下来

💡 **如果想卸载**
- Windows 设置 → 应用 → 搜索 "Java"
- 点击卸载

---

**预计完成时间**：5-10 分钟

**下一步**：检查安装是否成功，然后安装 Android Studio

**需要帮助**？查看上面的"常见问题"部分 🆘
