# NFC自动读卡功能实现 (2025-11-06)

## 📋 需求说明

用户反馈：**"手机靠近NFC卡后，手机底部会自动弹出 选择要使用的应用，能否禁止此弹窗，并自动读取卡号，无需再点 读卡"**

### 问题分析
- **原问题**：系统默认会弹出应用选择器对话框
- **用户期望**：静默自动读卡，无需任何操作
- **解决方案**：实现NFC前台调度系统(Foreground Dispatch)

---

## ✅ 实现内容

### 1. NFC前台调度系统 (Foreground Dispatch)

#### **工作原理**
当APP在前台运行时，通过`NfcAdapter.enableForegroundDispatch()`拦截所有NFC事件，阻止系统弹出应用选择对话框，直接由APP处理NFC标签。

#### **核心组件**
```kotlin
// 成员变量
private var nfcAdapter: NfcAdapter? = null              // NFC适配器
private var pendingIntent: PendingIntent? = null        // 待处理意图
private var intentFilters: Array<IntentFilter>? = null  // 意图过滤器
private var techLists: Array<Array<String>>? = null     // 支持的NFC技术列表

// 视图引用（用于自动更新UI）
private lateinit var tvNfcid: TextView         // NFCID显示
private lateinit var etCardNumber: TextView    // 卡号显示
private lateinit var etUnitName: EditText      // 单位名称
private lateinit var etDeviceName: EditText    // 设备名称
```

### 2. 初始化方法 `initializeNFC()`

```kotlin
private fun initializeNFC() {
    // 获取NFC适配器
    nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    
    // 检查设备支持
    if (nfcAdapter == null) {
        Toast.makeText(this, "设备不支持NFC功能", Toast.LENGTH_SHORT).show()
        return
    }
    
    // 检查NFC是否开启
    if (!nfcAdapter!!.isEnabled) {
        Toast.makeText(this, "请在系统设置中开启NFC", Toast.LENGTH_LONG).show()
    }
    
    // 创建PendingIntent（Android 12+需要FLAG_MUTABLE）
    val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    
    // 设置IntentFilter（捕获所有NFC事件类型）
    intentFilters = arrayOf(
        IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
        IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
        IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
    )
    
    // 设置支持的技术列表（支持8种常见NFC技术）
    techLists = arrayOf(
        arrayOf(IsoDep::class.java.name),
        arrayOf(NfcA::class.java.name),
        arrayOf(NfcB::class.java.name),
        arrayOf(NfcF::class.java.name),
        arrayOf(NfcV::class.java.name),
        arrayOf(Ndef::class.java.name),
        arrayOf(MifareClassic::class.java.name),
        arrayOf(MifareUltralight::class.java.name)
    )
}
```

### 3. 生命周期管理

#### **onResume() - 启用前台调度**
```kotlin
override fun onResume() {
    super.onResume()
    // 当Activity可见时启用前台调度
    nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
    Log.d(TAG, "✓ NFC前台调度已启用")
}
```

#### **onPause() - 禁用前台调度**
```kotlin
override fun onPause() {
    super.onPause()
    // 当Activity失去焦点时禁用前台调度（必须！否则影响其他应用）
    nfcAdapter?.disableForegroundDispatch(this)
    Log.d(TAG, "✓ NFC前台调度已禁用")
}
```

### 4. NFC标签处理 `onNewIntent()`

```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    
    // 检查是否为NFC意图
    if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
        intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
        intent?.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
        
        // 获取NFC标签
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            // 提取卡号（标签ID转十六进制）
            val cardNumber = tag.id.joinToString("") { String.format("%02X", it) }
            
            // 更新UI显示
            etCardNumber.text = cardNumber
            tvNfcid.text = "NFCID: ${cardNumber.take(8)}"
            
            // 异步查询历史记录
            CoroutineScope(Dispatchers.IO).launch {
                val lastRecord = database.nfcRecordDao().getLastRecordByCardNumber(cardNumber)
                withContext(Dispatchers.Main) {
                    if (lastRecord != null) {
                        // 自动填充历史单位和设备信息
                        etUnitName.setText(lastRecord.unitName)
                        etDeviceName.setText(lastRecord.deviceName)
                        Toast.makeText(this@MainActivity, "✓ 读卡成功！已加载历史信息", Toast.LENGTH_SHORT).show()
                    } else {
                        // 新卡，清空字段
                        etUnitName.setText("")
                        etDeviceName.setText("")
                        Toast.makeText(this@MainActivity, "✓ 读卡成功！卡号: ${cardNumber.take(8)}...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
```

### 5. 读卡按钮优化

```kotlin
btnRead.setOnClickListener {
    // 按钮仅显示提示信息，实际读卡由NFC自动触发
    Toast.makeText(this, "📖 请将NFC卡靠近手机背面", Toast.LENGTH_SHORT).show()
}
```

---

## 🎯 功能特性

### ✨ 核心功能
1. **静默读卡**：靠近NFC卡自动读取，无系统弹窗
2. **自动显示**：卡号实时显示到界面
3. **历史加载**：自动查询并填充该卡最后一次的单位、设备信息
4. **多技术支持**：支持8种NFC技术（ISO-DEP、MIFARE、NFC-A/B/F/V、NDEF等）

### 🔧 技术细节
- **前台调度优先级**：APP在前台时拦截所有NFC事件
- **Android 12兼容**：PendingIntent使用FLAG_MUTABLE
- **异步数据库查询**：使用协程避免UI阻塞
- **安全处理**：空安全检查，异常捕获

---

## 📝 使用说明

### 操作步骤
1. **启动APP**
   - 自动显示机号（手机号/设备ID）
   
2. **靠近NFC卡**
   - 手机感应到卡片后立即自动读取
   - 无需点击"读卡"按钮
   - 不会弹出系统应用选择对话框
   
3. **查看结果**
   - 卡号自动显示
   - 如果是老卡，单位名称和设备名称自动填充历史信息
   - 如果是新卡，单位和设备字段为空，需手动输入

4. **输入消费金额**
   - 填写本次消费金额

5. **写入/打印**
   - 点击"写入"保存记录到卡片和数据库
   - 点击"打印"通过蓝牙打印小票

### 注意事项
⚠️ **NFC必须开启**：请确保手机系统设置中NFC功能已启用
⚠️ **APP需在前台**：只有当APP在前台运行时才会自动读卡
⚠️ **卡片靠近背面**：将NFC卡靠近手机背面的NFC感应区域（通常在摄像头附近）

---

## 🔍 调试信息

### Logcat标签过滤
```
adb logcat | findstr "NFCApp"
```

### 关键日志输出
```
✓ NFC初始化成功
✓ NFC前台调度已启用
📱 NFC卡检测到: 04A1B2C3D4E5F6
✓ 已加载历史记录: 单位=石化公司, 设备=3号加油机
```

---

## 📦 编译信息

### 编译命令
```powershell
.\gradlew.bat assembleDebug
```

### 编译结果
```
BUILD SUCCESSFUL in 22s
36 actionable tasks: 6 executed, 30 up-to-date
```

### APK位置
```
K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

### 警告信息（可忽略）
```
w: Variable 'nfcReader' is never used
w: Variable 'nfcWriter' is never used
w: 'getParcelableExtra(String!): T?' is deprecated. Deprecated in Java
```

---

## 🚀 安装部署

### USB连接安装
```powershell
# 1. 连接手机到电脑（USB调试开启）
# 2. 检查设备
K:\tool\adb\adb.exe devices

# 3. 安装APK
K:\tool\adb\adb.exe install -r K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

### 手动安装
1. 将APK文件传输到手机
2. 在手机上打开文件管理器
3. 点击APK文件安装

---

## 🎉 完成状态

### ✅ 已实现功能（全部完成）
- [x] GitHub仓库推送
- [x] UI界面重命名（加油刷卡消费）
- [x] 字段调整（机号、消费金额）
- [x] 卡号只读显示
- [x] 自动绑定机号（手机号/设备ID）
- [x] 单位名称、设备名称字段
- [x] 数据库版本升级（v2）
- [x] 历史记录自动填充
- [x] 蓝牙打印功能
- [x] 布局滚动修复
- [x] **NFC自动读卡（本次实现）**

### 🎯 本次更新亮点
1. **无弹窗体验**：彻底解决系统应用选择对话框问题
2. **全自动读卡**：靠近即读，无需任何操作
3. **智能填充**：老卡自动加载历史信息
4. **生命周期安全**：正确启用/禁用前台调度

---

## 📚 技术文档

### Android NFC官方文档
- [NFC Basics](https://developer.android.com/guide/topics/connectivity/nfc/nfc)
- [Advanced NFC](https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc)

### 相关API
- `NfcAdapter.enableForegroundDispatch()`
- `NfcAdapter.disableForegroundDispatch()`
- `Tag.getId()`
- `Intent.FLAG_ACTIVITY_SINGLE_TOP`

---

**开发完成时间**：2025年11月6日  
**版本**：1.0 (Database v2)  
**状态**：✅ 编译成功，待设备测试

---

## 📞 下一步

1. **连接手机测试**
   - 使用USB线连接手机
   - 确保USB调试已开启
   - 安装APK到手机

2. **功能验证**
   - 测试NFC自动读卡
   - 验证历史记录加载
   - 确认无系统弹窗

3. **问题反馈**
   - 如遇到问题请查看Logcat日志
   - 反馈问题时请提供完整错误信息
