# BLE 与 Classic 蓝牙地址转换修复

## 问题背景

AQ 打印机支持两种蓝牙模式,拥有**两个不同的蓝牙地址**:

### 1. BLE (低功耗蓝牙) 模式
- **用途**: 设备发现和扫描
- **特征**: 
  - 设备名称带 `(BLE)` 后缀,如 `AQ-V258007640(BLE)`
  - BLE 地址示例: `CD:AC:B0:F0:31:FC`
  - **无需配对** 即可被扫描到
  - **无法用于数据传输**

### 2. Classic Bluetooth (经典蓝牙) 模式
- **用途**: 数据传输和打印
- **特征**:
  - 设备名称无 `(BLE)` 后缀,如 `AQ-V258007640`
  - Classic 地址示例: `CD:AC:B0:07:00:72`
  - **必须配对** 才能使用
  - **实际打印时使用此地址**

## 问题描述

**症状**: 
- 用户在打印机列表中看到 `AQ-V258007640(BLE)`
- 选择后打印失败,提示"打印失败,请检查打印机"

**根本原因**:
应用程序直接使用 BLE 地址 (`CD:AC:B0:F0:31:FC`) 连接 PUQU SDK,但 SDK 需要的是 Classic 地址 (`CD:AC:B0:07:00:72`)。

## 解决方案

### 1. 地址转换逻辑

在 `PuQuPrinterManager.kt` 中实现了三层转换机制:

```kotlin
// 步骤1: 检测是否为 BLE 设备
private fun isLikelyBLEAddress(address: String): Boolean {
    // 检查设备名是否包含 "(BLE)"
    // 检查是否存在同名但不同地址的 Classic 设备
}

// 步骤2: 查找对应的 Classic 地址
private fun getClassicAddressForBLEDevice(
    deviceNameOrAddress: String, 
    address: String
): String? {
    // 方法1: 通过地址查找设备,移除 "(BLE)" 后缀,查找同名 Classic 设备
    // 方法2: 直接通过设备名称查找
}

// 步骤3: 在打印时自动转换
suspend fun printToAddress(...) {
    // 检测到 BLE 设备时自动转换
    val actualAddress = if (isLikelyBLEAddress(printerAddress)) {
        getClassicAddressForBLEDevice(...) ?: printerAddress
    } else {
        printerAddress
    }
    
    // 使用转换后的 Classic 地址连接
    connectToPrinter(actualAddress)
}
```

### 2. 日志输出

修复后的代码会输出详细的转换日志:

```
========== 打印到指定打印机 ==========
原始地址: CD:AC:B0:F0:31:FC
检测到 BLE 设备,尝试获取 Classic 地址...
✓ 已转换: BLE -> Classic 地址: CD:AC:B0:07:00:72
实际连接地址: CD:AC:B0:07:00:72
```

## 用户配对要求

⚠️ **重要**: 用户必须在系统蓝牙设置中配对 **Classic 版本** 的设备:

### 配对步骤:
1. 打开手机 **设置 → 蓝牙**
2. 在"可用设备"中找到 **不带 (BLE) 后缀** 的设备
   - ✅ 正确: `AQ-V258007640`
   - ❌ 错误: `AQ-V258007640(BLE)`
3. 点击配对 (可能需要输入 PIN 码: `0000` 或 `1234`)
4. 配对成功后返回应用

### 为什么不能配对 BLE 版本?
- BLE 设备**无法配对** (系统不会显示配对选项)
- BLE 仅用于设备发现,不支持 SPP 数据传输协议
- PUQU SDK 需要通过 SPP 协议传输打印数据

## 测试验证

### 1. 检查配对状态
```bash
adb shell dumpsys bluetooth_manager | findstr "AQ-V"
```

应该看到:
```
Name: AQ-V258007640  (无 BLE 后缀)
Address: CD:AC:B0:07:00:72
Bond state: BOND_BONDED (已配对)
```

### 2. 查看应用日志
```bash
adb logcat -s PuQuPrinterManager:D MainActivity:D
```

关键日志:
```
[PuQuPrinterManager] 检测到 BLE 设备,尝试获取 Classic 地址...
[PuQuPrinterManager] ✓ 已转换: BLE -> Classic 地址: CD:AC:B0:07:00:72
[PuQuPrinterManager] ✓ 打印机连接成功
[PuQuPrinterManager] 后台线程: printJob() 完成
[MainActivity] ✓ 2份小票打印完成
```

## 参考 Print 项目实现

Print 项目 (`K:\NFC\Print`) 已正确处理此问题:

**MainActivity.kt (Lines 293-330)**:
```kotlin
private fun connectAQPrinterWithSDK(device: BleDevice) {
    val bleDeviceName = device.name
    val classicDeviceName = bleDeviceName.replace("(BLE)", "").trim()
    
    // 在已配对设备中查找 Classic 版本
    val pairedDevice = bluetoothAdapter?.bondedDevices?.find {
        it.name.equals(classicDeviceName, ignoreCase = true)
    }
    
    if (pairedDevice != null) {
        // 使用 Classic 地址连接
        puquPrinterManager.connect(pairedDevice.address)
    } else {
        // 提示用户配对
        showPairInstructionDialog(device, classicDeviceName)
    }
}
```

## 修改文件清单

- ✅ `PuQuPrinterManager.kt`:
  - 新增 `isLikelyBLEAddress()` - 判断是否为 BLE 设备
  - 新增 `getClassicAddressForBLEDevice()` - 地址转换
  - 修改 `printToAddress()` - 打印前自动转换地址
  - 修改 `printReceiptContent()` - 等待打印完成

## 预期效果

### 修复前:
```
用户选择: AQ-V258007640(BLE)
↓
使用 BLE 地址: CD:AC:B0:F0:31:FC
↓
SDK 连接失败 ❌
↓
打印失败
```

### 修复后:
```
用户选择: AQ-V258007640(BLE) 
↓
检测到 BLE 设备
↓
自动转换为 Classic 地址: CD:AC:B0:07:00:72
↓
SDK 连接成功 ✅
↓
打印成功,输出 2 张小票
```

## 故障排查

### 问题: 日志显示"未找到 Classic 地址"

**检查清单**:
1. ✅ 是否在系统设置中配对了 **不带 (BLE) 的设备**?
2. ✅ 配对设备名称是否正确? (如 `AQ-V258007640` 而非 `AQ-V258007640(BLE)`)
3. ✅ 打印机是否开机且在蓝牙范围内?

**解决方法**:
```bash
# 1. 查看所有已配对设备
adb shell dumpsys bluetooth_manager | findstr "Name:"

# 2. 取消配对旧设备
手机设置 → 蓝牙 → 点击 AQ-V258007640(BLE) → 取消配对

# 3. 重新配对 Classic 版本
手机设置 → 蓝牙 → 可用设备 → AQ-V258007640 → 配对
```

### 问题: 仍然打印失败

**检查日志关键点**:
```bash
adb logcat -s PuQuPrinterManager:* | findstr "实际连接地址"
```

- 如果显示 `实际连接地址: CD:AC:B0:F0:31:FC` (以 F 开头)
  → 转换失败,检查配对状态
  
- 如果显示 `实际连接地址: CD:AC:B0:07:00:72` (以 0 开头)
  → 转换成功,检查打印机连接状态

## 总结

这个修复解决了 BLE 和 Classic 蓝牙地址不一致导致的打印失败问题,通过智能地址转换,确保应用始终使用正确的 Classic 地址连接 PUQU SDK。

**关键要点**:
- 🔍 扫描看到的是 BLE 设备 (带后缀)
- 📱 配对需要配对 Classic 设备 (无后缀)
- 🖨️ 打印使用 Classic 地址 (自动转换)
- ✅ 应用会自动处理转换,用户只需正确配对即可

---
**修复日期**: 2025-11-13  
**问题编号**: BLE-CLASSIC-ADDRESS-MISMATCH  
**影响**: 所有 AQ 系列蓝牙打印机  
