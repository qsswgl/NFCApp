# 🚀 NFC 读写系统 - 开发者快速参考

## 📍 项目位置
```
k:\NFC\NFCApp\
```

## 🎯 核心组件速查

### 主活动
```kotlin
// MainActivity.kt - 主界面逻辑
private fun handleNFCRead(tag: Tag)      // NFC 读取处理
private fun handleNFCWrite(tag: Tag)     // NFC 写入处理
private fun handlePrint()                // 打印处理
private fun handleUpload()               // 上传处理
```

### 数据库操作
```kotlin
// database/NFCRecordDao.kt
getAllRecords()                          // 获取所有记录
insert(record)                           // 插入新记录
update(record)                           // 更新记录
delete(record)                           // 删除记录
```

### NFC 功能
```kotlin
// nfc/NFCReader.kt
readTag(tag): Map<String, Any>          // 读取 NFC 标签

// nfc/NFCWriter.kt
writeTag(tag, data): Boolean            // 写入 NFC 标签
```

### 蓝牙打印
```kotlin
// print/BluetoothPrinter.kt
connect(deviceAddress)                   // 连接打印机
disconnect()                             // 断开连接
print(content)                           // 打印文本
getAvailableDevices()                    // 获取设备列表
```

## 📱 UI 组件速查

### 主界面布局
```xml
activity_main.xml
├── 标题区 (TextView)
├── NFC 信息区 (LinearLayout)
│   ├── tv_nfcid (显示 NFCID)
│   ├── et_card_number (卡号输入)
│   └── et_car_number (车号输入)
├── 按钮区 (LinearLayout)
│   ├── btn_write (写入)
│   ├── btn_read (读取)
│   ├── btn_print (打印)
│   └── btn_upload (上传)
└── RecyclerView (记录列表)
```

### 列表项布局
```xml
item_record.xml
├── tv_nfcid (NFCID)
├── tv_card_number (卡号)
├── tv_car_number (车号)
├── tv_time (时间)
└── tv_content (内容)
```

## 🗂️ 文件快速导航

| 功能 | 文件 | 行数 |
|------|------|------|
| NFC 读取 | `nfc/NFCReader.kt` | 50+ |
| NFC 写入 | `nfc/NFCWriter.kt` | 50+ |
| 数据库 | `database/` | 150+ |
| 主界面 | `MainActivity.kt` | 250+ |
| 打印功能 | `print/BluetoothPrinter.kt` | 80+ |
| 列表显示 | `ui/RecordAdapter.kt` | 60+ |
| 主布局 | `res/layout/activity_main.xml` | 100+ |
| 列表项 | `res/layout/item_record.xml` | 40+ |

## 🔧 常见操作

### 添加新的数据字段
1. 编辑 `database/NFCRecord.kt` - 添加属性
2. 增加数据库版本号
3. Room 会自动处理迁移

### 添加新的 UI 按钮
1. 在 `activity_main.xml` 添加 Button
2. 在 `MainActivity.kt` 的 `setupUIListeners()` 中添加监听
3. 实现对应的处理函数

### 集成新的外部库
1. 编辑 `app/build.gradle.kts`
2. 添加依赖到 dependencies 块
3. 运行 Gradle 同步

## 📦 关键依赖速查

```gradle
// 数据库
implementation "androidx.room:room-runtime:2.5.2"

// NFC
implementation "androidx.nfc:nfc:1.1.0"

// 列表视图
implementation "androidx.recyclerview:recyclerview:1.3.1"

// 协程
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1"

// 生命周期
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
```

## 🐛 调试技巧

### 查看日志
```kotlin
Log.d("TAG", "Message")    // 调试日志
Log.e("TAG", "Error")      // 错误日志
```

### 数据库查看
```bash
# 使用 Android Studio Device File Explorer
/data/data/com.nfc.app/databases/nfc_database
```

### NFC 测试
- 连接真实 NFC 设备（不支持模拟器）
- 使用标准 NDEF 格式标签
- 查看 LogCat 输出

## ⚡ 快速命令

### 构建
```bash
./gradlew build          # 完整构建
./gradlew assemble       # 生成 APK
./gradlew clean          # 清理
```

### 运行
```bash
./gradlew installDebug   # 安装到设备
./gradlew run           # 运行应用
```

### 测试
```bash
./gradlew test          # 运行单元测试
./gradlew connectedAndroidTest  # 运行集成测试
```

## 🔍 权限速查

### AndroidManifest.xml 中声明的权限
```xml
<uses-permission android:name="android.permission.NFC" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 📊 数据模型速查

```kotlin
// NFCRecord.kt - 数据库表对应的 Entity
data class NFCRecord(
    val id: Int,              // 主键
    val nfcId: String,        // NFC ID
    val cardNumber: String,   // 卡号
    val carNumber: String,    // 车号
    val readTime: Long,       // 时间戳
    val content: String,      // 内容
    val uploadStatus: Boolean // 上传状态
)
```

## 🎨 颜色和样式速查

```kotlin
// colors.xml
primary_color = #2196F3        // 蓝色
primary_dark_color = #1976D2   // 深蓝
accent_color = #FF4081         // 粉红
text_color = #333333           // 深灰
light_gray = #F5F5F5           // 浅灰
```

## 📖 文档导航

| 文档 | 用途 | 大小 |
|------|------|------|
| README.md | 完整说明 | 5KB+ |
| QUICKSTART.md | 快速开始 | 3KB+ |
| DEVELOPMENT.md | 开发指南 | 4KB+ |
| PROJECT_SUMMARY.md | 项目总结 | 3KB+ |
| FILE_INVENTORY.md | 文件清单 | 3KB+ |
| COMPLETION_REPORT.md | 完成报告 | 4KB+ |

## 🆘 常见问题快速解决

### 编译错误
```
❌ 错误：找不到 R.java
✅ 解决：运行 ./gradlew clean && ./gradlew build
```

### NFC 不工作
```
❌ 错误：设备不支持 NFC
✅ 解决：使用支持 NFC 的真实设备，不是模拟器
```

### 蓝牙连接失败
```
❌ 错误：无法连接蓝牙
✅ 解决：先在系统设置中配对设备，确保蓝牙权限已授予
```

### 数据库错误
```
❌ 错误：数据库损坏
✅ 解决：清除应用数据，重新启动应用
```

## 🎯 开发检查清单

在提交代码前：
- [ ] 代码编译无错误
- [ ] 所有方法都有注释
- [ ] 遵循 Kotlin 风格指南
- [ ] 使用有意义的变量名
- [ ] 没有硬编码值
- [ ] 异常处理完善
- [ ] 日志记录充分
- [ ] 代码无死代码

## 📞 快速求助

**找不到某个功能？**
→ 查看 FILE_INVENTORY.md

**不知道怎么用某个类？**
→ 查看类上方的注释

**想要了解项目架构？**
→ 查看 DEVELOPMENT.md

**需要快速开始？**
→ 查看 QUICKSTART.md

---

**打印此页作为快速参考！** 📋

最后更新：2025年11月2日
