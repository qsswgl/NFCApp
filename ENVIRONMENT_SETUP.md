# 🔧 完整的环境配置和验证指南

**最后更新**：2025年11月2日

---

## 📋 环境检查清单

在开始开发前，请按照以下步骤进行环境配置和验证。

### ✅ 自动检查脚本

**最快方式 - 运行自动检查脚本**

#### 📍 Windows (PowerShell)

```powershell
# 1. 打开 PowerShell (以管理员身份)
# 2. 进入项目目录
cd k:\NFC\NFCApp

# 3. 运行检查脚本
.\check_environment.ps1

# 如果遇到脚本执行权限问题，先运行:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

#### 📍 Windows (Command Prompt)

```cmd
REM 1. 打开 CMD
REM 2. 进入项目目录
cd k:\NFC\NFCApp

REM 3. 运行检查脚本
check_environment.bat
```

---

## 🔴 如果自动检查失败

### 问题 1️⃣：Java 未找到

**症状**：
```
✗ Java 未安装或未添加到 PATH
```

**解决方案**：

1. **下载 Java**
   - 访问：https://www.oracle.com/java/technologies/downloads/
   - 下载 JDK 11 或更新版本
   - 选择你的操作系统版本

2. **安装 Java**
   - Windows：通常安装到 `C:\Program Files\Java\jdk-xx`
   - 记住安装路径

3. **设置环境变量**

   **Windows (图形界面)**
   - 打开系统属性：`Win+X` → 系统 → 高级系统设置
   - 点击"环境变量"按钮
   - 新建系统变量：
     - 名称：`JAVA_HOME`
     - 值：`C:\Program Files\Java\jdk-xx` (你的安装路径)
   - 编辑 PATH 变量，添加：`%JAVA_HOME%\bin`
   - 点击确定，重启电脑

   **Windows (PowerShell 命令)**
   ```powershell
   # 添加 JAVA_HOME 环境变量
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-xx", "Machine")
   
   # 将 Java bin 添加到 PATH
   $path = [Environment]::GetEnvironmentVariable("PATH", "Machine")
   [Environment]::SetEnvironmentVariable("PATH", "$path;C:\Program Files\Java\jdk-xx\bin", "Machine")
   ```

4. **验证安装**
   ```bash
   java -version
   ```

---

### 问题 2️⃣：Android SDK 未找到

**症状**：
```
✗ Android SDK 未找到
```

**解决方案**：

1. **下载 Android Studio**
   - 访问：https://developer.android.com/studio
   - 下载 Android Studio

2. **安装 Android Studio**
   - 运行安装程序
   - 选择标准安装选项
   - 完成初始设置（下载 SDK 文件）
   - 记住 SDK 安装路径

3. **设置环境变量**

   **Windows (图形界面)**
   - 打开系统属性：`Win+X` → 系统 → 高级系统设置
   - 点击"环境变量"按钮
   - 新建系统变量：
     - 名称：`ANDROID_HOME`
     - 值：`C:\Users\你的用户名\AppData\Local\Android\sdk`
   - 编辑 PATH 变量，添加：`%ANDROID_HOME%\platform-tools`
   - 点击确定，重启电脑

   **Windows (PowerShell 命令)**
   ```powershell
   $androidSdk = "C:\Users\$env:USERNAME\AppData\Local\Android\sdk"
   
   # 设置 ANDROID_HOME
   [Environment]::SetEnvironmentVariable("ANDROID_HOME", $androidSdk, "Machine")
   
   # 添加到 PATH
   $path = [Environment]::GetEnvironmentVariable("PATH", "Machine")
   [Environment]::SetEnvironmentVariable("PATH", "$path;$androidSdk\platform-tools", "Machine")
   ```

4. **验证安装**
   ```bash
   adb version
   ```

---

### 问题 3️⃣：ADB 未找到

**症状**：
```
✗ ADB 未找到
```

**解决方案**：

1. **检查 Android SDK 是否安装了 Platform Tools**
   - 打开 Android Studio
   - Tools → SDK Manager
   - 确保 "Android SDK Platform Tools" 已安装
   - 如果未安装，点击安装

2. **确保 ADB 在 PATH 中**
   ```bash
   # Windows PowerShell
   echo $env:ANDROID_HOME
   
   # 应该返回类似: C:\Users\你的用户名\AppData\Local\Android\sdk
   ```

3. **手动添加到 PATH**（如果上面未设置）
   - 参考"Android SDK 未找到"的解决方案中的第 3 步

4. **验证安装**
   ```bash
   adb devices
   ```

---

### 问题 4️⃣：local.properties 未找到

**症状**：
```
✗ local.properties 未找到
```

**解决方案**：

1. **快速创建 local.properties**

   **方法 1：从模板复制**
   ```bash
   # Windows PowerShell
   Copy-Item .\local.properties.template .\local.properties
   ```

   **方法 2：手动创建**
   - 在项目根目录创建文本文件
   - 命名为 `local.properties`
   - 添加以下内容：
   ```properties
   sdk.dir=C:\Users\你的用户名\AppData\Local\Android\sdk
   ```

2. **配置 SDK 路径**
   - 打开 `local.properties`
   - 修改 `sdk.dir` 的值为你的 Android SDK 路径
   - 保存文件

---

### 问题 5️⃣：Gradle 问题

**症状**：
```
Gradle build failed
```

**解决方案**：

1. **清理 Gradle 缓存**
   ```bash
   # Windows
   .\gradlew.bat clean
   
   # Mac/Linux
   ./gradlew clean
   ```

2. **重新构建**
   ```bash
   # Windows
   .\gradlew.bat build
   
   # Mac/Linux
   ./gradlew build
   ```

3. **如果仍然失败**
   - 删除 `.gradle` 文件夹
   - 删除 `build` 文件夹
   - 重新运行构建

---

## 🟢 验证环境已就绪

所有检查通过后，你会看到：
```
✓ 所有检查通过！您可以开始开发了。
```

现在可以：

1. **打开 VS Code**
   ```bash
   code k:\NFC\NFCApp
   ```

2. **运行构建**
   - 按 `Ctrl+Shift+P`
   - 输入 `Tasks: Run Task`
   - 选择 `Build Project` 或 `Build, Install and Run`

3. **开始开发！** 🚀

---

## 📱 额外：设置 Android 设备或模拟器

### 使用真实 Android 设备

1. **连接设备**
   - 用 USB 线连接 Android 手机
   - 在手机上启用"开发者选项"
   - 启用"USB 调试"

2. **验证连接**
   ```bash
   adb devices
   ```
   应该显示你的设备

### 使用 Android 模拟器

1. **在 Android Studio 中创建虚拟设备**
   - Tools → AVD Manager
   - Create Virtual Device
   - 选择设备类型和 API 级别（推荐 API 30 或更高）
   - 创建

2. **启动模拟器**
   - 在 AVD Manager 中点击播放按钮
   - 等待模拟器启动

3. **验证连接**
   ```bash
   adb devices
   ```
   应该显示 `emulator-xxxx`

---

## 🆘 常见问题 Q&A

**Q: 环境变量设置后仍未生效？**
A: 重启 PowerShell、CMD 或整个电脑

**Q: Java 和 Android SDK 可以安装在其他位置吗？**
A: 可以，但需要相应修改环境变量

**Q: 可以使用其他 Java 版本吗？**
A: 推荐 JDK 11+，其他版本可能不兼容

**Q: 没有 Android 设备可以测试吗？**
A: 可以，使用 Android 模拟器

**Q: 什么是 Gradle Wrapper？**
A: 项目包含的 Gradle 版本管理工具，不需要全局安装

---

## 📞 获取帮助

如遇到问题：

1. **查看项目文档**
   - `README.md` - 项目说明
   - `QUICKSTART.md` - 快速开始
   - `VSCODE_GUIDE.md` - VS Code 使用指南

2. **检查日志**
   ```bash
   adb logcat -s NFCApp
   ```

3. **清理并重建**
   ```bash
   ./gradlew.bat clean build
   ```

---

**一切准备就绪后，就可以开始开发了！** 🎉

有问题？查看上面的"常见问题"或项目文档。
