# BLE打印机SDK集成说明

## 更新内容 (2024-11-10)

本次更新成功集成了PUQU打印机官方SDK,实现了对AQ系列打印机的完整支持。

### 集成的SDK

- **SDK名称**: PUQU Print SDK v1.2.0
- **SDK位置**: `app/libs/puqu-print_v1.2.0.aar`
- **来源**: K:\NFC\sdk_puquprint\Android\

### 新增功能

1. **智能设备识别**
   - 自动检测打印机类型(AQ系列 vs 其他系列)
   - AQ设备自动使用SDK模式连接
   - 非AQ设备继续使用标准BLE连接

2. **SDK包装类**
   - 新增 `PuQuPrinterManager` 类封装SDK调用
   - 位置: `app/src/main/java/com/example/bleprinter/sdk/PuQuPrinterManager.kt`
   - 提供简化的打印API

3. **双模式支持**
   - **SDK模式**: 用于AQ系列打印机,支持图像打印
   - **标准模式**: 用于GP系列等标准ESC/POS打印机

### SDK API使用

#### 连接打印机
```kotlin
val sdkManager = PuQuPrinterManager(context)
sdkManager.setOnConnectSuccess { 
    // 连接成功回调
}
sdkManager.connect(deviceAddress)  // MAC地址
```

#### 打印测试页
```kotlin
sdkManager.printTestPage()  // 打印预设的测试页
```

#### 打印自定义内容
```kotlin
sdkManager.printSimpleText("Hello World")
```

#### 断开连接
```kotlin
sdkManager.disconnect()
```

### SDK高级功能

SDK提供了强大的布局和格式化能力:

```kotlin
// 开始打印任务
printManager.startJob(400, -1)  // 宽度400, 高度自动

// 设置样式
printManager.setLeft(16)         // 左边距
printManager.setFontSize(24f)    // 字体大小
printManager.setAlignment(1)     // 对齐方式: 0=左, 1=中, 2=右
printManager.setBold(true)       // 粗体

// 添加内容
printManager.addText("文本内容")
printManager.addBlank(16)        // 添加空行
printManager.addBarCode("12345678", 200, 100)  // 添加条形码

// 执行打印
printManager.printJob()
```

### 使用流程

1. **扫描设备**
   - 点击"开始扫描"按钮
   - 等待扫描到BLE打印机

2. **连接设备**
   - 点击扫描到的设备
   - 应用会自动识别设备类型
   - AQ设备显示"使用SDK模式连接"提示
   - 其他设备显示"使用标准模式连接"提示

3. **测试打印**
   - 连接成功后,"测试打印"按钮变为可用
   - 点击测试打印
   - AQ设备使用SDK打印测试页
   - 其他设备使用ESC/POS命令打印

### 技术细节

#### 为什么需要SDK?

AQ系列打印机使用**图像打印协议**,不支持标准的ESC/POS文本命令。PUQU官方SDK内部会:
1. 将文本转换为图像
2. 使用特定的图像打印协议发送到打印机
3. 处理打印机的特殊通信要求

#### 依赖配置

在 `app/build.gradle.kts` 中:
```kotlin
dependencies {
    // ... 其他依赖
    
    // PUQU Printer SDK
    implementation(files("libs/puqu-print_v1.2.0.aar"))
}
```

#### MainActivity关键修改

1. 新增SDK管理器实例
```kotlin
private lateinit var puquPrinterManager: PuQuPrinterManager
private var isUsingSDK = false  // 标记当前连接模式
```

2. 设备点击时智能判断
```kotlin
val isAQDevice = deviceName.startsWith("AQ", ignoreCase = true)
if (isAQDevice) {
    connectWithSDK(device)
} else {
    connectionManager.connect(device.device)
}
```

3. 打印时使用对应的API
```kotlin
if (isUsingSDK) {
    puquPrinterManager.printTestPage()  // SDK模式
} else {
    connectionManager.sendData(printData)  // 标准模式
}
```

### 测试建议

1. **GP系列打印机**
   - 应该继续正常工作
   - 使用标准BLE连接
   - 打印ESC/POS命令

2. **AQ系列打印机**
   - 自动切换到SDK模式
   - 连接成功后状态显示"已连接 (SDK模式 - 就绪)"
   - 打印测试页应该正常输出内容

### 已知问题和注意事项

1. SDK的打印API是同步调用,建议在协程中执行
2. 连接回调在SDK线程中,需要使用`runOnUiThread`更新UI
3. SDK打印内容是基于图像的,不支持直接发送ESC/POS命令
4. 打印质量和速度取决于图像转换质量

### 构建信息

- **构建工具**: Gradle 8.2
- **Kotlin版本**: 1.9.x
- **编译SDK**: 34 (Android 14)
- **最低SDK**: 26 (Android 8.0)
- **APK大小**: ~5.8MB (包含SDK)

### 下一步优化建议

1. 添加打印队列管理
2. 支持自定义打印模板
3. 添加打印历史记录
4. 支持图片打印
5. 添加打印预览功能
6. 优化打印速度和质量设置

### 参考文档

- SDK Demo: K:\NFC\sdk_puquprint\Android\PQprintDemo\
- SDK文档: K:\NFC\sdk_puquprint\Android\PUQUPrinter-SDK&API-Documents-Android.pdf

---

## 常见问题

**Q: AQ打印机连接成功但不打印?**
A: 确保使用了SDK模式连接。连接状态应显示"已连接 (SDK模式 - 就绪)"。

**Q: GP打印机突然不能打印了?**
A: GP打印机应该继续使用标准模式。检查连接状态是否显示"就绪 (可以打印)"而不是"SDK模式"。

**Q: 如何添加新的打印内容?**
A: 编辑 `PuQuPrinterManager.kt`,参考 `printTestPage()` 方法的实现,使用SDK的布局API创建自定义打印内容。

**Q: 可以同时连接多台打印机吗?**
A: 当前实现不支持。需要修改代码维护多个连接实例。

---

**最后更新**: 2024-11-10 17:46
**APK位置**: `app/build/outputs/apk/debug/app-debug.apk`
