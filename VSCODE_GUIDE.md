# VS Code 中构建和运行 NFC 应用指南

## 🎯 前置要求

在 VS Code 中使用之前，确保已安装：

1. **Gradle** - 项目已包含 Gradle Wrapper (`gradlew.bat`)
2. **Java Development Kit (JDK)** - 11 或更高版本
3. **Android SDK** - 已配置在 `local.properties` 中
4. **ADB (Android Debug Bridge)** - 在系统 PATH 中

---

## 🚀 在 VS Code 中构建和运行

### 方法 1：使用 VS Code 任务（推荐）

**第 1 步：打开任务运行器**
- 按 `Ctrl+Shift+P` 打开命令面板
- 输入 `Tasks: Run Task`
- 选择一个任务

**可用的任务：**

| 任务名称 | 描述 |
|--------|------|
| **Build Project** | 完整构建项目（默认构建任务） |
| **Gradle: Clean** | 清理构建文件 |
| **Gradle: Assemble Debug APK** | 生成 Debug APK |
| **Gradle: Install Debug APK** | 安装 APK 到设备 |
| **Gradle: Run Unit Tests** | 运行单元测试 |
| **Build and Install** | 构建并安装 APK |
| **Build, Install and Run** | 构建、安装并启动应用 |
| **ADB: List Devices** | 查看已连接设备 |

**快速开始：**
```
1. 按 Ctrl+Shift+B 运行默认构建任务 (Build Project)
2. 或 Ctrl+Shift+P → Tasks: Run Task → Build, Install and Run
```

### 方法 2：使用集成终端

**打开终端：**
- 按 `` Ctrl+` `` 打开 VS Code 集成终端

**常用命令：**

```bash
# 构建项目
./gradlew.bat build

# 清理
./gradlew.bat clean

# 生成 Debug APK
./gradlew.bat assembleDebug

# 安装 APK
./gradlew.bat installDebug

# 查看已连接的设备
adb devices

# 启动应用
adb shell am start -n com.nfc.app/.MainActivity

# 查看日志
adb logcat -s NFCApp
```

### 方法 3：完整的一键构建和运行

```bash
# 构建、安装并启动应用
./gradlew.bat assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.nfc.app/.MainActivity
```

---

## 📋 快速命令参考

### 🏗️ 构建命令

```bash
# 完整构建
./gradlew.bat build

# 清理
./gradlew.bat clean

# 只构建 Debug
./gradlew.bat assembleDebug

# 只构建 Release
./gradlew.bat assembleRelease

# 构建同时生成 APK
./gradlew.bat build assembleDebug
```

### 📱 ADB 命令

```bash
# 查看设备
adb devices

# 安装 APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 卸载应用
adb uninstall com.nfc.app

# 启动应用
adb shell am start -n com.nfc.app/.MainActivity

# 停止应用
adb shell am force-stop com.nfc.app

# 查看应用日志
adb logcat -s NFCApp

# 清除日志
adb logcat -c

# 设备重启
adb reboot
```

### 🧪 测试命令

```bash
# 运行单元测试
./gradlew.bat test

# 运行集成测试
./gradlew.bat connectedAndroidTest

# 运行特定测试类
./gradlew.bat test --tests com.nfc.app.*
```

---

## 🔧 配置 VS Code

### 1. **安装推荐的扩展**

打开 VS Code 扩展市场搜索：

- **Gradle for Java** - `vscjava.vscode-gradle`
- **Code Runner** - `formulahendry.code-runner`（可选）
- **Better Comments** - `aaron-bond.better-comments`（可选）
- **Markdown Preview Enhanced** - `shd101wyy.markdown-preview-enhanced`（可选）

### 2. **配置 Java 环境**

VS Code 会自动检测 JDK。如需手动配置，编辑 `.vscode/settings.json`：

```json
{
  "java.home": "C:\\Program Files\\Java\\jdk-11",
  "java.configuration.updateBuildConfiguration": "automatic"
}
```

### 3. **配置 Android SDK**

确保 `local.properties` 正确配置：
```properties
sdk.dir=C:\\Users\\你的用户名\\AppData\\Local\\Android\\sdk
```

---

## 🎮 快捷键设置（可选）

如果想快速运行特定任务，可在 VS Code 快捷键配置中添加：

**打开快捷键配置：**
- `Ctrl+K Ctrl+S` 或 File → Preferences → Keyboard Shortcuts

**添加自定义快捷键：**

编辑 `keybindings.json`：

```json
[
  {
    "key": "ctrl+alt+b",
    "command": "workbench.action.tasks.runTask",
    "args": "Build Project"
  },
  {
    "key": "ctrl+alt+i",
    "command": "workbench.action.tasks.runTask",
    "args": "Build, Install and Run"
  },
  {
    "key": "ctrl+alt+d",
    "command": "workbench.action.tasks.runTask",
    "args": "ADB: List Devices"
  }
]
```

然后可以使用：
- `Ctrl+Alt+B` 快速构建
- `Ctrl+Alt+I` 构建、安装并运行
- `Ctrl+Alt+D` 查看设备

---

## 🐛 故障排除

### 问题 1：找不到 Gradle
**解决方案：**
```bash
# 使用项目中的 Gradle Wrapper
./gradlew.bat --version
```

### 问题 2：找不到 Java
**解决方案：**
```bash
# 检查 JAVA_HOME
echo %JAVA_HOME%

# 如果为空，在终端设置它
set JAVA_HOME=C:\Program Files\Java\jdk-11
```

### 问题 3：找不到 Android SDK
**解决方案：**
- 编辑 `local.properties`
- 设置正确的 SDK 路径

### 问题 4：构建失败
**解决方案：**
```bash
# 清理并重建
./gradlew.bat clean build
```

### 问题 5：设备未检测到
**解决方案：**
```bash
# 检查设备连接
adb devices

# 如果没有显示，重启 ADB 服务
adb kill-server
adb start-server
adb devices
```

---

## 📊 终端输出示例

### 成功构建
```
> Task :app:compileDebugKotlin
> Task :app:compileDebugJavaWithJavac
> Task :app:linkDebugJavaResources
> Task :app:packageDebug
> Task :app:assembleDebug

BUILD SUCCESSFUL in 45s
```

### 成功安装
```
> Task :app:installDebug
Installing APK 'app-debug.apk' ...
Installed Successfully.

BUILD SUCCESSFUL in 5s
```

---

## 💡 最佳实践

1. **定期清理**
   ```bash
   ./gradlew.bat clean
   ```

2. **构建前检查设备**
   ```bash
   adb devices
   ```

3. **使用 Logcat 调试**
   - 打开终端并运行 `adb logcat -s NFCApp`
   - 查看实时日志输出

4. **避免频繁的完整构建**
   - 只修改代码时，使用增量构建
   - 完整构建仅在需要清理时进行

5. **使用任务快捷键**
   - 设置常用任务的快捷键以提高效率

---

## 🚀 推荐工作流

### 开发流程

```
1. 打开 VS Code 和项目
2. 连接 Android 设备
3. 在终端运行 `adb logcat -s NFCApp`（新终端）
4. 按 Ctrl+Alt+I 构建、安装并运行
5. 查看 Logcat 输出进行调试
6. 修改代码后重复第 4 步
```

### 快捷键配置后

```
按 Ctrl+Alt+B     → 快速构建
按 Ctrl+Alt+I     → 构建、安装并运行
按 Ctrl+Alt+D     → 查看连接的设备
```

---

## 📚 相关文档

- `README.md` - 项目完整说明
- `DEVELOPMENT.md` - 开发指南
- `QUICKSTART.md` - 快速开始

---

**VS Code 中开发愉快！** 🎉

有问题？查看上面的"故障排除"部分或查看项目文档。
