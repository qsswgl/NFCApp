# NFC 读写系统 - 开发指南

## 项目概述

本项目是一个基于 Kotlin/Android 的 NFC 读写系统，提供了完整的 NFC 标签读写、数据存储、表格展示和蓝牙打印功能。

## 核心模块说明

### 1. MainActivity (主活动)

**职责**：
- 管理应用的主界面和用户交互
- 协调各个模块之间的通信
- 处理 NFC 事件

**关键方法**：
- `setupUIListeners()` - 设置 UI 事件监听器
- `handleNFCRead(tag)` - 处理 NFC 读取
- `handleNFCWrite(tag)` - 处理 NFC 写入
- `handlePrint()` - 处理打印
- `handleUpload()` - 处理上传
- `loadRecords()` - 从数据库加载记录

### 2. 数据库模块 (database/)

#### NFCRecord.kt
数据模型，定义了一条读写记录的结构：
```kotlin
@Entity(tableName = "nfc_records")
data class NFCRecord(
    val id: Int,              // 主键
    val nfcId: String,        // NFC标签ID
    val cardNumber: String,   // 卡号
    val carNumber: String,    // 车号
    val readTime: Long,       // 读写时间戳
    val content: String,      // 原始内容
    val uploadStatus: Boolean // 上传状态
)
```

#### NFCRecordDao.kt
数据访问对象，提供数据库操作接口：
- `getAllRecords()` - 获取所有记录
- `insert(record)` - 插入新记录
- `update(record)` - 更新记录
- `delete(record)` - 删除记录

#### NFCDatabase.kt
Room 数据库配置，使用单例模式确保全局只有一个数据库实例。

### 3. NFC 功能模块 (nfc/)

#### NFCReader.kt
NFC 标签读取功能：
- 支持 NDEF 格式标签读取
- 自动解析文本、MIME 数据等多种格式
- 返回标签内容和元数据

#### NFCWriter.kt
NFC 标签写入功能：
- 将键值对数据写入 NDEF 格式标签
- 支持标签自动格式化
- 验证标签容量

### 4. 打印模块 (print/)

#### BluetoothPrinter.kt
蓝牙打印机通信：
- 连接蓝牙设备
- 发送打印数据
- 管理连接生命周期
- 获取配对设备列表

### 5. UI 模块 (ui/)

#### RecordAdapter.kt
RecyclerView 适配器，显示读写记录列表：
- 使用 ListAdapter 和 DiffUtil 进行高效更新
- 格式化显示记录信息
- 支持动态加载和刷新

## 开发流程

### 添加新功能的步骤

1. **如果涉及数据持久化**：
   - 在 `database/` 模块扩展 NFCRecord 或创建新的 Entity
   - 添加相应的 DAO 方法

2. **如果涉及外部硬件**：
   - 在相应功能模块（`nfc/`, `print/` 等）中创建新类
   - 实现通信协议

3. **如果涉及 UI**：
   - 在 `res/layout/` 中创建或修改布局文件
   - 在 `ui/` 模块中创建相应的 Adapter 或 Fragment

4. **在 MainActivity 中集成**：
   - 初始化新的功能类
   - 添加事件监听器
   - 处理用户交互

### 代码规范

- **命名规则**：
  - 类名使用 PascalCase（如 `NFCReader`）
  - 方法名使用 camelCase（如 `handleNFCRead`）
  - 常量使用 UPPER_CASE
  - 私有成员使用下划线前缀（可选）

- **Kotlin 风格**：
  - 使用 data class 定义数据模型
  - 使用 suspend 函数处理异步操作
  - 充分利用 scope functions（let, apply, with 等）
  - 使用 coroutines 替代 callbacks

- **错误处理**：
  - 在 try-catch 中处理异常
  - 向用户显示 Toast 提示
  - 记录关键错误到日志

## 扩展指南

### 添加上传功能

需要实现：
1. 创建 `network/` 模块
2. 使用 Retrofit 或 OkHttp 进行 HTTP 请求
3. 在 `MainActivity.handleUpload()` 中调用
4. 上传成功后更新 `uploadStatus` 标记

### 扩展蓝牙打印功能

可以考虑：
1. 添加设备搜索和自动配对界面
2. 支持更多打印机型号
3. 添加打印模板配置
4. 支持网络打印机

### 数据分析和报表

可以添加：
1. 统计信息视图（每日读写数量等）
2. 图表显示（使用 MPAndroidChart 库）
3. 数据导出功能（CSV、PDF）
4. 高级查询和过滤

## 测试建议

### 单元测试
```kotlin
@Test
fun testNFCRead() {
    // 测试 NFCReader
}

@Test
fun testDatabaseOperations() {
    // 测试数据库增删改查
}
```

### 集成测试
```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    // 使用 Espresso 测试 UI
}
```

### 真机测试
- 使用支持 NFC 的 Android 设备
- 使用标准 NDEF 格式的 NFC 标签
- 配对不同型号的蓝牙打印机

## 性能优化建议

1. **数据库操作**：
   - 使用分页加载大量数据
   - 添加数据库索引（NFCID, 卡号等）
   - 定期清理过期记录

2. **UI 优化**：
   - RecyclerView 使用 ViewHolder 缓存
   - 避免在主线程执行耗时操作
   - 使用协程处理异步任务

3. **内存优化**：
   - 及时释放蓝牙连接
   - 使用 WeakReference 避免内存泄漏
   - 监控 Bitmap 使用

## 常见问题

**Q: 如何测试 NFC 功能？**
A: 需要使用支持 NFC 的真实 Android 设备，模拟器不支持 NFC。

**Q: 如何添加新的数据字段？**
A: 修改 NFCRecord Entity，增加迁移版本号，Room 会自动处理数据库升级。

**Q: 如何处理蓝牙权限？**
A: 应用需要请求运行时权限，已在 AndroidManifest.xml 中声明。

## 资源和参考

- [Android NFC 官方文档](https://developer.android.com/guide/topics/connectivity/nfc)
- [Room 数据库指南](https://developer.android.com/training/data-storage/room)
- [蓝牙 API 参考](https://developer.android.com/guide/topics/connectivity/bluetooth)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## 版本历史

| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0 | 2025-11-02 | 初始版本，包含 NFC 读写、数据库、打印基础功能 |

---

**最后更新**：2025年11月2日
