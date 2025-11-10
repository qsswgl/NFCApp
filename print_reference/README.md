# BLE蓝牙打印机应用

这是一个使用Kotlin开发的Android BLE蓝牙打印机应用，支持扫描、连接和测试打印功能。

## 功能特性

- ✅ **BLE设备扫描**: 自动扫描附近的BLE蓝牙打印机设备
- ✅ **设备连接**: 支持连接到BLE打印机并维持稳定连接
- ✅ **测试打印**: 内置测试打印功能，支持ESC/POS打印命令
- ✅ **权限管理**: 自动处理Android 12+的蓝牙权限请求
- ✅ **实时状态**: 显示连接状态和扫描进度

## 技术栈

- **语言**: Kotlin
- **最低SDK**: Android 8.0 (API 26)
- **目标SDK**: Android 14 (API 34)
- **架构**: MVVM + Flow
- **UI**: Material Design + ViewBinding

## 项目结构

```
app/
├── src/main/
│   ├── java/com/example/bleprinter/
│   │   ├── MainActivity.kt              # 主界面
│   │   ├── adapter/
│   │   │   └── DeviceAdapter.kt         # 设备列表适配器
│   │   ├── bluetooth/
│   │   │   ├── BluetoothScanner.kt      # BLE扫描管理
│   │   │   └── BluetoothConnectionManager.kt  # BLE连接管理
│   │   ├── model/
│   │   │   └── BleDevice.kt             # 设备数据模型
│   │   └── printer/
│   │       └── PrinterCommands.kt       # ESC/POS打印命令
│   ├── res/
│   │   └── layout/
│   │       ├── activity_main.xml        # 主界面布局
│   │       └── item_device.xml          # 设备列表项布局
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 核心功能说明

### 1. BLE扫描 (BluetoothScanner)
- 支持Android 12+新权限模型
- 使用StateFlow实现响应式数据流
- 自动过滤未命名设备
- 实时更新信号强度(RSSI)

### 2. 设备连接 (BluetoothConnectionManager)
- GATT协议连接
- 自动服务发现
- 支持多种UUID(打印机服务/串口服务)
- 连接状态管理(断开/连接中/已连接/就绪)

### 3. 打印命令 (PrinterCommands)
支持的ESC/POS命令:
- 文本打印
- 字体大小设置(正常/双倍)
- 对齐方式(左/中/右)
- 粗体和下划线
- 换行控制
- 切纸
- 二维码打印
- 条形码打印
- 测试打印模板

### 4. 权限处理
自动请求必要的蓝牙权限:
- Android 12+: BLUETOOTH_SCAN, BLUETOOTH_CONNECT
- Android 11-: BLUETOOTH, ACCESS_FINE_LOCATION

## 使用方法

### 编译运行

1. 使用Android Studio打开项目
2. 连接Android设备或启动模拟器
3. 点击运行按钮

### 使用流程

1. **授予权限**: 首次运行时授予蓝牙和位置权限
2. **启用蓝牙**: 如果蓝牙未启用，应用会提示启用
3. **扫描设备**: 点击"开始扫描"按钮扫描附近的BLE设备
4. **连接打印机**: 从列表中选择打印机设备并确认连接
5. **测试打印**: 连接成功后，点击"测试打印"按钮打印测试页

## 支持的打印机

理论上支持所有基于ESC/POS协议的BLE打印机，包括:
- 热敏小票打印机
- 标签打印机
- 便携式蓝牙打印机

## 注意事项

1. **UUID配置**: 不同品牌打印机可能使用不同的服务UUID，需要在`BluetoothConnectionManager`中调整
2. **字符编码**: 默认使用GBK编码支持中文，部分打印机可能需要调整
3. **命令兼容性**: ESC/POS命令在不同打印机上可能有细微差异
4. **权限要求**: 
   - Android 12+需要BLUETOOTH_SCAN和BLUETOOTH_CONNECT
   - Android 11-需要ACCESS_FINE_LOCATION
5. **蓝牙版本**: 仅支持BLE(蓝牙4.0+)设备

## 自定义打印

可以使用`PrinterCommands`类创建自定义打印内容:

```kotlin
val printData = PrinterCommands.initPrinter() +
    PrinterCommands.alignCenter() +
    PrinterCommands.fontSizeDouble() +
    PrinterCommands.printLine("自定义标题") +
    PrinterCommands.fontSizeNormal() +
    PrinterCommands.alignLeft() +
    PrinterCommands.printLine("内容行1") +
    PrinterCommands.printLine("内容行2") +
    PrinterCommands.cutPaper()

connectionManager.sendData(printData)
```

## 故障排除

### 扫描不到设备
- 确保打印机已开启并处于配对模式
- 检查是否授予了位置权限
- 确认蓝牙已启用

### 连接失败
- 打印机可能已与其他设备连接
- 尝试重启打印机
- 检查打印机是否支持BLE

### 打印无反应
- 确认连接状态为"就绪"
- 检查打印机纸张
- 尝试调整UUID配置

## 许可证

MIT License

## 作者

GitHub Copilot
