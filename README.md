# NFC 读写系统 - Android 应用

这是一个基于 Kotlin 开发的 Android 应用，用于实现 NFC 标签的读写、数据存储、表格显示和蓝牙打印功能。

## 功能特性

### 1. NFC 读写功能
- **读取 NFC 标签**：靠近 NFC 标签时自动读取其内容
- **写入 NFC 标签**：将卡号和车号等信息写入 NFC 标签
- 支持 NDEF 格式的标签读写
- 自动处理标签格式化

### 2. 数据存储
- 使用 SQLite 数据库（通过 Room ORM）存储所有读写记录
- 存储内容：NFCID、卡号、车号、读写时间戳、原始内容
- 支持数据的增删改查操作

### 3. 界面展示
- 主界面显示 NFCID、卡号、车号 输入框
- 四个功能按钮：写入、读取、打印、上传
- 使用 RecyclerView 以表格形式展示所有读写历史记录
- 记录包含时间、NFCID、卡号、车号等信息

### 4. 蓝牙打印
- 连接蓝牙打印机
- 将表格内容格式化后发送到打印机
- 支持标准蓝牙 SPP (Serial Port Profile)

### 5. 数据上传（待实现）
- 将数据库中的记录上传到服务器
- 需配置服务器 API 端点

## 项目结构

```
NFCApp/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/nfc/app/
│   │   │   ├── MainActivity.kt              # 主活动
│   │   │   ├── database/
│   │   │   │   ├── NFCRecord.kt             # 数据模型
│   │   │   │   ├── NFCRecordDao.kt          # 数据访问对象
│   │   │   │   └── NFCDatabase.kt           # 数据库配置
│   │   │   ├── nfc/
│   │   │   │   ├── NFCReader.kt             # NFC 读取功能
│   │   │   │   └── NFCWriter.kt             # NFC 写入功能
│   │   │   ├── print/
│   │   │   │   └── BluetoothPrinter.kt      # 蓝牙打印功能
│   │   │   └── ui/
│   │   │       └── RecordAdapter.kt         # RecyclerView 适配器
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml        # 主界面布局
│   │   │   │   └── item_record.xml          # 记录项布局
│   │   │   ├── values/
│   │   │   │   ├── strings.xml              # 字符串资源
│   │   │   │   ├── colors.xml               # 颜色资源
│   │   │   │   └── styles.xml               # 样式资源
│   │   │   └── drawable/
│   │   │       └── ic_*.xml                 # 图标资源
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                     # App 级构建配置
│   └── proguard-rules.pro
├── build.gradle.kts                         # 项目级构建配置
├── settings.gradle.kts
└── gradle.properties
```

## 开发环境要求

- Android Studio 2022.3.1 或更高版本
- Kotlin 1.8.0 或更高版本
- Android SDK 33 (API 33) 或更高版本
- 最小支持版本：Android 5.0 (API 21)

## 构建和运行

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd NFCApp
   ```

2. **打开项目**
   - 使用 Android Studio 打开项目

3. **配置本地开发环境**
   - 编辑 `local.properties` 文件，设置 Android SDK 路径：
     ```properties
     sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\sdk
     ```

4. **构建应用**
   ```bash
   ./gradlew build
   ```

5. **运行应用**
   - 连接 Android 设备或启动 Android 模拟器
   - 在 Android Studio 中点击"Run"按钮

## 使用说明

### 读取 NFC 标签
1. 点击"读取"按钮
2. 将设备靠近 NFC 标签
3. 读取内容会自动保存到数据库
4. 记录会显示在表格中

### 写入 NFC 标签
1. 输入卡号和车号
2. 点击"写入"按钮
3. 将设备靠近 NFC 标签
4. 数据会写入标签并保存到数据库

### 打印记录
1. 先连接蓝牙打印机
2. 点击"打印"按钮
3. 表格内容会发送到打印机

### 上传数据
1. 点击"上传"按钮
2. 数据会发送到配置的服务器

## 权限说明

应用需要以下权限：
- `NFC` - 访问 NFC 功能
- `BLUETOOTH` - 蓝牙操作
- `BLUETOOTH_ADMIN` - 蓝牙管理
- `BLUETOOTH_CONNECT` - 蓝牙连接（Android 12+）
- `READ/WRITE_EXTERNAL_STORAGE` - 文件访问

## 依赖库

- **AndroidX Core KTX** - Kotlin 扩展库
- **AndroidX AppCompat** - 应用兼容性库
- **Material Design** - Material 设计库
- **Room** - SQLite 对象关系映射
- **RecyclerView** - 列表显示
- **Lifecycle** - 生命周期管理
- **Coroutines** - 异步编程

## 待完成功能

1. **上传功能**
   - 实现 API 客户端
   - 配置服务器端点
   - 添加上传进度显示

2. **蓝牙配对**
   - UI 界面选择打印机
   - 自动蓝牙搜索和配对

3. **数据导出**
   - 支持导出为 CSV/Excel
   - 导出为 PDF 报表

4. **高级功能**
   - NFC 标签加密
   - 读写统计分析
   - 数据备份和恢复

## 故障排除

### 无法读写 NFC 标签
- 检查设备是否支持 NFC
- 确保 NFC 功能已启用
- 验证应用是否拥有 NFC 权限
- 尝试更换兼容的 NFC 标签

### 蓝牙打印机无法连接
- 确保打印机已开启且可见
- 检查蓝牙权限
- 尝试先在系统设置中手动配对

### 数据库错误
- 清除应用数据并重新启动
- 检查存储空间是否充足

## 许可证

本项目采用 MIT 许可证

## 作者

NFC 读写系统开发团队

## 联系方式

如有问题或建议，请联系开发团队。

---

**最后更新**：2025年11月2日
