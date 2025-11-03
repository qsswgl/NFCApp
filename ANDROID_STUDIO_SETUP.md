# Android Studio 配置向导

## 📋 第一步：打开 Android Studio

1. 按 **Windows 键**
2. 输入 `Android Studio`
3. 点击打开

---

## 📋 第二步：完成初始化向导

### 欢迎界面
- 点击 **Next**

### 安装类型
- 选择 **Standard** （推荐）
- 点击 **Next**

### UI 主题
- 选择你喜欢的主题（Light 或 Darcula）
- 点击 **Next**

### 验证设置
- 查看将要下载的组件
- 点击 **Next**

### 许可协议
- ✅ 勾选 **Accept**（所有项）
- 点击 **Finish**

### 下载组件
- ⏳ 等待下载完成（约 5-10 分钟）
- 下载内容包括：
  - Android SDK
  - Android SDK Platform
  - Android Emulator
  - 等等...

---

## 📋 第三步：获取 SDK 路径

### 方法 1：从欢迎界面
1. Android Studio 打开后
2. 点击右下角 **⚙️ Configure** 或 **More Actions**
3. 选择 **SDK Manager**
4. 在顶部看到 **Android SDK Location**
5. 复制完整路径

### 方法 2：从项目界面
1. 如果已创建/打开项目
2. 顶部菜单：**Tools** → **SDK Manager**
3. 在 **Android SDK Location** 处复制路径

### 典型路径示例
```
C:\Users\Administrator\AppData\Local\Android\Sdk
C:\Users\YourName\AppData\Local\Android\Sdk
```

---

## 📋 第四步：回到终端配置

**复制了 SDK 路径后，回到这里告诉我路径，我会自动配置！**

或者手动运行：
```powershell
cd k:\NFC\NFCApp
.\Setup-LocalProperties.bat
```

在提示时输入你的 SDK 路径。

---

## 💡 常见问题

### Q：初始化很慢？
A：这是正常的，Android SDK 需要下载约 3-5 GB 的内容

### Q：找不到 SDK Manager？
A：
- 欢迎界面：点击右下角 ⚙️ Configure → SDK Manager
- 项目界面：Tools → SDK Manager

### Q：下载失败？
A：
- 检查网络连接
- 重试下载
- 或使用 VPN

### Q：我可以跳过这一步吗？
A：可以，但无法运行 Android 应用。你可以：
- 先完成 Java 配置
- 稍后再安装 Android Studio
- 或使用已有的 SDK

---

## ✅ 完成标志

当你看到以下任意一个，说明初始化完成：
- Welcome to Android Studio 界面
- Projects 列表界面
- SDK Manager 中显示了已安装的组件

---

**准备好了？去打开 Android Studio 吧！** 🚀

**完成后回到终端告诉我 SDK 路径！** 💬
