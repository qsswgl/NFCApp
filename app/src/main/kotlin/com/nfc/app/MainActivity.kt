package com.nfc.app

import android.Manifest
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nfc.app.database.NFCDatabase
import com.nfc.app.database.NFCRecord
import com.nfc.app.nfc.NFCReader
import com.nfc.app.nfc.NFCWriter
import com.nfc.app.print.BluetoothPrinter
// BLE printing removed to restore classic Bluetooth only
import com.nfc.app.ui.RecordAdapter
import android.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val TAG = "NFCApp"
    private lateinit var recordAdapter: RecordAdapter
    private lateinit var bluetoothPrinter: BluetoothPrinter
    private lateinit var puquPrinter: PuQuPrinterManager // PUQU 厂家打印机 SDK
    private lateinit var database: NFCDatabase
    private lateinit var textToSpeech: TextToSpeech
    private var ttsReady = false
    private val BLUETOOTH_PERMISSION_REQUEST = 101
    private val BLUETOOTH_SCAN_PERMISSION_REQUEST = 104  // 新增：蓝牙扫描权限
    private val PHONE_STATE_PERMISSION_REQUEST = 102
    private val LOCATION_PERMISSION_REQUEST = 103
    
    // NFC相关
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null
    
    // 视图引用
    private lateinit var tvNfcid: TextView
    private lateinit var etCardNumber: TextView
    private lateinit var etUnitName: EditText
    private lateinit var etDeviceName: EditText
    private lateinit var tvFuelDate: TextView
    
    // 存储完整的卡号和机号（用于保存和打印）
    private var fullCardNumber: String = ""
    private var fullCarNumber: String = ""
    
    // 加油日期
    private var fuelDate: String = ""
    
    // OkHttp客户端
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    
    // 辅助函数：格式化显示号码（只显示后4位）
    private fun formatDisplayNumber(number: String): String {
        return if (number.length > 4) {
            number.takeLast(4)
        } else {
            number
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "=== onCreate started ===")
        
        try {
            // 初始化语音播报
            textToSpeech = TextToSpeech(this, this)
            
            // 使用LayoutInflater加载布局，避免R类问题
            val layoutResId = resources.getIdentifier("activity_main", "layout", packageName)
            Log.d(TAG, "Layout resource ID: $layoutResId")
            
            if (layoutResId == 0) {
                Log.e(TAG, "Cannot find activity_main layout")
                createFallbackLayout()
                return
            }
            
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(layoutResId, null)
            setContentView(view)
            
            Log.d(TAG, "Layout inflated successfully")
            
            // 初始化组件
            initializeComponents()
            
            // 初始化NFC适配器
            initializeNFC()
            
            Log.d(TAG, "=== onCreate completed successfully ===")
            Toast.makeText(this, "✓ NFC应用启动成功", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "!!! FATAL ERROR !!!", e)
            e.printStackTrace()
            Toast.makeText(this, "启动失败: ${e.message}", Toast.LENGTH_LONG).show()
            createFallbackLayout()
        }
    }
    
    private fun initializeComponents() {
        try {
            // 初始化数据库
            database = NFCDatabase.getDatabase(this)
            
            // 初始化NFC
            Log.d(TAG, "Initializing NFC components")
            val nfcReader = NFCReader()
            val nfcWriter = NFCWriter()
            
            // 初始化蓝牙打印机
            bluetoothPrinter = BluetoothPrinter(this)
            
            // 初始化 PUQU 打印器（厂家SDK，自动扫描连接）
            // 注意: 需要先授予 BLUETOOTH_CONNECT 权限才能初始化
            try {
                puquPrinter = PuQuPrinterManager(this)
                puquPrinter.initialize()
                puquPrinter.setCallback(object : PuQuPrinterManager.PrinterCallback {
                    override fun onConnecting(printerName: String) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "正在连接: $printerName", Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    override fun onConnected(printerName: String) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "✓ 已连接: $printerName", Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    override fun onDisconnected() {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "打印机已断开", Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    override fun onPrintStart() {
                        runOnUiThread {
                        Toast.makeText(this@MainActivity, "🖨️ 开始打印...", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onPrintSuccess() {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "✓ 打印成功！", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onPrintFailed(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "❌ 打印失败: $error", Toast.LENGTH_LONG).show()
                    }
                }
            })
                Log.d(TAG, "PUQU 打印器初始化完成")
            } catch (e: SecurityException) {
                // 权限不足时不初始化,等待用户点击打印按钮时再请求权限并初始化
                Log.w(TAG, "⚠️ 蓝牙权限不足,跳过PUQU打印器初始化 (将在打印时请求权限)")
                // 不抛出异常,让APP继续启动
            }
            
            // 获取视图并保存为成员变量
            tvNfcid = findViewById<TextView>(getResId("tv_nfcid", "id"))
            etCardNumber = findViewById<TextView>(getResId("et_card_number", "id"))
            val etCarNumber = findViewById<TextView>(getResId("et_car_number", "id"))
            etUnitName = findViewById<EditText>(getResId("et_unit_name", "id"))
            etDeviceName = findViewById<EditText>(getResId("et_device_name", "id"))
            val etAmount = findViewById<EditText>(getResId("et_amount", "id"))
            tvFuelDate = findViewById<TextView>(getResId("tv_fuel_date", "id"))
            val btnWrite = findViewById<LinearLayout>(getResId("btn_write", "id"))
            val btnRead = findViewById<LinearLayout>(getResId("btn_read", "id"))
            val btnConfirm = findViewById<LinearLayout>(getResId("btn_confirm", "id"))
            val btnPrint = findViewById<LinearLayout>(getResId("btn_print", "id"))
            val btnUpload = findViewById<LinearLayout>(getResId("btn_upload", "id"))
            // 临时测试打印按钮（布局中已添加）
            val btnTestPrint = findViewById<LinearLayout?>(getResId("btn_test_print", "id"))
            val btnSelectPrinter = findViewById<LinearLayout?>(getResId("btn_select_printer", "id"))
            val recyclerView = findViewById<RecyclerView>(getResId("recycler_view_records", "id"))
            
            // 初始化日期为今天
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            fuelDate = dateFormat.format(Calendar.getInstance().time)
            tvFuelDate.text = fuelDate
            
            // 设置日期选择器点击事件
            tvFuelDate.setOnClickListener {
                showDatePickerDialog()
            }
            
            // 先尝试获取手机号（即使没权限也会用设备ID）
            getPhoneNumber(etCarNumber)
            
            // 设置RecyclerView
            recordAdapter = RecordAdapter()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = recordAdapter
            
            // 加载历史记录
            loadRecords()
            
            // 设置按钮点击监听
            btnWrite.setOnClickListener {
                Toast.makeText(this, "✏️ 写入模式：请靠近NFC标签", Toast.LENGTH_SHORT).show()
            }
            
            btnRead.setOnClickListener {
                // 手动读卡按钮保留，但NFC靠近时会自动触发
                Toast.makeText(this, "📖 请将NFC卡靠近手机背面", Toast.LENGTH_SHORT).show()
            }
            
            // 确认按钮：保存数据到数据库
            btnConfirm.setOnClickListener {
                handleConfirm(etCardNumber, etCarNumber, etUnitName, etDeviceName, etAmount)
            }
            
            btnPrint.setOnClickListener {
                // Android 12+ 需要检查 BLUETOOTH_CONNECT 和 BLUETOOTH_SCAN 权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val neededPermissions = mutableListOf<String>()
                    
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) 
                        != PackageManager.PERMISSION_GRANTED) {
                        neededPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                    
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) 
                        != PackageManager.PERMISSION_GRANTED) {
                        neededPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
                    }
                    
                    if (neededPermissions.isNotEmpty()) {
                        Log.w(TAG, "缺少蓝牙权限: ${neededPermissions.joinToString()}")
                        ActivityCompat.requestPermissions(
                            this, 
                            neededPermissions.toTypedArray(), 
                            BLUETOOTH_SCAN_PERMISSION_REQUEST
                        )
                        Toast.makeText(this, "请授予蓝牙权限后再打印", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                
                // 如果 PUQU 打印器未初始化(之前因权限问题跳过),现在重新初始化
                if (!::puquPrinter.isInitialized) {
                    Log.d(TAG, "首次打印,初始化 PUQU 打印器")
                    try {
                        puquPrinter = PuQuPrinterManager(this)
                        puquPrinter.initialize()
                        puquPrinter.setCallback(object : PuQuPrinterManager.PrinterCallback {
                            override fun onConnecting(printerName: String) {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "正在连接: $printerName", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onConnected(printerName: String) {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "✓ 已连接: $printerName", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onDisconnected() {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "打印机已断开", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onPrintStart() {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "开始打印...", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onPrintSuccess() {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "✓ 打印成功！", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onPrintFailed(error: String) {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "❌ 打印失败: $error", Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                        Log.d(TAG, "✓ PUQU 打印器初始化成功")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ PUQU 打印器初始化失败", e)
                        Toast.makeText(this, "打印器初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
                
                handlePrint(etCardNumber, etCarNumber, etUnitName, etDeviceName, etAmount)
            }
            
            // 绑定临时测试打印按钮：尝试连接打印机并发送少量原始测试数据
            btnTestPrint?.setOnClickListener {
                Toast.makeText(this, "🖨️ 正在执行测试打印，请稍候...", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "用户触发：测试打印按钮")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val connected = bluetoothPrinter.connectToPrinter()
                        Log.d(TAG, "测试打印 - 连接结果: $connected")

                        val success = if (connected) {
                            bluetoothPrinter.printRawTest()
                        } else {
                            false
                        }

                        withContext(Dispatchers.Main) {
                            if (success) {
                                Toast.makeText(this@MainActivity, "✓ 测试打印指令已发送", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "测试打印：指令发送成功")
                            } else {
                                Toast.makeText(this@MainActivity, "✗ 测试打印失败（请检查打印机配对/电量/纸张）", Toast.LENGTH_LONG).show()
                                Log.e(TAG, "测试打印：发送失败")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "测试打印异常", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "✗ 测试打印发生异常: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            btnSelectPrinter?.setOnClickListener {
                // 弹出已配对设备列表供用户选择
                try {
                    val devices = bluetoothPrinter.getAvailableDevices()
                    if (devices.isEmpty()) {
                        Toast.makeText(this, "未找到已配对设备，请先在系统设置中配对打印机", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val names = devices.map { "${it.first} (${it.second})" }.toTypedArray()

                    AlertDialog.Builder(this)
                        .setTitle("选择首选打印机")
                        .setItems(names) { _, which ->
                            val selected = devices[which]
                            // 保存到 SharedPreferences
                            val prefs = getSharedPreferences("nfc_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("pref_printer_address", selected.second).apply()
                            Toast.makeText(this, "已选择打印机: ${selected.first}", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "用户选择首选打印机: ${selected.first} / ${selected.second}")
                        }
                        .setNegativeButton("取消", null)
                        .show()

                } catch (e: Exception) {
                    Log.e(TAG, "选择打印机失败: ${e.message}", e)
                    Toast.makeText(this, "选择打印机失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            btnUpload.setOnClickListener {
                handleUpload()
            }
            
            tvNfcid.text = "NFC已就绪"
            Log.d(TAG, "Components initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing components", e)
            Toast.makeText(this, "部分功能初始化失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 初始化NFC适配器和前台调度系统
     */
    private fun initializeNFC() {
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            
            if (nfcAdapter == null) {
                Log.w(TAG, "设备不支持NFC")
                Toast.makeText(this, "设备不支持NFC功能", Toast.LENGTH_SHORT).show()
                return
            }
            
            if (!nfcAdapter!!.isEnabled) {
                Toast.makeText(this, "请在系统设置中开启NFC", Toast.LENGTH_LONG).show()
            }
            
            // 创建PendingIntent用于前台调度
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            
            // 设置IntentFilter数组以捕获所有NFC事件
            val tagDiscovered = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            val ndefDiscovered = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            val techDiscovered = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            intentFilters = arrayOf(tagDiscovered, ndefDiscovered, techDiscovered)
            
            // 设置支持的NFC技术列表
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
            
            Log.d(TAG, "✓ NFC初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "NFC初始化失败", e)
            Toast.makeText(this, "NFC初始化失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 获取手机号码并绑定到机号
     */
    private fun getPhoneNumber(etCarNumber: TextView) {
        try {
            // 检查权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，直接使用设备ID
                Log.w(TAG, "⚠️ 未获得读取手机状态权限，使用设备ID")
                val deviceId = android.provider.Settings.Secure.getString(
                    contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )
                val displayNumber = if (deviceId.length >= 11) {
                    deviceId.take(11)
                } else {
                    deviceId.padEnd(11, '0')
                }
                fullCarNumber = displayNumber  // 保存完整机号
                etCarNumber.text = displayNumber  // 显示完整机号
                Log.d(TAG, "✓ 机号已设置为设备ID: $displayNumber")
                return
            }
            
            // 有权限，尝试获取手机号
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            if (telephonyManager == null) {
                Log.e(TAG, "❌ 无法获取TelephonyManager")
                useDeviceIdAsMachineNumber(etCarNumber)
                return
            }
            
            var phoneNumber: String? = null
            
            // 尝试不同方法获取手机号
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13+
                    phoneNumber = telephonyManager.subscriberId
                } else {
                    // Android 12及以下
                    @Suppress("DEPRECATION")
                    phoneNumber = telephonyManager.line1Number
                    
                    if (phoneNumber.isNullOrEmpty()) {
                        @Suppress("DEPRECATION")
                        phoneNumber = telephonyManager.subscriberId
                    }
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException获取手机号: ${e.message}")
            }
            
            // 处理获取结果
            val displayNumber = when {
                !phoneNumber.isNullOrEmpty() && phoneNumber.length >= 11 -> {
                    Log.d(TAG, "✓ 成功获取手机号/IMSI")
                    phoneNumber.takeLast(11) // 取后11位
                }
                !phoneNumber.isNullOrEmpty() -> {
                    Log.d(TAG, "✓ 获取到号码但长度不足: $phoneNumber")
                    phoneNumber.padEnd(11, '0')
                }
                else -> {
                    Log.w(TAG, "⚠️ 无法获取手机号，使用设备ID")
                    val deviceId = android.provider.Settings.Secure.getString(
                        contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID
                    )
                    if (deviceId.length >= 11) deviceId.take(11) else deviceId.padEnd(11, '0')
                }
            }
            
            fullCarNumber = displayNumber  // 保存完整机号
            etCarNumber.text = displayNumber  // 显示完整机号
            Log.d(TAG, "✓ 机号已自动绑定: $displayNumber")
            
        } catch (e: Exception) {
            Log.e(TAG, "获取机号异常", e)
            useDeviceIdAsMachineNumber(etCarNumber)
        }
    }
    
    /**
     * 使用设备ID作为机号
     */
    private fun useDeviceIdAsMachineNumber(etCarNumber: TextView) {
        try {
            val deviceId = android.provider.Settings.Secure.getString(
                contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            val displayNumber = if (deviceId.length >= 11) {
                deviceId.take(11)
            } else {
                deviceId.padEnd(11, '0')
            }
            fullCarNumber = displayNumber  // 保存完整机号
            etCarNumber.text = displayNumber  // 显示完整机号
            Log.d(TAG, "✓ 使用设备ID作为机号: $displayNumber")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取设备ID失败", e)
            etCarNumber.text = "00000000000"
        }
    }
    
    /**
     * 处理读卡操作
     */
    private fun handleReadCard(
        etCardNumber: TextView, 
        tvNfcid: TextView,
        etUnitName: EditText,
        etDeviceName: EditText
    ) {
        Log.d(TAG, "========== 开始读卡流程 ==========")
        
        // 模拟读取卡号（实际应该从NFC读取）
        val mockCardNumber = "1234567890123456"
        fullCardNumber = mockCardNumber  // 保存完整卡号
        etCardNumber.text = mockCardNumber  // 显示完整卡号
        tvNfcid.text = "NFCID: ${mockCardNumber.substring(0, 8)}"
        
        // 查询历史记录
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lastRecord = database.nfcRecordDao().getLastRecordByCardNumber(mockCardNumber)
                
                withContext(Dispatchers.Main) {
                    if (lastRecord != null) {
                        // 填充历史记录的单位名称和设备名称
                        etUnitName.setText(lastRecord.unitName)
                        etDeviceName.setText(lastRecord.deviceName)
                        
                        Log.d(TAG, "✓ 已加载历史记录")
                        Log.d(TAG, "  单位名称: ${lastRecord.unitName}")
                        Log.d(TAG, "  设备名称: ${lastRecord.deviceName}")
                        
                        Toast.makeText(
                            this@MainActivity,
                            "📖 读卡成功！已加载历史信息",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d(TAG, "ℹ️ 该卡号无历史记录")
                        Toast.makeText(
                            this@MainActivity,
                            "📖 读卡成功！卡号: $mockCardNumber",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "查询历史记录失败", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "读卡成功，但加载历史记录失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    /**
     * 处理确认操作 - 保存数据到数据库
     */
    private fun handleConfirm(
        etCardNumber: TextView,
        etCarNumber: TextView,
        etUnitName: EditText,
        etDeviceName: EditText,
        etAmount: EditText
    ) {
        Log.d(TAG, "========== 开始保存记录 ==========")
        
        // 使用完整号码而不是显示的后4位
        val cardNumber = fullCardNumber.ifEmpty { etCardNumber.text.toString().trim() }
        val carNumber = fullCarNumber.ifEmpty { etCarNumber.text.toString().trim() }
        val unitName = etUnitName.text.toString().trim()
        val deviceName = etDeviceName.text.toString().trim()
        val amount = etAmount.text.toString().trim()
        
        Log.d(TAG, "待保存数据:")
        Log.d(TAG, "  卡号: $cardNumber")
        Log.d(TAG, "  机号: $carNumber")
        Log.d(TAG, "  单位名称: $unitName")
        Log.d(TAG, "  设备名称: $deviceName")
        Log.d(TAG, "  消费金额: $amount")
        
        // 验证输入
        if (cardNumber.isEmpty() || cardNumber == "请先读卡") {
            speak("请先读卡")
            Toast.makeText(this, "⚠️ 请先读卡", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (carNumber.isEmpty() || carNumber == "自动获取中...") {
            speak("机号正在获取中，请稍候")
            Toast.makeText(this, "⚠️ 机号正在获取中，请稍候", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (unitName.isEmpty()) {
            speak("请输入单位名称")
            Toast.makeText(this, "⚠️ 请输入单位名称", Toast.LENGTH_SHORT).show()
            etUnitName.requestFocus()
            return
        }
        
        if (deviceName.isEmpty()) {
            speak("请输入设备名称")
            Toast.makeText(this, "⚠️ 请输入设备名称", Toast.LENGTH_SHORT).show()
            etDeviceName.requestFocus()
            return
        }
        
        if (amount.isEmpty()) {
            speak("请输入消费金额")
            Toast.makeText(this, "⚠️ 请输入消费金额", Toast.LENGTH_SHORT).show()
            return
        }
        
        Log.d(TAG, "✓ 输入验证通过")
        
        // 异步保存到数据库（保存完整卡号和机号）
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 检查是否为新卡（查询该卡号是否有历史记录）
                val existingRecords = database.nfcRecordDao().getRecordsByCardNumber(cardNumber)
                val isNewCard = existingRecords.isEmpty()
                
                if (isNewCard) {
                    // 新卡，调用API接口
                    Log.d(TAG, "📝 检测到新卡，调用API录入...")
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "📝 正在录入新卡信息...", Toast.LENGTH_SHORT).show()
                    }
                    
                    val apiSuccess = callInsertAPI(
                        className = unitName,  // 单位名称
                        memo = cardNumber,     // 卡号
                        shunXu = deviceName    // 设备名称
                    )
                    
                    withContext(Dispatchers.Main) {
                        if (apiSuccess) {
                            Log.d(TAG, "✓ API调用成功")
                            Toast.makeText(this@MainActivity, "✓ 新卡信息已录入系统", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.w(TAG, "⚠️ API调用失败")
                            Toast.makeText(this@MainActivity, "⚠️ 录入系统失败，但本地已保存", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d(TAG, "ℹ️ 老卡，跳过API调用")
                }
                
                val record = NFCRecord(
                    nfcId = cardNumber.take(8), // NFCID取前8位（用于内部识别）
                    cardNumber = cardNumber,  // 保存完整卡号
                    carNumber = carNumber,    // 保存完整机号
                    unitName = unitName,
                    deviceName = deviceName,
                    amount = amount,
                    readTime = System.currentTimeMillis(),
                    content = "消费记录: $amount 元",
                    uploadStatus = false
                )
                
                database.nfcRecordDao().insert(record)
                Log.d(TAG, "✓ 记录保存成功")
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "✔️ 保存成功！", Toast.LENGTH_SHORT).show()
                    
                    // 刷新列表显示
                    loadRecords()
                    
                    // 自动打印小票（使用 PUQU SDK）
                    Log.d(TAG, "🖨️ 保存成功后自动打印小票...")
                    
                    // 延迟初始化检查（如果之前未初始化）
                    if (!::puquPrinter.isInitialized) {
                        Log.d(TAG, "首次确认打印,初始化 PUQU 打印器")
                        try {
                            puquPrinter = PuQuPrinterManager(this@MainActivity)
                            puquPrinter.initialize()
                            puquPrinter.setCallback(object : PuQuPrinterManager.PrinterCallback {
                                override fun onConnecting(printerName: String) {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "正在连接: $printerName", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onConnected(printerName: String) {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "✓ 已连接: $printerName", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onDisconnected() {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "打印机已断开", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onPrintStart() {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "开始打印...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onPrintSuccess() {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "✓ 确认并打印成功！", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onPrintFailed(error: String) {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "❌ 打印失败: $error", Toast.LENGTH_LONG).show()
                                    }
                                }
                            })
                            Log.d(TAG, "✓ PUQU 打印器初始化成功")
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ PUQU 打印器初始化失败", e)
                            Toast.makeText(this@MainActivity, "打印器初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
                            return@withContext
                        }
                    }
                    
                    // 使用 PUQU SDK 自动打印
                    lifecycleScope.launch {
                        val printSuccess = puquPrinter.autoPrintReceipt(
                            cardNumber = cardNumber,
                            carNumber = carNumber,
                            unitName = unitName,
                            deviceName = deviceName,
                            amount = amount,
                            readTime = System.currentTimeMillis()
                        )
                        
                        if (printSuccess) {
                            Log.d(TAG, "✓ 确认后自动打印成功")
                        } else {
                            Log.w(TAG, "⚠️ 确认后自动打印失败")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 保存失败: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "✗ 保存失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * 加载历史记录
     */
    private fun loadRecords() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val records = database.nfcRecordDao().getAllRecords()
                Log.d(TAG, "✓ 加载到 ${records.size} 条记录")
                
                withContext(Dispatchers.Main) {
                    recordAdapter.submitList(records)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 加载记录失败: ${e.message}", e)
            }
        }
    }
    
    /**
     * 处理打印操作 - 使用BLE模式
     */
    private fun handlePrint(
        etCardNumber: TextView, 
        etCarNumber: TextView,
        etUnitName: EditText,
        etDeviceName: EditText,
        etAmount: EditText
    ) {
        Log.d(TAG, "========== 开始打印流程(PUQU自动扫描连接) ==========")

        // 使用完整号码而不是显示的后4位
        val cardNumber = fullCardNumber.ifEmpty { etCardNumber.text.toString().trim() }
        val carNumber = fullCarNumber.ifEmpty { etCarNumber.text.toString().trim() }
        val unitName = etUnitName.text.toString().trim()
        val deviceName = etDeviceName.text.toString().trim()
        val amount = etAmount.text.toString().trim()

        Log.d(TAG, "打印参数:")
        Log.d(TAG, "  卡号: $cardNumber")
        Log.d(TAG, "  机号: $carNumber")
        Log.d(TAG, "  单位名称: $unitName")
        Log.d(TAG, "  设备名称: $deviceName")
        Log.d(TAG, "  金额: $amount")

        // 验证输入
        if (cardNumber.isEmpty() || cardNumber == "请先读卡") {
            Log.w(TAG, "⚠️ 卡号为空")
            Toast.makeText(this, "请先读卡", Toast.LENGTH_SHORT).show()
            return
        }

        if (carNumber.isEmpty() || carNumber == "自动获取中...") {
            Log.w(TAG, "⚠️ 机号未获取")
            Toast.makeText(this, "机号正在获取中，请稍候", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount.isEmpty()) {
            Log.w(TAG, "⚠️ 金额为空")
            Toast.makeText(this, "请输入消费金额", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "✓ 输入验证通过")

        // 使用 PUQU 自动扫描、连接、打印
        Toast.makeText(this, "🖨️ 正在自动连接打印机...", Toast.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 自动扫描、连接、打印（一步完成）
                val success = puquPrinter.autoPrintReceipt(
                    cardNumber = cardNumber,
                    carNumber = carNumber,
                    unitName = unitName,
                    deviceName = deviceName,
                    amount = amount,
                    readTime = System.currentTimeMillis()
                )
                
                withContext(Dispatchers.Main) {
                    if (success) {
                        Log.d(TAG, "✓✓✓ PUQU 自动打印成功")
                        // 保存记录到数据库
                        saveRecordToDatabase(cardNumber, carNumber, unitName, deviceName, amount)
                        Toast.makeText(this@MainActivity, "✓ 打印成功！", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "❌ PUQU 自动打印失败")
                        // 打印失败，提供备选方案
                        showPrintFailureDialog(cardNumber, carNumber, unitName, deviceName, amount)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "PUQU 打印异常", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "打印异常: ${e.message}", Toast.LENGTH_LONG).show()
                    showPrintFailureDialog(cardNumber, carNumber, unitName, deviceName, amount)
                }
            }
        }
    }
    
    /**
     * 尝试使用 BLE 打印
     */
    private fun tryBlePrint(cardNumber: String, carNumber: String, unitName: String, deviceName: String, amount: String) {
        Log.d(TAG, "========== 尝试 BLE 打印 ==========")
        
        // 检查 BLE 是否已连接
        if (blePrinter.isConnected()) {
            Log.d(TAG, "BLE 打印机已连接，直接打印")
            Toast.makeText(this, "🖨️ 使用 BLE 打印机打印...", Toast.LENGTH_SHORT).show()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val blePrinted = blePrinter.printReceipt(cardNumber, carNumber, unitName, deviceName, amount)
                    
                    withContext(Dispatchers.Main) {
                        if (blePrinted) {
                            Log.d(TAG, "✓✓✓ BLE 打印成功")
                            saveRecordToDatabase(cardNumber, carNumber, unitName, deviceName, amount)
                            Toast.makeText(this@MainActivity, "✓ 打印成功！(BLE)", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "❌ BLE 打印失败")
                            showPrintFailureDialog(cardNumber, carNumber, unitName, deviceName, amount)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "BLE 打印异常", e)
                    withContext(Dispatchers.Main) {
                        showPrintFailureDialog(cardNumber, carNumber, unitName, deviceName, amount)
                    }
                }
            }
        } else {
            Log.w(TAG, "BLE 打印机未连接，提示用户选择")
            AlertDialog.Builder(this)
                .setTitle("打印失败")
                .setMessage("经典蓝牙打印失败。\n\n是否使用 BLE 打印？")
                .setPositiveButton("使用 BLE") { _, _ ->
                    // 先扫描 BLE 设备
                    Toast.makeText(this, "正在扫描 BLE 设备...", Toast.LENGTH_SHORT).show()
                    
                    // 保存打印参数供后续使用
                    val pendingPrintData = Pair(Triple(cardNumber, carNumber, unitName), Pair(deviceName, amount))
                    getSharedPreferences("nfc_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("pending_print_data", "${pendingPrintData.first.first},${pendingPrintData.first.second},${pendingPrintData.first.third},${pendingPrintData.second.first},${pendingPrintData.second.second}")
                        .apply()
                    
                    // 设置 BLE 连接成功后的自动打印回调
                    blePrinter.onConnected = {
                        runOnUiThread {
                            Toast.makeText(this, "BLE 已连接，正在打印...", Toast.LENGTH_SHORT).show()
                            
                            // 延迟一下确保连接稳定
                            CoroutineScope(Dispatchers.Main).launch {
                                kotlinx.coroutines.delay(500)
                                val printData = getSharedPreferences("nfc_prefs", Context.MODE_PRIVATE)
                                    .getString("pending_print_data", null)
                                if (printData != null) {
                                    val parts = printData.split(",")
                                    if (parts.size == 5) {
                                        tryBlePrint(parts[0], parts[1], parts[2], parts[3], parts[4])
                                        // 清除待打印数据
                                        getSharedPreferences("nfc_prefs", Context.MODE_PRIVATE)
                                            .edit()
                                            .remove("pending_print_data")
                                            .apply()
                                    }
                                }
                            }
                        }
                    }
                    
                    // 开始扫描
                    startBleScan()
                }
                .setNegativeButton("取消") { _, _ ->
                    showPrintFailureDialog(cardNumber, carNumber, unitName, deviceName, amount)
                }
                .show()
        }
    }
    
    /**
     * 开始 BLE 扫描
     */
    private fun startBleScan() {
        blePrinter.onScanComplete = { list ->
            runOnUiThread {
                if (list.isEmpty()) {
                    Toast.makeText(this, "未找到任何 BLE 设备", Toast.LENGTH_SHORT).show()
                } else {
                    val names = list.map { it.first }.toTypedArray()
                    AlertDialog.Builder(this)
                        .setTitle("选择 BLE 打印机")
                        .setItems(names) { _, which ->
                            val sel = list[which]
                            // 保存为首选
                            getSharedPreferences("nfc_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("pref_printer_address", sel.second)
                                .apply()
                            
                            Toast.makeText(this, "正在连接: ${sel.first}", Toast.LENGTH_SHORT).show()
                            
                            // 连接
                            val ok = blePrinter.connect(sel.second)
                            if (!ok) {
                                Toast.makeText(this, "连接请求发出失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("取消", null)
                        .show()
                }
            }
        }
        
        blePrinter.scanForPrinters(10000)
    }
    
    /**
     * 显示打印失败对话框
     */
    private fun showPrintFailureDialog(cardNumber: String, carNumber: String, unitName: String, deviceName: String, amount: String) {
        AlertDialog.Builder(this)
            .setTitle("打印失败")
            .setMessage("经典蓝牙和 BLE 打印均失败。\n\n是否重试？")
            .setPositiveButton("重试") { _, _ ->
                // 重新尝试打印 - 直接使用参数
                tryClassicBluetoothPrint(cardNumber, carNumber, unitName, deviceName, amount)
            }
            .setNegativeButton("取消") { _, _ ->
                // 询问是否仍要保存到数据库
                AlertDialog.Builder(this)
                    .setTitle("保存记录")
                    .setMessage("打印失败，是否仍要保存记录到数据库？")
                    .setPositiveButton("保存") { _, _ ->
                        saveRecordToDatabase(cardNumber, carNumber, unitName, deviceName, amount)
                        Toast.makeText(this, "✓ 记录已保存", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            .show()
    }
    
    /**
     * 尝试经典蓝牙打印
     */
    private fun tryClassicBluetoothPrint(cardNumber: String, carNumber: String, unitName: String, deviceName: String, amount: String) {
        Toast.makeText(this, "🖨️ 重新尝试经典蓝牙打印...", Toast.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            var printed = false
            try {
                val connected = bluetoothPrinter.connectToPrinter()
                if (connected) {
                    Thread.sleep(500)
                    printed = bluetoothPrinter.printReceipt(cardNumber, carNumber, unitName, deviceName, amount)
                }
            } catch (e: Exception) {
                Log.e(TAG, "经典蓝牙重试失败", e)
            }
            
            withContext(Dispatchers.Main) {
                if (printed) {
                    saveRecordToDatabase(cardNumber, carNumber, unitName, deviceName, amount)
                    Toast.makeText(this@MainActivity, "✓ 打印成功！", Toast.LENGTH_SHORT).show()
                } else {
                    // 再次失败，尝试 BLE
                    tryBlePrint(cardNumber, carNumber, unitName, deviceName, amount)
                }
            }
        }
    }
    
    /**
     * 处理上传操作
     */
    private fun handleUpload() {
        Log.d(TAG, "========== 开始上传流程 ==========")
        
        Toast.makeText(this, "☁️ 正在上传记录...", Toast.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 获取所有未上传的记录
                val unuploadedRecords = database.nfcRecordDao().getUnuploadedRecords()
                
                if (unuploadedRecords.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "ℹ️ 没有需要上传的记录", Toast.LENGTH_SHORT).show()
                        speak("没有需要上传的记录")
                    }
                    return@launch
                }
                
                Log.d(TAG, "找到 ${unuploadedRecords.size} 条未上传记录")
                
                var successCount = 0
                var failCount = 0
                var totalAmount = 0.0
                
                // 循环上传每条记录
                for (record in unuploadedRecords) {
                    Log.d(TAG, "正在上传记录 ID=${record.id}, 卡号=${record.cardNumber}")
                    
                    val success = callUploadRecordAPI(record)
                    val currentTime = System.currentTimeMillis()
                    
                    if (success) {
                        // 上传成功
                        successCount++
                        try {
                            val amount = record.amount.toDoubleOrNull() ?: 0.0
                            totalAmount += amount
                        } catch (e: Exception) {
                            Log.w(TAG, "金额转换失败: ${record.amount}")
                        }
                        
                        // 更新记录状态为成功
                        val updatedRecord = record.copy(
                            uploadStatus = true,
                            uploadSuccess = true,
                            uploadTime = currentTime
                        )
                        database.nfcRecordDao().update(updatedRecord)
                        
                        Log.d(TAG, "✓ 记录 ${record.id} 上传成功")
                    } else {
                        // 上传失败
                        failCount++
                        
                        // 更新记录状态为失败
                        val updatedRecord = record.copy(
                            uploadStatus = true,
                            uploadSuccess = false,
                            uploadTime = currentTime
                        )
                        database.nfcRecordDao().update(updatedRecord)
                        
                        Log.w(TAG, "✗ 记录 ${record.id} 上传失败")
                    }
                    
                    // 短暂延迟避免请求过快
                    Thread.sleep(100)
                }
                
                // 刷新列表显示
                withContext(Dispatchers.Main) {
                    loadRecords()
                }
                
                // 生成语音播报内容
                val speakMessages = mutableListOf<String>()
                
                if (successCount > 0) {
                    speakMessages.add("共成功上传${successCount}笔刷卡记录，总金额${String.format("%.2f", totalAmount)}元")
                }
                
                if (failCount > 0) {
                    speakMessages.add("有${failCount}条记录上传失败")
                }
                
                // 检查是否还有未上传的记录
                val stillUnuploaded = database.nfcRecordDao().getUnuploadedRecords()
                if (stillUnuploaded.isNotEmpty()) {
                    speakMessages.add("${stillUnuploaded.size}条记录未上传")
                }
                
                val finalMessage = speakMessages.joinToString("，")
                
                Log.d(TAG, "上传完成: 成功=$successCount, 失败=$failCount, 总金额=$totalAmount")
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "✓ 上传完成\n成功: $successCount 笔\n失败: $failCount 笔\n总金额: ${String.format("%.2f", totalAmount)} 元",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // 语音播报
                    speak(finalMessage)
                }
                
                Log.d(TAG, "========== 上传流程结束 ==========")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ 上传过程发生异常", e)
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "上传异常: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * 请求蓝牙权限
     */
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ 需要新的蓝牙权限
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            
            val needRequest = permissions.any { 
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED 
            }
            
            if (needRequest) {
                ActivityCompat.requestPermissions(this, permissions, BLUETOOTH_PERMISSION_REQUEST)
            }
        } else {
            // Android 11 及以下
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
            
            val needRequest = permissions.any { 
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED 
            }
            
            if (needRequest) {
                ActivityCompat.requestPermissions(this, permissions, BLUETOOTH_PERMISSION_REQUEST)
            }
        }
    }
    
    /**
     * 请求读取手机状态权限
     */
    private fun requestPhoneStatePermission() {
        val permission = Manifest.permission.READ_PHONE_STATE
        
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PHONE_STATE_PERMISSION_REQUEST)
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST)
            }
        } else {
            val permission = Manifest.permission.ACCESS_COARSE_LOCATION
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST)
            }
        }
    }
    
    /**
     * 保存记录到数据库
     */
    private fun saveRecordToDatabase(
        cardNumber: String,
        carNumber: String,
        unitName: String,
        deviceName: String,
        amount: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val record = com.nfc.app.database.NFCRecord(
                    nfcId = cardNumber.substring(0, minOf(8, cardNumber.length)),
                    cardNumber = cardNumber,
                    carNumber = carNumber,
                    unitName = unitName,
                    deviceName = deviceName,
                    amount = amount,
                    readTime = System.currentTimeMillis(),
                    content = "卡号:$cardNumber,机号:$carNumber,单位:$unitName,设备:$deviceName,金额:$amount",
                    uploadStatus = false
                )
                
                database.nfcRecordDao().insert(record)
                Log.d(TAG, "✓ 记录已保存到数据库")
                
            } catch (e: Exception) {
                Log.e(TAG, "保存记录失败", e)
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "✓ 蓝牙权限已授予", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "✗ 蓝牙权限被拒绝，打印功能无法使用", Toast.LENGTH_LONG).show()
                }
            }
            PHONE_STATE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "✓ 手机状态权限已授予")
                    // 重新获取手机号
                    val etCarNumber = findViewById<TextView>(getResId("et_car_number", "id"))
                    getPhoneNumber(etCarNumber)
                } else {
                    Log.w(TAG, "用户拒绝手机状态权限，继续使用设备ID")
                }
            }
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "✓ 定位权限已授予，可进行 BLE 扫描")
                } else {
                    Log.w(TAG, "未授予定位权限，BLE 扫描将使用现有权限状态")
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // 启用NFC前台调度，拦截所有NFC事件
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
        Log.d(TAG, "✓ NFC前台调度已启用")
    }
    
    override fun onPause() {
        super.onPause()
        // 禁用NFC前台调度
        nfcAdapter?.disableForegroundDispatch(this)
        Log.d(TAG, "✓ NFC前台调度已禁用")
    }
    
    /**
     * 处理NFC标签检测事件（自动读卡）
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        Log.d(TAG, "========== onNewIntent 开始 ==========")
        Log.d(TAG, "Intent action: ${intent?.action}")
        
        // 检查是否为NFC意图
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent?.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            
            Log.d(TAG, "✓ 确认为NFC事件")
            
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                Log.d(TAG, "✓ Tag获取成功")
                
                // 将标签ID转换为十六进制字符串作为卡号
                val tagId = tag.id
                Log.d(TAG, "TagId字节数组: ${tagId.contentToString()}")
                
                val cardNumber = tagId.joinToString("") { String.format("%02X", it) }
                
                Log.d(TAG, "📱 NFC卡检测到: $cardNumber (完整)")
                Log.d(TAG, "卡号长度: ${cardNumber.length} 字符")
                
                // 【立即刷新】保存完整卡号并立即更新UI显示（显示完整卡号）
                fullCardNumber = cardNumber
                Log.d(TAG, "保存到fullCardNumber: $fullCardNumber")
                
                // 检查视图是否已初始化,如果未初始化则重新查找
                if (!::etCardNumber.isInitialized) {
                    Log.w(TAG, "⚠️ 视图未初始化,重新查找视图")
                    try {
                        tvNfcid = findViewById<TextView>(getResId("tv_nfcid", "id"))
                        etCardNumber = findViewById<TextView>(getResId("et_card_number", "id"))
                        etUnitName = findViewById<EditText>(getResId("et_unit_name", "id"))
                        etDeviceName = findViewById<EditText>(getResId("et_device_name", "id"))
                        tvFuelDate = findViewById<TextView>(getResId("tv_fuel_date", "id"))
                        Log.d(TAG, "✓ 视图重新初始化成功")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ 视图初始化失败", e)
                        Toast.makeText(this, "❌ 视图初始化失败,请重启应用", Toast.LENGTH_LONG).show()
                        return
                    }
                }
                
                etCardNumber.text = cardNumber  // 显示完整卡号
                tvNfcid.text = "NFCID: ${cardNumber.take(8)}"
                Log.d(TAG, "✓ UI已更新: etCardNumber 和 tvNfcid")
                
                // 清空消费金额
                val etAmount = findViewById<EditText>(getResId("et_amount", "id"))
                etAmount.setText("")
                Log.d(TAG, "✓ 消费金额已清空")
                
                // 立即显示读卡成功提示
                Toast.makeText(this, "✓ 读卡成功！卡号: $cardNumber", Toast.LENGTH_SHORT).show()
                
                // 语音播报：读卡成功
                speak("读卡成功")
                Log.d(TAG, "✓ 语音播报: 读卡成功")
                
                // 异步查询数据库并自动填充历史记录（使用完整卡号查询）
                Log.d(TAG, "🔍 开始查询历史记录，使用完整卡号: $cardNumber")
                
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.d(TAG, ">>> 进入协程，开始数据库查询")
                        
                        val lastRecord = database.nfcRecordDao().getLastRecordByCardNumber(cardNumber)
                        Log.d(TAG, "查询lastRecord结果: ${if (lastRecord != null) "找到记录 id=${lastRecord.id}" else "无记录"}")
                        
                        // 获取该卡的所有历史记录
                        val allRecords = database.nfcRecordDao().getRecordsByCardNumber(cardNumber)
                        Log.d(TAG, "查询allRecords结果: ${allRecords.size} 条记录")
                        
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, ">>> 切换到主线程，开始UI更新")
                            if (lastRecord != null) {
                                // 找到本地历史记录，自动填充单位名称和设备名称
                                etUnitName.setText(lastRecord.unitName)
                                etDeviceName.setText(lastRecord.deviceName)
                                Log.d(TAG, "✓ 已加载本地历史记录: 单位=${lastRecord.unitName}, 设备=${lastRecord.deviceName}")
                                
                                // 显示该卡的历史记录
                                recordAdapter.submitList(allRecords)
                                
                                // 语音播报单位名称和设备名称
                                val speakText = "${lastRecord.unitName}，${lastRecord.deviceName}"
                                speak(speakText)
                                
                                Toast.makeText(this@MainActivity, "✓ 已加载历史信息", Toast.LENGTH_SHORT).show()
                            } else {
                                // 本地没有记录，调用服务器API查询
                                Log.d(TAG, "ℹ️ 本地无记录，查询服务器...")
                                Toast.makeText(this@MainActivity, "🔍 正在查询服务器...", Toast.LENGTH_SHORT).show()
                                
                                val (success, data) = callFindAPI(cardNumber)
                                
                                if (!success) {
                                    // API调用失败
                                    Log.w(TAG, "⚠️ 服务器查询失败")
                                    speak("获取服务器数据失败，请检查网络")
                                    Toast.makeText(this@MainActivity, "⚠️ 获取服务器数据失败，请检查网络", Toast.LENGTH_LONG).show()
                                    
                                    // 清空字段
                                    etUnitName.setText("")
                                    etDeviceName.setText("")
                                    recordAdapter.submitList(emptyList())
                                } else if (data == null) {
                                    // 服务器也没有记录，视为新卡
                                    Log.d(TAG, "ℹ️ 服务器也无记录，确认为新卡")
                                    speak("新卡，请录入单位名称，设备名称")
                                    Toast.makeText(this@MainActivity, "ℹ️ 新卡，请录入信息", Toast.LENGTH_SHORT).show()
                                    
                                    // 清空字段
                                    etUnitName.setText("")
                                    etDeviceName.setText("")
                                    recordAdapter.submitList(emptyList())
                                } else {
                                    // 从服务器获取到记录，填充数据
                                    try {
                                        val className = data.optString("ClassName", "")
                                        val shunXu = data.optString("ShunXu", "")
                                        
                                        Log.d(TAG, "✓ 从服务器加载: 单位=$className, 设备=$shunXu")
                                        
                                        etUnitName.setText(className)
                                        etDeviceName.setText(shunXu)
                                        recordAdapter.submitList(emptyList())
                                        
                                        // 语音播报
                                        val speakText = "$className，$shunXu"
                                        speak(speakText)
                                        
                                        Toast.makeText(this@MainActivity, "✓ 已从服务器加载卡信息", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e(TAG, "解析服务器数据失败", e)
                                        Toast.makeText(this@MainActivity, "⚠️ 数据解析失败", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "查询历史记录失败", e)
                        // 查询失败也不影响，卡号已经显示了
                        withContext(Dispatchers.Main) {
                            Log.w(TAG, "⚠️ 无法加载历史记录: ${e.message}")
                            Toast.makeText(this@MainActivity, "⚠️ 查询失败: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Log.w(TAG, "⚠️ 无法获取NFC标签")
                Toast.makeText(this, "读卡失败，请重试", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // 释放语音播报资源
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
            Log.d(TAG, "TTS已释放")
        }
        
        // 断开蓝牙连接
        try {
            bluetoothPrinter.disconnect()
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting printer", e)
        }
        
        // 释放 PUQU 资源
        try {
            if (::puquPrinter.isInitialized) {
                puquPrinter.release()
                Log.d(TAG, "PUQU已释放")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing PUQU", e)
        }
    }
    
    /**
     * TextToSpeech初始化回调
     */
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
    
    /**
     * 语音播报
     */
    private fun speak(text: String) {
        if (ttsReady) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            Log.d(TAG, "🔊 语音播报: $text")
        } else {
            Log.w(TAG, "⚠️ TTS未就绪，无法播报: $text")
        }
    }
    
    /**
     * 显示日期选择对话框
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        
        // 如果已有日期，解析并设置
        if (fuelDate.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                val date = dateFormat.parse(fuelDate)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                Log.w(TAG, "解析日期失败: $fuelDate", e)
            }
        }
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // 更新日期
                fuelDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                tvFuelDate.text = fuelDate
                Log.d(TAG, "选择日期: $fuelDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show()
    }
    
    /**
     * 调用API接口录入新卡信息
     */
    private suspend fun callInsertAPI(
        className: String,  // 单位名称
        memo: String,       // 卡号
        shunXu: String      // 设备名称
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://tx.qsgl.net:5190/qsoft483/procedure/ClassName_Insert"
                
                val jsonObject = JSONObject().apply {
                    put("Type", "加油卡")
                    put("ClassName", className)
                    put("Memo", memo)
                    put("ShunXu", shunXu)
                    put("UsersID", "14024")
                    put("MenuName", "加油刷卡APP")
                }
                
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonObject.toString().toRequestBody(mediaType)
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                
                Log.d(TAG, "📤 调用API: $url")
                Log.d(TAG, "📤 请求数据: ${jsonObject.toString()}")
                
                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                Log.d(TAG, "📥 响应码: ${response.code}")
                Log.d(TAG, "📥 响应数据: $responseBody")
                
                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "❌ API调用失败", e)
                false
            }
        }
    }
    
    /**
     * 调用API接口查询卡信息
     * @return Pair<成功标志, 数据对象或null>
     */
    private suspend fun callFindAPI(memo: String): Pair<Boolean, JSONObject?> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://tx.qsgl.net:5190/qsoft483/procedure/ClassName_Find"
                
                val jsonObject = JSONObject().apply {
                    put("Limit", "1")
                    put("Offset", "0")
                    put("OrderStr", "Type,ShunXu,ClassName asc")
                    put("UsersID", "14024")
                    put("Menu_Name", "加油刷卡APP")
                    put("Memo", memo)
                    put("WhereStr", "1=1")
                    put("Type", "加油卡")
                    put("IsMobile", true)
                }
                
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonObject.toString().toRequestBody(mediaType)
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                
                Log.d(TAG, "📤 查询API: $url")
                Log.d(TAG, "📤 查询数据: ${jsonObject.toString()}")
                
                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                Log.d(TAG, "📥 查询响应码: ${response.code}")
                Log.d(TAG, "📥 查询响应: $responseBody")
                
                if (response.isSuccessful && responseBody.isNotEmpty()) {
                    val jsonResponse = JSONObject(responseBody)
                    
                    // 检查是否有错误消息
                    if (jsonResponse.has("Message")) {
                        val message = jsonResponse.getString("Message")
                        if (message.contains("没有匹配的记录")) {
                            Log.d(TAG, "ℹ️ 服务器返回：没有匹配的记录（新卡）")
                            return@withContext Pair(true, null)  // 成功但无数据，视为新卡
                        }
                    }
                    
                    // 检查是否有rows数组
                    if (jsonResponse.has("rows")) {
                        val rows = jsonResponse.getJSONArray("rows")
                        if (rows.length() > 0) {
                            val firstRow = rows.getJSONObject(0)
                            Log.d(TAG, "✓ 找到服务器记录: ${firstRow.toString()}")
                            return@withContext Pair(true, firstRow)
                        } else {
                            Log.d(TAG, "ℹ️ 服务器返回空数组（新卡）")
                            return@withContext Pair(true, null)
                        }
                    }
                    
                    Log.w(TAG, "⚠️ 服务器响应格式异常")
                    return@withContext Pair(false, null)
                } else {
                    Log.w(TAG, "⚠️ 服务器请求失败: ${response.code}")
                    return@withContext Pair(false, null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 查询API调用失败", e)
                return@withContext Pair(false, null)
            }
        }
    }
    
    /**
     * 调用API接口上传单条记录
     * @return 是否上传成功
     */
    private suspend fun callUploadRecordAPI(record: NFCRecord): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://tx.qsgl.net:5190/qsoft483/procedure/AccountDetail_Insert"
                
                // 格式化日期
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                val beginTime = dateFormat.format(Date(record.readTime))
                
                val jsonObject = JSONObject().apply {
                    put("BeginTime", beginTime)
                    put("设备名称", record.deviceName)
                    put("卡号", record.cardNumber)
                    put("单位名称", record.unitName)
                    put("机号", record.carNumber)
                    put("消费金额", record.amount)
                    put("UsersID", "14024")
                    put("MenuName", "加油APP")
                }
                
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonObject.toString().toRequestBody(mediaType)
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                
                Log.d(TAG, "📤 上传记录API: $url")
                Log.d(TAG, "📤 上传数据: ${jsonObject.toString()}")
                
                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                Log.d(TAG, "📥 上传响应码: ${response.code}")
                Log.d(TAG, "📥 上传响应: $responseBody")
                
                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "❌ 上传API调用失败", e)
                false
            }
        }
    }
    
    private fun getResId(name: String, type: String): Int {
        val id = resources.getIdentifier(name, type, packageName)
        Log.d(TAG, "Resource $name ($type) ID: $id")
        return id
    }
    
    private fun createFallbackLayout() {
        try {
            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(50, 50, 50, 50)
            }
            
            val textView = TextView(this).apply {
                text = "NFC 读写系统\n\n资源加载失败，使用备用界面"
                textSize = 20f
            }
            
            layout.addView(textView)
            setContentView(layout)
            
            Log.d(TAG, "Fallback layout created")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create fallback layout", e)
        }
    }
    
    // BLE callbacks removed
    
    // BLE scan dialog removed
    
    // BLE device selection removed
    
    // BLE connect-and-print removed
}
