# ✅ 环境配置完成指南

**日期**：2025年11月2日

根据上面的环境检查结果，以下是需要完成的步骤：

---

## 🔴 需要立即完成的配置

根据上面的检查结果，你需要完成以下配置：

### ❌ Java 未找到

**解决方案**：

1. **下载 Java JDK**
   - 访问：https://www.oracle.com/java/technologies/downloads/
   - 下载 Java SE 11 或更新版本
   - 选择 Windows x64 版本

2. **安装 Java**
   - 运行下载的安装程序
   - 选择默认安装路径（通常是 `C:\Program Files\Java\jdk-xx`）
   - 完成安装

3. **设置环境变量**
   
   **方法 A：使用 GUI（推荐新手）**
   - 按 `Win+Pause/Break` 打开系统属性
   - 或：`控制面板` → `系统和安全` → `系统` → `高级系统设置`
   - 点击 `环境变量` 按钮
   - 在"系统变量"中点击 `新建`
   - 变量名：`JAVA_HOME`
   - 变量值：`C:\Program Files\Java\jdk-xx`（替换 xx 为实际版本号）
   - 点击确定

   **修改 PATH 变量**
   - 在"系统变量"中找到 `PATH`
   - 点击编辑
   - 点击 `新建`
   - 输入：`%JAVA_HOME%\bin`
   - 点击确定，保存所有更改

   **方法 B：使用 PowerShell（快速）**
   ```powershell
   # 以管理员身份运行 PowerShell
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-11", "Machine")
   $env:Path += ";C:\Program Files\Java\jdk-11\bin"
   ```

4. **重启电脑**
   - 关闭 PowerShell 和 VS Code
   - 重启电脑使环境变量生效

5. **验证安装**
   - 重新打开 PowerShell
   - 运行 `java -version`
   - 应该显示 Java 版本信息

---

### ❌ ANDROID_HOME 未设置

**解决方案**：

1. **下载 Android Studio**
   - 访问：https://developer.android.com/studio
   - 下载 Android Studio 最新版本
   - 选择 Windows 版本

2. **安装 Android Studio**
   - 运行安装程序
   - 选择"标准安装"
   - 完成初始设置（会下载 SDK 文件）
   - 记住 SDK 安装路径（通常在 `C:\Users\你的用户名\AppData\Local\Android\sdk`）

3. **设置 ANDROID_HOME**
   
   **方法 A：使用 GUI**
   - 按 `Win+Pause/Break` 打开系统属性
   - 点击 `环境变量` 按钮
   - 在"系统变量"中点击 `新建`
   - 变量名：`ANDROID_HOME`
   - 变量值：`C:\Users\你的用户名\AppData\Local\Android\sdk`
   - 点击确定

   **添加到 PATH**
   - 编辑 `PATH` 变量
   - 点击 `新建`
   - 输入：`%ANDROID_HOME%\platform-tools`
   - 点击确定

   **方法 B：使用 PowerShell**
   ```powershell
   $sdkPath = "C:\Users\$env:USERNAME\AppData\Local\Android\sdk"
   [Environment]::SetEnvironmentVariable("ANDROID_HOME", $sdkPath, "Machine")
   $path = [Environment]::GetEnvironmentVariable("PATH", "Machine")
   [Environment]::SetEnvironmentVariable("PATH", "$path;$sdkPath\platform-tools", "Machine")
   ```

4. **重启电脑**

5. **验证安装**
   - 重新打开 PowerShell
   - 运行 `adb version`
   - 应该显示 ADB 版本信息

---

### ⚠️ local.properties 需要配置

**解决方案**：

1. **方法 A：从模板复制**
   ```powershell
   cd k:\NFC\NFCApp
   Copy-Item .\local.properties.template .\local.properties
   ```

2. **方法 B：手动创建**
   - 在 `k:\NFC\NFCApp` 文件夹中
   - 右键 → 新建 → 文本文档
   - 命名为 `local.properties`
   - 右键 → 打开方式 → 记事本
   - 添加以下内容：
   ```properties
   sdk.dir=C:\Users\你的用户名\AppData\Local\Android\sdk
   ```
   - 保存

3. **方法 C：快速配置**
   - 打开 `local.properties.template`
   - 复制内容
   - 修改 `sdk.dir` 为你的 Android SDK 路径
   - 另存为 `local.properties`

---

## ✅ 所有配置完成后

1. **重新运行检查脚本**
   ```bash
   cd k:\NFC\NFCApp
   .\check_env.bat
   ```
   应该全部显示 ✓ 检查通过

2. **在 VS Code 中打开项目**
   ```bash
   code k:\NFC\NFCApp
   ```

3. **运行第一次构建**
   - 按 `Ctrl+Shift+P`
   - 输入 `Tasks: Run Task`
   - 选择 `Build Project`
   - 等待构建完成

4. **开始开发！**

---

## 🚀 快速验证清单

完成每一步后打勾：

- [ ] Java 已安装（运行 `java -version` 验证）
- [ ] ANDROID_HOME 已设置（运行 `echo %ANDROID_HOME%` 验证）
- [ ] ADB 可用（运行 `adb version` 验证）
- [ ] local.properties 已配置（查看文件内容）
- [ ] Gradle Wrapper 已找到（`gradlew.bat` 存在）
- [ ] 项目结构完整（看到 `app/build.gradle.kts`）

全部打勾后即可开始开发！

---

## 💻 常用命令速查

```bash
# 检查环境
java -version
echo %ANDROID_HOME%
adb devices
echo %PATH%

# 项目构建
cd k:\NFC\NFCApp
.\gradlew.bat build

# 查看日志
adb logcat -s NFCApp

# 连接设备检查
adb devices

# VS Code 打开项目
code k:\NFC\NFCApp
```

---

## 📞 如遇到问题

1. **查看详细配置指南**
   → 打开 `ENVIRONMENT_SETUP.md`

2. **查看 VS Code 使用指南**
   → 打开 `VSCODE_GUIDE.md`

3. **查看项目完整说明**
   → 打开 `README.md`

---

## 🎉 下一步

所有环境配置完成后：

1. 在 VS Code 中打开项目
2. 连接 Android 设备或启动模拟器
3. 按 `Ctrl+Shift+P` → `Tasks: Run Task` → `Build, Install and Run`
4. 应用启动！

**祝你开发顺利！** 🚀
