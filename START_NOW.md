# 🚀 立即开始：三步快速配置

你的系统检查结果显示，**所有关键工具都还需要安装**。不用担心，下面是详细的逐步指南。

---

## 📊 当前状态

根据系统检查结果：
- ❌ Java JDK：**未安装**
- ❌ Android SDK：**未安装**
- ❌ JAVA_HOME：**未配置**
- ❌ ANDROID_HOME：**未配置**
- ⚠️ Gradle Wrapper：**需初始化**

**预计总耗时：30-45 分钟**（首次安装）

---

## 🎯 三个关键步骤

### 步骤 1️⃣：安装 Java JDK（5-10 分钟）

#### 1.1 下载
- 访问：**https://www.oracle.com/java/technologies/downloads/**
- 找到 **Java SE 11** 或更新版本（推荐 Java 17 LTS）
- 选择 **Windows x64 Installer** (.exe 文件)
- 点击下载

#### 1.2 安装
- 双击下载的 `.exe` 文件
- 点击"Yes"确认 UAC
- 点击"Next"继续
- **保持默认安装路径**（通常是 `C:\Program Files\Java\jdk-XX`）
- 完成安装

#### 1.3 验证安装
打开 PowerShell，输入：
```powershell
java -version
```

应该看到类似：
```
java version "11.0.x" 2021-10-19 LTS
```

---

### 步骤 2️⃣：安装 Android Studio 和 SDK（15-25 分钟）

#### 2.1 下载
- 访问：**https://developer.android.com/studio**
- 点击"Download Android Studio"
- 选择 Windows 版本
- 下载文件（约 900MB）

#### 2.2 安装
- 双击下载的安装程序
- 点击"Next"继续
- 勾选 **"Android SDK"** 和 **"Android Emulator"**
- 点击"Install"安装

#### 2.3 首次启动
- 安装完成后启动 Android Studio
- 第一次启动会自动下载 SDK 组件（需要等待 5-10 分钟）
- 完成初始化

#### 2.4 查找 SDK 路径
- 打开 Android Studio
- 点击菜单：**Tools → SDK Manager**
- 顶部看到：**"Android SDK Location"**
- 复制这个路径，通常是：
  ```
  C:\Users\你的用户名\AppData\Local\Android\sdk
  ```

---

### 步骤 3️⃣：设置环境变量（5 分钟）

#### 3.1 设置 JAVA_HOME

1. 按 **Windows + X**，选择"系统"
2. 点击"高级系统设置"（或搜索"环境变量"）
3. 点击右下角"环境变量"按钮
4. 在"系统变量"中点击"新建"
5. 输入：
   - **变量名**：`JAVA_HOME`
   - **变量值**：`C:\Program Files\Java\jdk-11`（根据实际版本调整）
6. 点击"确定"

#### 3.2 设置 ANDROID_HOME

1. 在"系统变量"中再点击"新建"
2. 输入：
   - **变量名**：`ANDROID_HOME`
   - **变量值**：（粘贴步骤 2.4 复制的路径，例如 `C:\Users\你的用户名\AppData\Local\Android\sdk`）
3. 点击"确定"

#### 3.3 更新 PATH

1. 在"系统变量"中找到 **"Path"**，双击编辑
2. 点击"新建"，输入：`%JAVA_HOME%\bin`
3. 点击"新建"，输入：`%ANDROID_HOME%\platform-tools`
4. 点击"确定"保存

#### 3.4 验证设置
打开**新的** PowerShell 窗口（重新加载环境），输入：
```powershell
$env:JAVA_HOME
$env:ANDROID_HOME
java -version
adb version
```

都应该成功输出而不报错。

---

## 💻 重启电脑（⚠️ 非常重要！）

完成上面的所有步骤后，**必须重启电脑**。

Windows 环境变量修改需要重启才能在所有应用中生效。

---

## ✅ 重启后的验证

重启电脑后，打开 PowerShell 运行：

```powershell
cd k:\NFC\NFCApp
.\Check-System-Ready.bat
```

**应该看到**：
```
[OK] Java folder found
[OK] JAVA_HOME = C:\Program Files\Java\jdk-11
[OK] Android SDK found
[OK] ANDROID_HOME = C:\Users\YourName\AppData\Local\Android\sdk
Summary: 5/5 checks passed
```

---

## 🔧 配置 local.properties

检查通过后，运行：

```powershell
cd k:\NFC\NFCApp
.\Setup-LocalProperties.bat
```

这会自动从 `ANDROID_HOME` 环境变量配置 SDK 路径。

---

## 🎉 最终验证

运行最终环境检查：

```powershell
cd k:\NFC\NFCApp
.\check_env.bat
```

**应该全是 ✓ 通过标记**

---

## 🚀 开始开发！

一切就绪后，打开 VS Code：

```powershell
code k:\NFC\NFCApp
```

或者在 VS Code 中：
- 按 **Ctrl+Shift+P**
- 输入 **"Tasks: Run Task"**
- 选择 **"Build, Install and Run"**

应用会构建、安装并在连接的 Android 设备上运行！

---

## 🆘 如果遇到问题

### 问题：Java 安装后仍未找到
**解决**：
1. 关闭所有 PowerShell 窗口
2. 重新打开 PowerShell
3. 重新输入命令

### 问题：找不到 Android SDK 路径
**解决**：
1. 打开 Android Studio
2. Tools → SDK Manager
3. 顶部的"Android SDK Location"就是

### 问题：check_env.bat 显示找不到 adb
**解决**：
1. 检查 `%ANDROID_HOME%\platform-tools` 目录是否存在
2. 如果不存在，在 Android Studio 中：Tools → SDK Manager → Platform Tools → Install
3. 重启 PowerShell

### 问题：Gradle 构建失败
**解决**：
1. 重新验证 JAVA_HOME 和 ANDROID_HOME
2. 确保 local.properties 中的 SDK 路径正确
3. 重启电脑

---

## 📋 快速检查清单

完成后打勾：

- [ ] Java JDK 已安装（验证：`java -version` 成功）
- [ ] JAVA_HOME 已设置（验证：`echo %JAVA_HOME%` 显示路径）
- [ ] PATH 已包含 Java（验证：`%JAVA_HOME%\bin` 在 PATH 中）
- [ ] Android Studio 已安装
- [ ] Android SDK 已安装完整（包含 Platform Tools）
- [ ] ANDROID_HOME 已设置（验证：`echo %ANDROID_HOME%` 显示路径）
- [ ] PATH 已包含 ADB（验证：`adb version` 成功）
- [ ] local.properties 已配置正确
- [ ] 已重启电脑
- [ ] `.\Check-System-Ready.bat` 显示 5/5 通过
- [ ] `.\check_env.bat` 所有项都是 ✓

---

## 📚 参考文档

- **SETUP_GUIDE.md** - 详细的配置说明
- **ENVIRONMENT_SETUP.md** - 完整的环境变量指南
- **VSCODE_GUIDE.md** - VS Code 使用指南
- **README.md** - 项目完整说明

---

## 💬 需要帮助？

如果卡住了：
1. 查看上面的"常见问题"部分
2. 阅读 SETUP_GUIDE.md 中的详细步骤
3. 检查环境变量是否正确设置

**记住：这是首次设置，之后就一劳永逸了！** 🎯

祝你配置顺利！加油！💪
