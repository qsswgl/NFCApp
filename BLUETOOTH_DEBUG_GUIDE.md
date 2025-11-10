# 蓝牙打印机调试指南

## 问题描述

**现象**: GP-C58_9B99 打印机已配对，但连接失败，提示"打印机连接失败"

**目标**: 通过增强日志分析连接失败的根本原因

---

## 操作步骤

### 第一步：安装增强日志版本

在**物理机**上打开 PowerShell 或命令提示符，执行：

```powershell
cd K:\NFC\NFCApp
.\install-to-phone.bat
```

或者手动安装：

```powershell
K:\tool\adb\adb.exe install -r K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

**验证**: 手机上应用图标更新，打开应用无闪退

---

### 第二步：收集诊断日志

#### 方法 A: 使用自动化脚本（推荐）

```powershell
cd K:\NFC\NFCApp
.\collect-bluetooth-log.bat
```

脚本会：
1. 清除旧日志
2. 提示你在手机上执行打印操作
3. 实时显示并保存日志到文件

#### 方法 B: 手动收集

**终端1** - 清除旧日志：
```powershell
K:\tool\adb\adb.exe logcat -c
```

**终端2** - 实时监控：
```powershell
K:\tool\adb\adb.exe logcat -s NFCApp:V BluetoothPrinter:V
```

**保存到文件**：
```powershell
K:\tool\adb\adb.exe logcat -s NFCApp:V BluetoothPrinter:V > K:\bluetooth_debug.log
```

---

### 第三步：重现问题

在手机应用中：

1. **启动应用** - 确保蓝牙已开启
2. **输入测试数据**：
   - 卡号: `1234567890123456`
   - 车号: `1` (默认值)
   - 金额: `100.50`
3. **点击打印按钮** 🖨️
4. **观察结果** - 应显示"打印机连接失败"

---

## 日志分析关键点

### 期望看到的日志内容

```
========== 开始打印流程 ==========
打印参数: 卡号: 1234567890123456, 车号: 1, 金额: 100.50
Step 1: 尝试连接打印机...

========== 自动查找并连接打印机 ==========
蓝牙适配器是否为空: false
蓝牙适配器是否已启用: true
✓ 找到 X 个已配对设备:
  [0] 名称: GP-C58_9B99, 地址: XX:XX:XX:XX:XX:XX
  [1] 名称: 其他设备, 地址: XX:XX:XX:XX:XX:XX
✓ 在索引 0 找到目标打印机: GP-C58_9B99 (XX:XX:XX:XX:XX:XX)

========== 开始连接蓝牙设备 ==========
目标设备地址: XX:XX:XX:XX:XX:XX
蓝牙适配器是否为空: false
蓝牙适配器是否已启用: true
当前连接状态: false
✓ 找到目标设备: GP-C58_9B99
  设备名称: GP-C58_9B99
  设备地址: XX:XX:XX:XX:XX:XX
  设备类型: 1
  绑定状态: 12
尝试创建RFCOMM套接字...
套接字创建结果: not null
开始连接...
连接耗时: XXXms
✓✓✓ 连接成功！
```

### 关键诊断信息

#### 1. 蓝牙适配器检查
```
蓝牙适配器是否为空: true   ❌ 严重问题：无蓝牙硬件或权限被拒绝
蓝牙适配器是否已启用: false ❌ 用户问题：需要开启蓝牙
```

#### 2. 设备发现
```
✓ 找到 0 个已配对设备:     ❌ 设备未配对或权限问题
❌ 未找到匹配的打印机设备    ❌ 设备名称不匹配或已取消配对
```

#### 3. 设备绑定状态
```
绑定状态: 10 (BOND_NONE)     ❌ 未配对
绑定状态: 11 (BOND_BONDING)  ⚠️  配对中，稍后重试
绑定状态: 12 (BOND_BONDED)   ✓ 正常
```

#### 4. 套接字创建
```
套接字创建结果: null          ❌ UUID错误或权限不足
❌❌❌ 主连接方法失败，尝试备用方法...  ⚠️  标准连接失败，使用反射
```

#### 5. 连接异常

**常见异常类型**：

| 异常信息 | 可能原因 | 解决方案 |
|---------|---------|---------|
| `read failed, socket might closed [socket is closed]` | 设备忙碌、通道错误、设备不兼容 | 尝试不同通道(1-30)、重启打印机 |
| `Connection refused` | UUID错误、设备不支持SPP | 尝试替代UUID或不安全RFCOMM |
| `Permission denied` | 运行时权限未授予 | 检查应用权限设置 |
| `Device or resource busy` | 其他应用占用打印机 | 关闭其他打印应用 |
| `Host is down` | 设备电量低或关机 | 检查打印机电源 |

---

## 快速诊断决策树

```
日志中是否找到 GP-C58_9B99？
├─ 否 → 检查设备名称是否完全匹配，尝试在系统设置中重新配对
└─ 是 → 绑定状态是多少？
    ├─ 10 (BOND_NONE) → 设备已取消配对，需要重新配对
    ├─ 11 (BOND_BONDING) → 配对进行中，等待完成后重试
    └─ 12 (BOND_BONDED) → 套接字创建成功吗？
        ├─ null → 权限问题或UUID不支持
        └─ not null → 连接时抛出什么异常？
            ├─ read failed → 通道错误，需要修改createRfcommSocket参数
            ├─ Connection refused → UUID不匹配，尝试不安全连接
            └─ Permission denied → 运行时权限未授予
```

---

## 常见修复方案

### 方案 1: 设备重新配对

```
1. 手机设置 → 蓝牙 → GP-C58_9B99 → 取消配对
2. 打印机关机再开机
3. 重新搜索并配对
4. 再次尝试打印
```

### 方案 2: 修改连接通道

编辑 `BluetoothPrinter.kt` 第 47 行：

```kotlin
// 当前使用通道 1
method.invoke(device, 1)

// 尝试通道 2-5
method.invoke(device, 2)  // 或 3, 4, 5
```

### 方案 3: 使用不安全RFCOMM

编辑 `BluetoothPrinter.kt` 第 36 行：

```kotlin
// 当前使用安全连接
socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)

// 改为不安全连接
socket = device.createInsecureRfcommSocketToServiceRecord(PRINTER_UUID)
```

### 方案 4: 检查运行时权限

确保应用在手机上已授予：
- ✓ 蓝牙权限
- ✓ 附近设备权限（Android 12+）

路径: 设置 → 应用 → NFC读写系统 → 权限

---

## 提交日志

请将日志文件内容（`bluetooth_print_log_YYYYMMDD_HHMMSS.txt`）或终端输出的完整内容发送给开发人员。

**重点关注**：
- "开始连接蓝牙设备" 部分
- 任何带有 "❌" 的行
- 异常堆栈跟踪 (Exception stacktrace)

---

## 技术背景

### 蓝牙打印机连接流程

```
1. 获取BluetoothAdapter
   ↓
2. 检查蓝牙是否开启
   ↓
3. 获取已配对设备列表
   ↓
4. 查找目标设备（GP-C58_9B99）
   ↓
5. 验证绑定状态 (BOND_BONDED=12)
   ↓
6. 创建RFCOMM套接字
   ├─ 主方法: createRfcommSocketToServiceRecord(UUID)
   └─ 备用方法: createRfcommSocket(channel) 反射调用
   ↓
7. 连接套接字 (socket.connect())
   ↓
8. 获取输出流并发送ESC/POS命令
```

### 使用的SPP UUID

```
00001101-0000-1000-8000-00805F9B34FB
标准串口服务(Serial Port Profile)
```

### ESC/POS命令示例

```
初始化: ESC @ (0x1B 0x40)
居中对齐: ESC a 1 (0x1B 0x61 0x01)
放大字体: GS ! 0x22 (0x1D 0x21 0x22)
切纸: GS V 0 (0x1D 0x56 0x00)
```

---

**文档版本**: 1.0  
**创建日期**: 2024-11-06  
**目标打印机**: GP-C58_9B99 (POS58兼容, 58mm热敏打印机)
