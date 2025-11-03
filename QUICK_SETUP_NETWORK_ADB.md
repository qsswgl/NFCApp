# 快速设置指南

## 在物理主机上执行（只需一次）

打开命令提示符或PowerShell，执行：

```cmd
# 如果物理主机有ADB（Android Studio或SDK）
adb devices
adb tcpip 5555
adb shell ip -f inet addr show wlan0 | findstr "inet "
```

**或者**，如果物理主机没有ADB：

1. 下载platform-tools：https://dl.google.com/android/repository/platform-tools-latest-windows.zip
2. 解压到 C:\platform-tools
3. 执行：
```cmd
C:\platform-tools\adb.exe devices
C:\platform-tools\adb.exe tcpip 5555
C:\platform-tools\adb.exe shell ip -f inet addr show wlan0
```

记下显示的IP地址（格式如：inet 192.168.1.100/24）

---

## 在虚拟机中执行

1. 运行连接脚本：
```cmd
.\connect-network-adb.bat
```

2. 输入手机IP地址（例如：192.168.1.100）

3. 如果连接成功，运行安装脚本：
```cmd
.\install-and-run.bat
```

---

## 常见IP地址范围

- 家庭WiFi：192.168.0.x 或 192.168.1.x
- 公司网络：10.x.x.x 或 172.16.x.x

---

## 测试连接

在虚拟机PowerShell中：
```powershell
# 测试网络连通性（替换为实际IP）
Test-NetConnection -ComputerName 192.168.1.100 -Port 5555
```

如果显示 TcpTestSucceeded : True，说明网络连接正常。
