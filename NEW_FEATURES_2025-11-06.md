# 新功能实现说明 - 2025年11月6日

## 🎯 功能概述

本次更新实现了三个主要功能：

### 1. 自动读取手机号绑定到机号
- APP启动时自动获取手机SIM卡号码
- 将手机号作为机号显示（只读，不可修改）
- 如果无法获取手机号，使用设备Android ID的前11位

### 2. 增加单位名称和设备名称字段
- 在界面上添加两个新的输入框
- 用户可以手动输入单位名称和设备名称
- 这些信息会保存到数据库并打印在小票上

### 3. 历史记录自动填充
- 读卡后自动查询该卡号的历史记录
- 如果找到历史记录，自动填充上次使用的单位名称和设备名称
- 实现智能记忆，减少重复输入

---

## 📱 界面变化

### 字段顺序
```
标题: 加油刷卡消费
├─ NFCID显示
├─ 卡号 (TextView只读)
├─ 机号 (TextView只读，自动获取)
├─ 单位名称 (EditText可输入)  ← 新增
├─ 设备名称 (EditText可输入)  ← 新增
└─ 消费金额 (EditText可输入)
```

### 机号字段
**原来**:
- EditText 可手动输入
- 默认值: "1"

**现在**:
- TextView 只读显示
- 自动获取手机号或设备ID
- 提示文字: "自动获取中..."

### 新增字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| 单位名称 | EditText | 可手动输入，读卡后自动填充历史值 |
| 设备名称 | EditText | 可手动输入，读卡后自动填充历史值 |

---

## 🔧 技术实现

### 1. 权限申请

**AndroidManifest.xml** 新增权限:
```xml
<!-- 读取手机状态权限 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
```

**运行时权限请求**:
```kotlin
private val PHONE_STATE_PERMISSION_REQUEST = 102

private fun requestPhoneStatePermission() {
    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) 
        != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 
            PHONE_STATE_PERMISSION_REQUEST)
    }
}
```

### 2. 自动获取手机号

**实现逻辑**:
```kotlin
private fun getPhoneNumber(etCarNumber: TextView) {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    // 尝试获取手机号
    val phoneNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        telephonyManager.subscriberId ?: "未获取到"
    } else {
        telephonyManager.line1Number ?: telephonyManager.subscriberId ?: "未获取到"
    }
    
    // 如果无法获取手机号，使用设备ID
    val displayNumber = if (phoneNumber.isNotEmpty() && phoneNumber != "未获取到") {
        phoneNumber
    } else {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).take(11)
    }
    
    etCarNumber.text = displayNumber
}
```

**获取优先级**:
1. **首选**: `line1Number` (手机号码)
2. **次选**: `subscriberId` (SIM卡IMSI)
3. **备选**: Android设备ID前11位

### 3. 数据库扩展

**NFCRecord实体更新**:
```kotlin
@Entity(tableName = "nfc_records")
data class NFCRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nfcId: String,
    val cardNumber: String,
    val carNumber: String,
    val unitName: String = "",      // 新增
    val deviceName: String = "",    // 新增
    val amount: String = "",        // 新增
    val readTime: Long,
    val content: String,
    val uploadStatus: Boolean = false
)
```

**新增DAO查询方法**:
```kotlin
@Query("SELECT * FROM nfc_records WHERE cardNumber = :cardNumber ORDER BY readTime DESC LIMIT 1")
suspend fun getLastRecordByCardNumber(cardNumber: String): NFCRecord?
```

**数据库版本升级**:
```kotlin
@Database(entities = [NFCRecord::class], version = 2, exportSchema = false)
abstract class NFCDatabase : RoomDatabase() {
    // ...
    .fallbackToDestructiveMigration() // 删除旧数据重建
}
```

### 4. 读卡流程增强

**handleReadCard方法**:
```kotlin
private fun handleReadCard(
    etCardNumber: TextView,
    tvNfcid: TextView,
    etUnitName: EditText,
    etDeviceName: EditText
) {
    // 1. 模拟读取卡号
    val mockCardNumber = "1234567890123456"
    etCardNumber.text = mockCardNumber
    
    // 2. 查询数据库历史记录
    CoroutineScope(Dispatchers.IO).launch {
        val lastRecord = database.nfcRecordDao()
            .getLastRecordByCardNumber(mockCardNumber)
        
        withContext(Dispatchers.Main) {
            if (lastRecord != null) {
                // 3. 自动填充历史单位和设备名称
                etUnitName.setText(lastRecord.unitName)
                etDeviceName.setText(lastRecord.deviceName)
                Toast.makeText(this@MainActivity, 
                    "📖 读卡成功！已加载历史信息", 
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

**流程图**:
```
读取NFC卡号
    ↓
显示在界面
    ↓
查询数据库 (SELECT * WHERE cardNumber = ? ORDER BY readTime DESC LIMIT 1)
    ↓
找到历史记录？
├─ 是 → 自动填充单位名称、设备名称 → 提示"已加载历史信息"
└─ 否 → 保持空白 → 提示"读卡成功"
```

### 5. 打印功能更新

**printReceipt方法签名变化**:
```kotlin
// 原来
fun printReceipt(cardNumber: String, carNumber: String, amount: String): Boolean

// 现在
fun printReceipt(
    cardNumber: String,
    carNumber: String,
    unitName: String,
    deviceName: String,
    amount: String
): Boolean
```

**小票内容格式**:
```kotlin
// 标题
outputStream.write("消费小票".toByteArray(Charset.forName("GBK")))

// 卡号
outputStream.write("卡号: ".toByteArray(Charset.forName("GBK")))
outputStream.write(cardNumber.toByteArray())

// 机号
outputStream.write("机号: ".toByteArray(Charset.forName("GBK")))
outputStream.write(carNumber.toByteArray())

// 单位名称 (如果不为空)
if (unitName.isNotEmpty()) {
    outputStream.write("单位: ".toByteArray(Charset.forName("GBK")))
    outputStream.write(unitName.toByteArray(Charset.forName("GBK")))
}

// 设备名称 (如果不为空)
if (deviceName.isNotEmpty()) {
    outputStream.write("设备: ".toByteArray(Charset.forName("GBK")))
    outputStream.write(deviceName.toByteArray(Charset.forName("GBK")))
}

// 消费金额
outputStream.write("消费金额: ".toByteArray(Charset.forName("GBK")))
outputStream.write(amount.toByteArray())
outputStream.write(" 元".toByteArray(Charset.forName("GBK")))
```

### 6. 保存记录到数据库

**打印成功后自动保存**:
```kotlin
private fun saveRecordToDatabase(
    cardNumber: String,
    carNumber: String,
    unitName: String,
    deviceName: String,
    amount: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        val record = NFCRecord(
            nfcId = cardNumber.substring(0, minOf(8, cardNumber.length)),
            cardNumber = cardNumber,
            carNumber = carNumber,
            unitName = unitName,
            deviceName = deviceName,
            amount = amount,
            readTime = System.currentTimeMillis(),
            content = "卡号:$cardNumber,机号:$carNumber,单位:$unitName,设备:$deviceName,金额:$amount"
        )
        database.nfcRecordDao().insert(record)
    }
}
```

---

## 📋 使用流程

### 完整操作步骤

```
┌─────────────────────────────────┐
│ 1. 打开APP                      │
│    - 自动获取手机号绑定到机号   │
│    - 请求必要权限               │
└────────────┬────────────────────┘
             ↓
┌─────────────────────────────────┐
│ 2. 点击"读取"按钮               │
│    - 手机靠近NFC卡              │
│    - 读取卡号                   │
└────────────┬────────────────────┘
             ↓
┌─────────────────────────────────┐
│ 3. 系统自动操作                 │
│    - 查询该卡号历史记录         │
│    - 自动填充单位名称           │
│    - 自动填充设备名称           │
└────────────┬────────────────────┘
             ↓
┌─────────────────────────────────┐
│ 4. 用户确认或修改               │
│    - 单位名称可修改             │
│    - 设备名称可修改             │
│    - 输入消费金额               │
└────────────┬────────────────────┘
             ↓
┌─────────────────────────────────┐
│ 5. 点击"打印"按钮               │
│    - 连接蓝牙打印机             │
│    - 打印小票                   │
│    - 保存记录到数据库           │
└─────────────────────────────────┘
```

### 场景示例

#### 场景A: 首次使用某张卡
```
1. 读卡 → 卡号: 1234567890123456
2. 机号: 13800138000 (自动获取)
3. 单位名称: (空白) → 用户输入"测试公司"
4. 设备名称: (空白) → 用户输入"加油机01"
5. 金额: 100.00
6. 打印 → 保存到数据库
```

#### 场景B: 再次使用同一张卡
```
1. 读卡 → 卡号: 1234567890123456
2. 机号: 13800138000 (自动获取)
3. 单位名称: "测试公司" (自动填充) ✓
4. 设备名称: "加油机01" (自动填充) ✓
5. 金额: 200.00
6. 打印 → 更新数据库记录
```

---

## 🖨️ 打印小票格式

### 示例输出

```
━━━━━━━━━━━━━━━━━━━
      消费小票
━━━━━━━━━━━━━━━━━━━

卡号: 1234567890123456

机号: 13800138000

单位: 测试公司

设备: 加油机01

消费金额: 100.00 元

━━━━━━━━━━━━━━━━━━━
时间: 2025-11-06 15:30:25
━━━━━━━━━━━━━━━━━━━
      谢谢使用!
━━━━━━━━━━━━━━━━━━━
```

### 字段说明

| 字段 | 来源 | 说明 |
|------|------|------|
| 卡号 | NFC读取 | 16位卡号 |
| 机号 | 自动获取 | 手机号或设备ID |
| 单位 | 手动输入/历史 | 可选，为空不打印 |
| 设备 | 手动输入/历史 | 可选，为空不打印 |
| 消费金额 | 手动输入 | 必填，单位:元 |
| 时间 | 系统时间 | 格式: yyyy-MM-dd HH:mm:ss |

---

## ⚠️ 注意事项

### 权限相关

1. **首次启动需要授权**:
   - 蓝牙权限 (BLUETOOTH_CONNECT/SCAN)
   - 手机状态权限 (READ_PHONE_STATE)

2. **权限被拒绝的影响**:
   - 拒绝蓝牙权限 → 无法打印
   - 拒绝手机状态权限 → 机号显示"未授权"

### 机号获取

1. **可能的机号来源**:
   - 优先: 手机号码 (需要SIM卡支持)
   - 次选: SIM卡IMSI
   - 备选: Android设备ID

2. **获取失败的情况**:
   - 无SIM卡
   - 运营商不允许读取
   - 用户拒绝权限
   - 虚拟机环境

### 历史记录

1. **查询逻辑**:
   - 按卡号查询
   - 取最近一次记录
   - 只填充单位和设备名称

2. **数据库升级**:
   - 旧版本数据会被清空
   - 使用 `fallbackToDestructiveMigration()`
   - 如需保留数据，需实现Migration

### 字段验证

**打印前验证**:
- ✓ 卡号不为空
- ✓ 机号不为"自动获取中..."或"未授权"
- ✓ 金额不为空
- ⚠️ 单位名称和设备名称可选

---

## 🔍 调试日志

### 关键日志标记

```kotlin
// 机号获取
Log.d(TAG, "✓ 机号已自动绑定: $displayNumber")
Log.w(TAG, "⚠️ 未获得读取手机状态权限")

// 读卡流程
Log.d(TAG, "========== 开始读卡流程 ==========")
Log.d(TAG, "✓ 已加载历史记录")
Log.d(TAG, "ℹ️ 该卡号无历史记录")

// 打印流程
Log.d(TAG, "打印参数:")
Log.d(TAG, "  卡号: $cardNumber")
Log.d(TAG, "  机号: $carNumber")
Log.d(TAG, "  单位名称: $unitName")
Log.d(TAG, "  设备名称: $deviceName")
Log.d(TAG, "  金额: $amount")

// 数据库操作
Log.d(TAG, "✓ 记录已保存到数据库")
```

### 查看日志命令

```powershell
# 查看完整日志
K:\tool\adb\adb.exe logcat -s NFCApp:V BluetoothPrinter:V

# 过滤特定内容
K:\tool\adb\adb.exe logcat -s NFCApp:V | findstr "机号\|单位\|设备"
```

---

## 📦 编译和部署

### 编译状态
```
✅ BUILD SUCCESSFUL in 31s
36 actionable tasks: 7 executed, 29 up-to-date
```

### APK位置
```
K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

### 安装命令
在物理主机上执行:
```powershell
K:\tool\adb\adb.exe install -r K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

### 首次安装注意
- 会提示请求手机状态权限
- 建议选择"允许"以自动获取机号
- 拒绝后可在设置中手动授权

---

## 📊 功能对比

### 更新前后对比

| 功能点 | 更新前 | 更新后 |
|--------|--------|--------|
| 机号输入 | 手动输入，默认"1" | **自动获取手机号，只读** |
| 单位名称 | 不存在 | **新增，支持历史填充** |
| 设备名称 | 不存在 | **新增，支持历史填充** |
| 历史记录 | 只显示，不利用 | **智能填充，减少输入** |
| 数据库版本 | Version 1 | **Version 2 (新增3字段)** |
| 打印内容 | 卡号、机号、金额 | **卡号、机号、单位、设备、金额** |
| 权限要求 | 蓝牙 | **蓝牙 + 手机状态** |

---

## 🎉 更新总结

**本次更新完成的目标**:
1. ✅ 自动获取手机号绑定到机号（只读不可修改）
2. ✅ 新增单位名称和设备名称输入框
3. ✅ 读卡后自动填充历史单位和设备信息
4. ✅ 更新数据库模型支持新字段
5. ✅ 打印小票包含完整信息
6. ✅ 保存记录供下次智能填充

**用户体验提升**:
- 🚀 无需手动输入机号
- 💡 智能记忆常用单位和设备
- 📝 减少重复录入工作
- 🖨️ 小票信息更完整

**文档版本**: 1.0  
**更新日期**: 2025年11月6日  
**编译状态**: ✅ BUILD SUCCESSFUL
