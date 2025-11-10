# AQ打印机调试指南

## 当前测试情况
- ✅ GP开头打印机：连接正常，打印正常
- ❌ AQ开头打印机：连接正常，打印无反应

## 增强功能

已添加以下增强功能到新版本应用中：

### 1. 详细日志输出
- 连接状态详细追踪
- 服务发现详细信息
- 所有特征（Characteristic）的UUID、属性、权限
- 数据发送过程完整记录（包括十六进制显示）
- MTU变更追踪

### 2. AQ设备特殊处理
- 自动识别AQ开头的设备
- 使用分片发送模式（20字节/片）
- 每片之间50ms延迟，避免数据丢失

### 3. 写入类型优化
- 优先使用WRITE_NO_RESPONSE模式
- 自动MTU协商（请求512字节）

## 测试步骤

### 第一步：连接AQ打印机并查看日志

1. 在手机上打开应用
2. 点击"开始扫描"
3. 找到 **AQ-V258000114(BLE)** 并点击连接
4. 等待连接成功（卡片变绿）
5. **不要立即点击打印**，先在电脑上查看日志

在电脑PowerShell中运行：
```powershell
adb logcat -s BluetoothConnection:D MainActivity:D | Select-String "AQ|===|Characteristic|Service|UUID|Properties"
```

### 第二步：查看服务和特征信息

从日志中找到以下关键信息：

**AQ打印机的服务**：
```
Service UUID: 0000ae30-0000-1000-8000-00805f9b34fb
Service UUID: 0000ae3a-0000-1000-8000-00805f9b34fb
```

**可写特征**：
- `0000ae01` - Properties: 12 (0x0C = READ + WRITE)
- `0000ae3b` - Properties: 12 (0x0C = READ + WRITE)

**GP打印机的服务** (对比)：
```
Service UUID: 49535343-fe7d-4ae5-8fa9-9fafd205e455
```

**可写特征**:
- `49535343-8841-43f4-a8d4-ecbe34729bb3` - Properties: 8 (WRITE_NO_RESPONSE)

### 第三步：点击测试打印并分析

1. 点击"测试打印"按钮
2. 观察日志输出

**预期看到的日志**：
```
MainActivity: === Test Print ===
MainActivity: Device: AQ-V258000114(BLE)
MainActivity: Is AQ device: true
MainActivity: Print data size: XXX bytes
MainActivity: AQ设备使用分片发送模式...
BluetoothConnection: === Send Data In Chunks ===
BluetoothConnection: Total chunks: XX
BluetoothConnection: Sending chunk 1: ...
BluetoothConnection: === Send Data Request ===
BluetoothConnection: Data (hex): 1B 40 ...
BluetoothConnection: Using characteristic: 0000ae01-...
BluetoothConnection: ✓ Data send initiated successfully
```

## 关键问题分析

### 问题1：选择的特征不正确

从之前的日志看，AQ打印机有多个可写特征：
- `0000ae01` (Properties: 12)
- `0000ae3b` (Properties: 12)

Properties=12 表示：READ(2) + WRITE(8) = 10，但实际是 0x0C

Properties位定义：
- 0x02 = READ
- 0x04 = WRITE_NO_RESPONSE  
- 0x08 = WRITE
- 0x10 = NOTIFY

**Properties=0x12 (18) = READ(2) + NOTIFY(16)**
**Properties=0x0C (12) = WRITE_NO_RESPONSE(4) + WRITE(8)**

需要确认应用选择了哪个特征。

### 问题2：数据格式或编码

AQ打印机可能：
- 使用不同的命令集
- 需要特殊的初始化序列
- 使用不同的字符编码

### 问题3：数据发送方式

- GP打印机：WRITE_NO_RESPONSE(8)，单次发送
- AQ打印机：WRITE(8) + WRITE_NO_RESPONSE(4)，可能需要分片

## 下一步操作

请运行以下命令查看完整的连接和打印日志：

```powershell
# 清除旧日志
adb logcat -c

# 查看完整日志
adb logcat BluetoothConnection:D MainActivity:D BluetoothScanner:D *:E
```

然后在手机上：
1. 连接到AQ打印机
2. 点击测试打印
3. 将日志输出复制给我分析

或者运行：
```powershell
adb logcat -d > k:\Print\logcat_aq.txt
```

将日志保存到文件后查看。
