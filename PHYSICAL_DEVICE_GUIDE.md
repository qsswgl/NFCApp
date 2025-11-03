# 物理设备测试指南

## 环境说明
您的开发环境在Hyper-V虚拟机中，无法运行Android模拟器（嵌套虚拟化限制）。
使用物理Android设备是最佳测试方案。

## 📱 准备Android设备

### 步骤1：启用开发者选项
1. 打开**设置** → **关于手机**
2. 连续点击**版本号**或**内部版本号** 7次
3. 系统提示"您已处于开发者模式"

### 步骤2：启用USB调试
1. 返回设置，找到**开发者选项**（可能在**系统**或**更多设置**中）
2. 启用**USB调试**
3. 启用**USB安装**（某些设备有此选项）

### 步骤3：连接设备
1. 使用USB数据线连接设备到电脑
2. 设备上会弹出"允许USB调试"对话框
3. 勾选"始终允许来自这台计算机"
4. 点击**允许**

### 步骤4：验证连接
运行以下命令：
```cmd
C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe devices
```

应该看到类似输出：
```
List of devices attached
1234567890ABCDEF    device
```

## 🚀 安装和运行应用

### 方法1：使用自动化脚本（推荐）
双击运行：`install-and-run.bat`

### 方法2：手动安装
```cmd
# 安装APK
C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk

# 启动应用
C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe shell am start -n com.nfc.app/.MainActivity
```

## 📋 NFC功能测试要点

### 测试前检查
- ✅ 设备必须支持NFC功能
- ✅ 在设置中启用NFC
- ✅ 准备NFC卡片或标签

### 测试流程
1. 打开应用
2. 将NFC卡片靠近设备背部（NFC天线位置）
3. 应用应该能够读取卡片信息
4. 检查是否能正确显示NFCID、卡号等信息

## 🔧 常见问题

### Q: adb devices显示unauthorized
**A:** 设备上没有授权。断开重连，在设备上点击"允许"。

### Q: adb devices显示offline
**A:** 
1. 拔掉USB线，重新连接
2. 或运行：`adb kill-server` 然后 `adb start-server`

### Q: 应用无法读取NFC
**A:**
1. 确认设备支持NFC（设置→连接→NFC）
2. 确认NFC已启用
3. 尝试不同位置靠近卡片（NFC天线位置因设备而异）

### Q: 安装失败：INSTALL_FAILED_UPDATE_INCOMPATIBLE
**A:** 先卸载旧版本：
```cmd
adb uninstall com.nfc.app
```
然后重新安装。

## 📂 重要文件位置

- APK文件：`K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk`
- 安装脚本：`K:\NFC\NFCApp\install-and-run.bat`
- ADB工具：`C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe`

## 🔄 重新构建应用

如果修改了代码，使用以下命令重新构建：
```cmd
.\gradlew.bat assembleDebug
```

构建完成后，重新运行安装脚本即可。

---

**注意**：由于您的环境是Hyper-V虚拟机，USB设备需要在Hyper-V管理器中正确配置USB重定向，确保Android设备能够正确连接到虚拟机。
