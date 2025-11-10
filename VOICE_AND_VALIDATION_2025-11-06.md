# 语音播报和数据验证功能 (2025-11-06)

## 📋 更新需求

1. ✅ 读取NFC卡后，若为新卡（无刷卡记录），语音播报："新卡，请新录入 单位名称 设备名称"
2. ✅ 若有刷卡记录，获取最后一次记录的单位名称、设备名称并绑定到控件上
3. ✅ 显示该卡的所有刷卡记录
4. ✅ 语音播报单位名称和设备名称
5. ✅ 点确认按钮时验证必须输入单位名称、设备名称、消费金额
6. ✅ 验证失败时语音提示

---

## ✨ 实现内容

### 1. Android TextToSpeech集成

#### 导入和初始化

**文件**: `MainActivity.kt`

**导入TTS包**:
```kotlin
import android.speech.tts.TextToSpeech
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech
    private var ttsReady = false
```

**onCreate()中初始化**:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // 初始化语音播报
    textToSpeech = TextToSpeech(this, this)
    
    // ... 其他初始化代码
}
```

**TTS初始化回调**:
```kotlin
override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
        val result = textToSpeech.setLanguage(Locale.CHINA)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(TAG, "❌ TTS不支持中文")
            ttsReady = false
        } else {
            Log.d(TAG, "✓ TTS初始化成功")
            ttsReady = true
        }
    } else {
        Log.e(TAG, "❌ TTS初始化失败")
        ttsReady = false
    }
}
```

**释放资源**:
```kotlin
override fun onDestroy() {
    super.onDestroy()
    
    // 释放语音播报资源
    if (::textToSpeech.isInitialized) {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
    
    // 断开蓝牙连接
    bluetoothPrinter.disconnect()
}
```

---

### 2. 语音播报方法

#### speak() - 统一语音播报接口

```kotlin
/**
 * 语音播报
 * @param text 要播报的文字内容
 */
private fun speak(text: String) {
    if (ttsReady) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        Log.d(TAG, "🔊 语音播报: $text")
    } else {
        Log.w(TAG, "⚠️ TTS未就绪，无法播报: $text")
    }
}
```

**参数说明**:
- `text`: 播报内容（支持中文）
- `QUEUE_FLUSH`: 清空队列立即播报（不排队）
- 自动检查TTS是否就绪

---

### 3. NFC读卡逻辑增强

#### onNewIntent() - 读卡后的处理

**完整流程**:

```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    
    if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED || ...) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            val cardNumber = tag.id.joinToString("") { String.format("%02X", it) }
            
            // 更新UI显示卡号
            etCardNumber.text = cardNumber
            tvNfcid.text = "NFCID: ${cardNumber.take(8)}"
            
            // 查询数据库
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 1. 获取最后一次记录
                    val lastRecord = database.nfcRecordDao().getLastRecordByCardNumber(cardNumber)
                    
                    // 2. 获取该卡的所有历史记录
                    val allRecords = database.nfcRecordDao().getRecordsByCardNumber(cardNumber)
                    
                    withContext(Dispatchers.Main) {
                        if (lastRecord != null) {
                            // ===== 老卡处理 =====
                            
                            // 自动填充单位名称和设备名称
                            etUnitName.setText(lastRecord.unitName)
                            etDeviceName.setText(lastRecord.deviceName)
                            
                            // 显示该卡的历史记录
                            recordAdapter.submitList(allRecords)
                            
                            // 语音播报单位名称和设备名称
                            val speakText = "${lastRecord.unitName}，${lastRecord.deviceName}"
                            speak(speakText)
                            
                            Toast.makeText(this@MainActivity, "✓ 读卡成功！已加载历史信息", Toast.LENGTH_SHORT).show()
                        } else {
                            // ===== 新卡处理 =====
                            
                            // 清空字段
                            etUnitName.setText("")
                            etDeviceName.setText("")
                            
                            // 显示空列表
                            recordAdapter.submitList(emptyList())
                            
                            // 语音播报：新卡提示
                            speak("新卡，请新录入单位名称，设备名称")
                            
                            Toast.makeText(this@MainActivity, "✓ 读卡成功！新卡，请录入信息", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "查询历史记录失败", e)
                }
            }
        }
    }
}
```

**关键改进**:
1. 同时查询最后记录和所有记录
2. 老卡显示该卡的所有历史记录（不是全部记录）
3. 新卡显示空列表
4. 根据情况播报不同语音内容

---

### 4. 数据库查询增强

#### NFCRecordDao - 新增查询方法

**文件**: `NFCRecordDao.kt`

**新增方法**:
```kotlin
@Query("SELECT * FROM nfc_records WHERE cardNumber = :cardNumber ORDER BY readTime DESC")
suspend fun getRecordsByCardNumber(cardNumber: String): List<NFCRecord>
```

**已有方法**:
```kotlin
@Query("SELECT * FROM nfc_records WHERE cardNumber = :cardNumber ORDER BY readTime DESC LIMIT 1")
suspend fun getLastRecordByCardNumber(cardNumber: String): NFCRecord?
```

**区别**:
- `getLastRecordByCardNumber`: 获取该卡的最后一次记录（用于填充字段）
- `getRecordsByCardNumber`: 获取该卡的所有记录（用于显示历史）

---

### 5. 确认按钮验证增强

#### handleConfirm() - 数据验证

**完整验证流程**:

```kotlin
private fun handleConfirm(...) {
    val cardNumber = etCardNumber.text.toString().trim()
    val carNumber = etCarNumber.text.toString().trim()
    val unitName = etUnitName.text.toString().trim()
    val deviceName = etDeviceName.text.toString().trim()
    val amount = etAmount.text.toString().trim()
    
    // 1. 验证卡号
    if (cardNumber.isEmpty() || cardNumber == "请先读卡") {
        speak("请先读卡")
        Toast.makeText(this, "⚠️ 请先读卡", Toast.LENGTH_SHORT).show()
        return
    }
    
    // 2. 验证机号
    if (carNumber.isEmpty() || carNumber == "自动获取中...") {
        speak("机号正在获取中，请稍候")
        Toast.makeText(this, "⚠️ 机号正在获取中，请稍候", Toast.LENGTH_SHORT).show()
        return
    }
    
    // 3. 验证单位名称（新增）
    if (unitName.isEmpty()) {
        speak("请输入单位名称")
        Toast.makeText(this, "⚠️ 请输入单位名称", Toast.LENGTH_SHORT).show()
        etUnitName.requestFocus()  // 焦点定位到该输入框
        return
    }
    
    // 4. 验证设备名称（新增）
    if (deviceName.isEmpty()) {
        speak("请输入设备名称")
        Toast.makeText(this, "⚠️ 请输入设备名称", Toast.LENGTH_SHORT).show()
        etDeviceName.requestFocus()  // 焦点定位到该输入框
        return
    }
    
    // 5. 验证消费金额
    if (amount.isEmpty()) {
        speak("请输入消费金额")
        Toast.makeText(this, "⚠️ 请输入消费金额", Toast.LENGTH_SHORT).show()
        return
    }
    
    // 验证通过，保存数据
    // ...
}
```

**验证增强点**:
1. 每个验证失败都有语音播报
2. 单位名称和设备名称变为必填项
3. 验证失败时自动定位焦点到对应输入框
4. 语音和Toast双重提示

---

## 🎯 用户操作流程

### 场景1：新卡刷卡

```
1. 靠近NFC卡
   ↓
2. 自动读取卡号
   ↓
3. 查询数据库 → 无记录
   ↓
4. 🔊 语音播报："新卡，请新录入单位名称，设备名称"
   ↓
5. Toast显示："✓ 读卡成功！新卡，请录入信息"
   ↓
6. 界面显示：
   - 卡号：已填充
   - 机号：已填充
   - 单位名称：空（需要输入）
   - 设备名称：空（需要输入）
   - 刷卡记录：空列表
   ↓
7. 用户输入单位名称、设备名称、消费金额
   ↓
8. 点击确认 ✔️
   ↓
9. 验证通过 → 保存成功
```

### 场景2：老卡刷卡

```
1. 靠近NFC卡
   ↓
2. 自动读取卡号
   ↓
3. 查询数据库 → 有记录
   ↓
4. 自动填充：
   - 单位名称：石化公司
   - 设备名称：3号加油机
   ↓
5. 🔊 语音播报："石化公司，3号加油机"
   ↓
6. Toast显示："✓ 读卡成功！已加载历史信息"
   ↓
7. 界面显示：
   - 卡号：已填充
   - 机号：已填充
   - 单位名称：石化公司（自动填充）
   - 设备名称：3号加油机（自动填充）
   - 刷卡记录：显示该卡的所有历史记录
   ↓
8. 用户只需输入消费金额
   ↓
9. 点击确认 ✔️
   ↓
10. 验证通过 → 保存成功
```

### 场景3：确认时验证失败

```
用户点击确认按钮
   ↓
验证单位名称 → 为空？
   ↓ 是
🔊 "请输入单位名称"
⚠️ Toast: "请输入单位名称"
焦点定位到单位名称输入框
停止，等待输入
   ↓ 否
验证设备名称 → 为空？
   ↓ 是
🔊 "请输入设备名称"
⚠️ Toast: "请输入设备名称"
焦点定位到设备名称输入框
停止，等待输入
   ↓ 否
验证消费金额 → 为空？
   ↓ 是
🔊 "请输入消费金额"
⚠️ Toast: "请输入消费金额"
停止，等待输入
   ↓ 否
✓ 验证通过，保存数据
```

---

## 🔊 语音播报场景汇总

| 场景 | 触发时机 | 播报内容 |
|------|---------|---------|
| 新卡读取 | NFC读卡完成，数据库无记录 | "新卡，请新录入单位名称，设备名称" |
| 老卡读取 | NFC读卡完成，数据库有记录 | "{单位名称}，{设备名称}" |
| 确认验证失败 - 未读卡 | 点击确认时卡号为空 | "请先读卡" |
| 确认验证失败 - 机号未获取 | 点击确认时机号为空 | "机号正在获取中，请稍候" |
| 确认验证失败 - 无单位 | 点击确认时单位名称为空 | "请输入单位名称" |
| 确认验证失败 - 无设备 | 点击确认时设备名称为空 | "请输入设备名称" |
| 确认验证失败 - 无金额 | 点击确认时消费金额为空 | "请输入消费金额" |

---

## 🔧 技术实现细节

### TextToSpeech工作原理

```
初始化TTS
   ↓
TextToSpeech(context, OnInitListener)
   ↓
回调 onInit(status)
   ↓
检查状态 → SUCCESS?
   ↓ 是
设置语言 → setLanguage(Locale.CHINA)
   ↓
检查语言支持 → 支持中文?
   ↓ 是
ttsReady = true
   ↓
可以调用 speak()
```

### speak()方法调用链

```
speak(text)
   ↓
检查 ttsReady?
   ↓ 是
textToSpeech.speak(
    text,                    // 播报内容
    QUEUE_FLUSH,             // 清空队列
    null,                    // Bundle参数
    null                     // utteranceId
)
   ↓
Android系统TTS引擎
   ↓
手机扬声器输出语音
```

### 数据查询优化

**原逻辑**:
```kotlin
// 只查询最后一次记录
val lastRecord = database.nfcRecordDao().getLastRecordByCardNumber(cardNumber)
// 显示所有记录（包括其他卡）
loadRecords()  // getAllRecords()
```

**新逻辑**:
```kotlin
// 同时查询最后记录和所有记录
val lastRecord = database.nfcRecordDao().getLastRecordByCardNumber(cardNumber)
val allRecords = database.nfcRecordDao().getRecordsByCardNumber(cardNumber)

// 只显示该卡的记录
recordAdapter.submitList(allRecords)
```

**优势**:
- 用户体验更好：只看到当前卡的历史
- 查询更精确：不会被其他卡的记录干扰
- 性能更优：数据量更少

---

## 📊 界面交互增强

### 焦点管理

**自动聚焦到错误字段**:
```kotlin
if (unitName.isEmpty()) {
    speak("请输入单位名称")
    Toast.makeText(this, "⚠️ 请输入单位名称", Toast.LENGTH_SHORT).show()
    etUnitName.requestFocus()  // 自动定位到该输入框
    return
}
```

**用户体验提升**:
- 语音提示告知问题
- Toast视觉提示
- 焦点自动定位，方便立即输入

### 记录列表动态更新

**新卡**:
```kotlin
recordAdapter.submitList(emptyList())  // 显示空列表
```

**老卡**:
```kotlin
recordAdapter.submitList(allRecords)  // 显示该卡的所有历史
```

**确认后**:
```kotlin
loadRecords()  // 刷新显示（可选：仍显示该卡记录）
```

---

## 📦 文件修改清单

### 1. MainActivity.kt
- ✅ 添加TextToSpeech导入和接口实现
- ✅ 添加textToSpeech成员变量
- ✅ onCreate()中初始化TTS
- ✅ 实现onInit()回调
- ✅ 实现speak()方法
- ✅ onDestroy()中释放TTS资源
- ✅ 修改onNewIntent()增加语音播报和按卡号查询
- ✅ 修改handleConfirm()增加单位、设备验证和语音提示

### 2. NFCRecordDao.kt
- ✅ 添加getRecordsByCardNumber()查询方法

---

## ✅ 编译状态

### 编译信息
```
BUILD SUCCESSFUL in 32s
36 actionable tasks: 7 executed, 29 up-to-date
```

### 警告信息（可忽略）
```
w: Variable 'nfcReader' is never used
w: Variable 'nfcWriter' is never used
w: 'getParcelableExtra(String!): T?' is deprecated
```

### APK位置
```
K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

---

## 🧪 测试建议

### 功能测试清单

#### 1. 语音播报测试
- [ ] TTS初始化成功
- [ ] 新卡读取时播报"新卡，请新录入单位名称，设备名称"
- [ ] 老卡读取时播报单位名称和设备名称
- [ ] 确认验证失败时播报对应提示
- [ ] 音量适中，发音清晰

#### 2. 新卡流程测试
- [ ] 读取新卡，字段清空
- [ ] 播报新卡提示
- [ ] 记录列表显示为空
- [ ] 必须输入单位、设备、金额才能确认
- [ ] 确认后保存成功

#### 3. 老卡流程测试
- [ ] 读取老卡，自动填充单位和设备
- [ ] 播报单位名称和设备名称
- [ ] 记录列表只显示该卡的历史记录
- [ ] 可修改单位、设备信息
- [ ] 输入金额后确认保存

#### 4. 验证测试
- [ ] 未读卡点确认，语音提示"请先读卡"
- [ ] 未输入单位点确认，语音提示"请输入单位名称"，焦点定位
- [ ] 未输入设备点确认，语音提示"请输入设备名称"，焦点定位
- [ ] 未输入金额点确认，语音提示"请输入消费金额"
- [ ] 所有字段填写完整，确认成功

#### 5. 记录列表测试
- [ ] 新卡显示空列表
- [ ] 老卡只显示该卡的记录（不包括其他卡）
- [ ] 记录按时间倒序排列
- [ ] 确认后列表自动刷新

---

## 🎉 功能亮点

### 1. 智能语音提示
- **即时反馈**: 读卡、验证失败立即语音播报
- **清晰指引**: 语音提示下一步操作
- **解放双手**: 无需盯着屏幕即可获取信息

### 2. 差异化处理
- **新卡**: 清空字段、语音提示录入、显示空列表
- **老卡**: 自动填充、语音播报信息、显示历史记录
- **智能识别**: 自动判断卡类型并采取不同策略

### 3. 严格验证
- **必填项**: 单位名称、设备名称、消费金额都必填
- **双重提示**: 语音+Toast
- **焦点定位**: 自动聚焦到错误字段

### 4. 精准查询
- **按卡筛选**: 只显示当前卡的历史记录
- **信息聚焦**: 避免其他卡记录干扰
- **操作高效**: 快速查看该卡消费历史

---

## 🚀 下一步

### 安装部署
```powershell
# 连接设备
K:\tool\adb\adb.exe devices

# 安装APK
K:\tool\adb\adb.exe install -r K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk
```

### 测试重点
1. **语音播报**: 音量、清晰度、时机
2. **新卡流程**: 提示、验证、保存
3. **老卡流程**: 自动填充、历史显示
4. **数据验证**: 必填项拦截、焦点管理

### 可能的优化方向
- 支持语音播报速度调节
- 支持播报语言切换（普通话/粤语/英语）
- 支持语音识别输入单位和设备名称
- 记录列表支持删除和编辑
- 导出该卡的消费报表

---

## 📚 相关文档

- **UI更新**: `UI_UPDATE_2025-11-06.md`
- **NFC自动读卡**: `NFC_AUTO_READ_2025-11-06.md`
- **历史功能**: `NEW_FEATURES_2025-11-06.md`

---

**开发完成时间**: 2025年11月6日  
**版本**: 1.2 (Database v2 + TTS)  
**状态**: ✅ 编译成功，待设备测试

---

## 💡 使用提示

### 语音播报注意事项
1. **音量设置**: 确保手机媒体音量已开启
2. **TTS引擎**: 如果无语音，请检查系统TTS设置
3. **中文支持**: 部分设备可能需要下载中文语音包
4. **环境噪音**: 嘈杂环境建议同时查看屏幕提示

### 操作建议
1. **新卡**: 第一次使用时仔细输入单位和设备信息
2. **老卡**: 检查自动填充的信息是否正确，如有变化及时修改
3. **历史查看**: 刷卡后即可查看该卡的消费历史
4. **批量操作**: 相同单位设备的多张卡，第二张起会自动填充

### 故障排查
- **无语音播报**: 检查系统TTS设置和媒体音量
- **播报不清晰**: 调整TTS引擎或下载高质量语音包
- **验证总是失败**: 确保已读卡且所有字段已填写
- **历史记录不显示**: 检查数据库是否有该卡的记录
