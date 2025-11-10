# 打印功能测试指南

## 已修复的问题

### 1. ✅ APP崩溃问题
**原因**: Android 12+ 缺少运行时蓝牙权限
**解决**: 在打印前自动请求 `BLUETOOTH_CONNECT` 和 `BLUETOOTH_SCAN` 权限

### 2. ✅ 连接到错误设备（T50）
**原因**: 没有优先选择打印机设备
**解决**: 优先连接名称包含 "printer"、"print"、"打印"、"GP-"、"XP-" 等关键字的设备

### 3. ✅ 没有打印
**原因**: API方法不匹配
**解决**: 使用反射尝试多种打印方法（sendDataByte、writeBytes、sendBytes、printRawData）

## 测试步骤

### 第一次使用（需要授权）

1. **启动APP**
   - 打开 NFC 缴费APP

2. **读取NFC卡**
   - 将NFC卡放在手机背面
   - 等待读卡成功提示

3. **输入金额**
   - 在"消费金额"框输入金额（如：11）

4. **点击打印按钮**
   - 第一次点击会弹出权限请求
   - ⚠️ **必须授予"附近的设备"权限**（这是蓝牙扫描权限）
   - 点击"允许"

5. **再次点击打印**
   - APP会自动扫描已配对的蓝牙打印机
   - 自动连接到打印机
   - 开始打印小票

### 后续使用（已授权）

1. 读卡 → 输入金额 → 点击打印
2. 系统自动：扫描→连接→打印

## 查看详细日志

```powershell
# 实时监控打印日志
adb -s 192.168.1.152:45581 logcat -v time PuQuPrinterManager:D NFCApp:D AndroidRuntime:E *:S

# 或导出日志到文件
adb -s 192.168.1.152:45581 logcat -d > print_log.txt
```

## 日志关键信息

### ✅ 成功流程
```
D/PuQuPrinterManager: 已配对设备: Printer_AF35_BLE (C0:15:83:36:AF:35)
D/PuQuPrinterManager: 找到 1 个打印机设备
D/PuQuPrinterManager: 优先使用打印机: Printer_AF35_BLE
D/PuQuPrinterManager: 开始连接打印机: Printer_AF35_BLE
D/PuQuPrinterManager: ✓ 打印机已连接
D/PuQuPrinterManager: 打印数据准备完毕: 234 字节
D/PuQuPrinterManager: ✓ 使用 sendDataByte 方法发送成功
D/PuQuPrinterManager: ✓✓✓ 打印成功
```

### ❌ 权限问题
```
E/AndroidRuntime: java.lang.SecurityException: Need android.permission.BLUETOOTH_SCAN
```
**解决**: 授予"附近的设备"权限

### ⚠️ 找不到打印机
```
D/PuQuPrinterManager: 找到 0 个打印机设备
```
**解决**: 
1. 确保打印机已在系统蓝牙设置中配对
2. 打印机名称应包含"printer"或"print"

### ⚠️ API方法不匹配
```
W/PuQuPrinterManager: sendDataByte 方法不可用
❌ 所有打印方法都失败，列出可用方法:
  - method1(Type)
  - method2(Type)
```
**说明**: 需要根据日志中列出的方法名更新代码

## 打印机配对检查

### 查看已配对设备
```powershell
adb -s 192.168.1.152:45581 shell dumpsys bluetooth_manager | Select-String -Pattern "Bonded"
```

### 如果打印机未配对
1. 打开手机"设置" → "蓝牙"
2. 搜索附近设备
3. 找到打印机（Printer_AF35_BLE 或其他）
4. 点击配对
5. 返回APP测试

## 小票内容格式

```
     ===== 缴费小票 =====
     -------------------------

卡号: 04EC162DC12A81
车号: 4a469c13314
单位: 测试33
设备: 测试钩机
金额: 11 元

时间: 2025-11-10 17:54:57
     -------------------------
     谢谢惠顾!


```

## 常见问题

### Q1: 点击打印后APP立即关闭
**A**: Android 12+ 权限未授予，已修复。更新后首次使用会自动请求权限。

### Q2: 连接到错误的设备（耳机、手机等）
**A**: 已优先选择打印机设备。确保打印机名称包含"printer"关键字。

### Q3: 连接成功但没有打印
**A**: 
1. 查看日志中的"列出可用方法"
2. 检查打印机是否支持 ESC/POS 指令
3. 确认打印机电源和纸张

### Q4: 如何指定特定打印机
**A**: 目前自动选择第一个打印机设备。如需手动选择，可使用"选择打印机"按钮。

## 下一步优化建议

1. **记住上次使用的打印机**
2. **添加打印机列表选择界面**
3. **支持多台打印机切换**
4. **打印失败重试机制**
5. **打印队列管理**
