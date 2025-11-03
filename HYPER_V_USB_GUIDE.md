# Hyper-V USB设备连接指南

## 问题说明
Android设备连接到物理主机USB，但Hyper-V虚拟机无法直接访问。

## 解决方案

### 方案A：启用增强会话模式（最简单）

1. **在物理主机上**，打开 PowerShell（管理员）：
```powershell
# 启用增强会话模式
Set-VMHost -EnableEnhancedSessionMode $true
```

2. **重新连接虚拟机**时，会看到"连接到虚拟机"对话框
3. 点击"选项" → "本地资源"
4. 展开"本地设备和资源" → 勾选"其他支持的即插即用设备"
5. 连接后，USB设备应该可用

### 方案B：使用USB/IP重定向

1. **在物理主机上**安装 USB/IP：
```powershell
# 下载 usbipd-win
winget install usbipd
```

2. **列出USB设备**：
```cmd
usbipd list
```

3. **绑定Android设备**（假设BUSID是1-1）：
```cmd
usbipd bind --busid 1-1
```

4. **附加到WSL/虚拟机**：
```cmd
usbipd attach --wsl --busid 1-1
```

### 方案C：在物理主机上直接安装（最直接）

**这是最简单的方法！**

1. 在物理主机上下载ADB工具
2. 在物理主机上运行安装命令

#### 物理主机安装步骤：

```cmd
# 1. 下载platform-tools到物理主机
# 访问：https://developer.android.com/tools/releases/platform-tools
# 或直接下载：https://dl.google.com/android/repository/platform-tools-latest-windows.zip

# 2. 解压到任意目录，例如：C:\platform-tools

# 3. 检查设备连接
C:\platform-tools\adb.exe devices

# 4. 从虚拟机复制APK到物理主机
# 可以通过共享文件夹或网络共享

# 5. 在物理主机上安装APK
C:\platform-tools\adb.exe install -r "C:\path\to\app-debug.apk"

# 6. 启动应用
C:\platform-tools\adb.exe shell am start -n com.nfc.app/.MainActivity
```

### 方案D：使用ADB网络调试（推荐！）

**无需USB连接，通过WiFi调试**

#### 在手机上（需要先USB连接一次）：

1. **物理主机上操作**：
```cmd
# 确保手机通过USB连接到物理主机
adb devices

# 启用网络ADB（5555端口）
adb tcpip 5555

# 查看手机IP地址
adb shell ip addr show wlan0
```

2. **断开USB，记下手机IP地址**（例如：192.168.1.100）

3. **在虚拟机中连接**：
```cmd
adb connect 192.168.1.100:5555
```

4. **验证连接**：
```cmd
adb devices
# 应该显示：192.168.1.100:5555    device
```

5. **现在可以正常安装了**：
```cmd
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 快速测试网络ADB

### 物理主机上执行：
```cmd
# 1. 下载platform-tools（如果还没有）
# 2. 连接手机USB
C:\platform-tools\adb.exe devices
C:\platform-tools\adb.exe tcpip 5555
C:\platform-tools\adb.exe shell ip -f inet addr show wlan0 | findstr "inet "
```

### 虚拟机中执行：
```cmd
# 使用上面显示的IP地址
C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe connect 手机IP:5555
C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe devices
```

## 推荐方案顺序

1. **方案D（网络ADB）** - 最灵活，设置一次后无需USB
2. **方案C（物理主机直接安装）** - 最简单直接
3. **方案A（增强会话模式）** - 需要Hyper-V配置
4. **方案B（USB/IP）** - 较复杂

---

**注意**：如果您能在物理主机上访问虚拟机的共享文件夹，强烈建议使用方案D（网络ADB）。
