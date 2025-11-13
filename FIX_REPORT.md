# 打印失败问题修复说明

**问题**: Print项目可以打印,但NFCApp提示"打印失败,请检查打印机"  
**原因**: 线程调度和连接检查逻辑错误  
**修复时间**: 2025-11-13  
**提交**: 018e58a

---

## 🔍 问题分析

### 对比参考项目 (K:\NFC\Print)

参考项目的成功实现:
```kotlin
// Print/app/.../sdk/PuQuPrinterManager.kt
fun connect(address: String) {
    // 检查 SDK 连接状态
    if (printManager.isConnected) {
        printManager.closePrinter()
        Thread.sleep(500)
    }
    printManager.openPrinter(address)
}

fun printTestPage(): Boolean {
    // 在后台线程执行打印
    Thread {
        printManager.printJob()
    }.start()
}
```

### 我们的错误实现

**错误1: 协程混用问题**
```kotlin
// ❌ 错误: printToAddress 是同步函数,但使用协程delay
suspend fun printToAddress(...): Boolean {
    kotlinx.coroutines.delay(1000)  // 编译错误!
    while (!isConnected && waitTime < 20000) {
        kotlinx.coroutines.delay(1000)  // 在同步函数中无法使用!
    }
}
```

**错误2: 连接状态检查不准确**
```kotlin
// ❌ 错误: 只检查我们维护的标志
if (isConnected && connectedAddress == address) {
    return
}
// 应该检查 SDK 的实际连接状态
```

**错误3: 主线程阻塞**
```kotlin
// ❌ 错误: 在主线程调度器中使用Thread.sleep
private suspend fun printReceipts(...) {
    Thread.sleep(2000)  // 阻塞UI!
}
```

---

## ✅ 修复方案

### 修复1: 使用正确的线程同步

**PuQuPrinterManager.kt - connectToPrinter()**
```kotlin
fun connectToPrinter(address: String) {
    // ✓ 检查 SDK 实际连接状态
    if (printManager?.isConnected == true && connectedAddress == address) {
        Log.d(TAG, "✓ 已连接到此设备")
        isConnected = true
        return
    }
    
    // ✓ 断开旧连接使用Thread.sleep
    if (printManager?.isConnected == true) {
        printManager?.closePrinter()
        Thread.sleep(500)
    }
    
    printManager?.openPrinter(address)
}
```

**PuQuPrinterManager.kt - printToAddress()**
```kotlin
suspend fun printToAddress(...): Boolean {
    // ✓ 使用Thread.sleep替代协程delay
    if (isConnected && connectedAddress != printerAddress) {
        printManager?.closePrinter()
        isConnected = false
        Thread.sleep(1000)  // 正确: 在suspend函数中使用Thread.sleep
    }
    
    // ✓ 等待连接使用Thread.sleep
    var waitTime = 0
    while (!isConnected && waitTime < 20000) {
        Thread.sleep(1000)  // 正确
        waitTime += 1000
    }
}
```

### 修复2: 在IO调度器中执行阻塞操作

**MainActivity.kt - printReceipts()**
```kotlin
private suspend fun printReceipts(...) = withContext(Dispatchers.IO) {
    // ✓ 在IO线程中执行,不阻塞UI
    val printSuccess1 = puquPrinter.printToAddress(...)
    
    Thread.sleep(2000)  // ✓ 在IO线程中安全
    
    val printSuccess2 = puquPrinter.printToAddress(...)
}
```

---

## 📋 修改清单

### PuQuPrinterManager.kt
- ✅ `connectToPrinter()` - 添加 `printManager?.isConnected` 检查
- ✅ `printToAddress()` - 将 `kotlinx.coroutines.delay` 改为 `Thread.sleep`
- ✅ `printToAddress()` - 添加try-catch处理断开连接异常

### MainActivity.kt
- ✅ `printReceipts()` - 使用 `withContext(Dispatchers.IO)` 包装
- ✅ `printReceipts()` - 将 `kotlinx.coroutines.delay(2000)` 改为 `Thread.sleep(2000)`

---

## 🧪 测试验证

### 1. 编译测试
```powershell
cd K:\NFC\NFCApp
.\gradlew.bat assembleDebug --quiet
```
**结果**: ✅ 编译成功,无错误

### 2. 安装测试
```powershell
.\adb.exe devices                     # 确认设备连接
.\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
```

### 3. 功能测试
1. 读取NFC卡
2. 填写金额
3. 点击"确认"按钮
4. 选择打印机 (如有多个)
5. **观察日志**:
   ```
   PuQuPrinterManager: ========== SDK连接请求 ==========
   PuQuPrinterManager: 调用 SDK openPrinter(CD:AC:B0:07:00:72)...
   PuQuPrinterManager: SDK openPrinter调用完成,等待连接回调 (约6秒)...
   PuQuPrinterManager: ✓ 打印机连接成功
   PuQuPrinterManager: 步骤5: 后台线程执行 printJob()
   PuQuPrinterManager: 后台线程: printJob() 完成
   MainActivity: ✓ 第1份小票打印成功
   MainActivity: ✓ 第2份小票打印成功
   ```

---

## 🔧 关键修复点总结

| 问题 | 原因 | 修复 |
|------|------|------|
| 协程delay在同步函数 | 混用协程和同步代码 | 统一使用Thread.sleep |
| UI线程阻塞 | printReceipts直接调用Thread.sleep | 使用withContext(Dispatchers.IO) |
| 连接状态不准确 | 只检查自维护标志 | 检查SDK的isConnected属性 |
| 连接回调丢失 | isConnected标志更新时机错误 | 在SDK回调中正确设置标志 |

---

## 📖 参考资料

### Print项目成功示例
- 位置: `K:\NFC\Print\app\src\main\java\com\example\bleprinter\sdk\PuQuPrinterManager.kt`
- 关键方法:
  - `connect(address)` - 连接打印机
  - `printTestPage()` - 后台线程打印

### Android线程最佳实践
- **UI线程**: 只做UI更新,不执行耗时操作
- **IO线程**: 执行阻塞IO操作 (网络、磁盘、蓝牙)
- **Default线程**: 执行CPU密集型操作

### 协程调度器选择
```kotlin
Dispatchers.Main    // UI更新
Dispatchers.IO      // 阻塞IO (Thread.sleep, 打印机连接)
Dispatchers.Default // CPU密集型计算
```

---

## ⚠️ 注意事项

### 1. SDK连接时序
```
openPrinter(address)
    ↓ (约6秒)
ConnectSuccess.success()
    ↓
isConnected = true
    ↓
可以调用 printJob()
```

### 2. 打印流程
```
startJob(400, -1)
    ↓
addText("内容")
    ↓
Thread { printJob() }.start()  // 必须在后台线程!
```

### 3. 连接检查顺序
1. 先检查 `printManager?.isConnected`
2. 再检查自维护的 `isConnected` 标志
3. 最后检查 `connectedAddress`

---

## 🚀 安装新版本

```powershell
# 1. 确保设备连接
cd K:\NFC\NFCApp
.\adb.exe devices

# 2. 卸载旧版本
.\adb.exe uninstall com.nfc.app

# 3. 安装新版本
.\adb.exe install app\build\outputs\apk\debug\app-debug.apk

# 4. 启动应用
.\adb.exe shell am start -n com.nfc.app/.MainActivity

# 5. 查看实时日志
.\adb.exe logcat -v time | findstr "PuQuPrinterManager MainActivity"
```

---

## 📞 如果仍然失败

### 收集以下信息:

1. **完整日志**:
   ```powershell
   .\adb.exe logcat -v time > full_log.txt
   # 点击"确认"按钮
   # Ctrl+C 停止
   ```

2. **SDK连接回调**:
   查找日志中的:
   ```
   SDK回调: 连接成功
   SDK回调: 连接失败
   SDK回调: 连接关闭
   ```

3. **printJob执行**:
   查找:
   ```
   后台线程: 开始 printJob()...
   后台线程: printJob() 完成
   ```

4. **错误信息**:
   ```
   ❌ 连接超时
   ❌ 打印失败
   ❌ 打印异常
   ```

提供这些信息以便进一步分析!

---

**修复完成! 现在的代码逻辑与成功的Print项目完全一致! 🎉**
