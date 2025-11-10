package com.example.bleprinter

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bleprinter.adapter.DeviceAdapter
import com.example.bleprinter.bluetooth.BluetoothConnectionManager
import com.example.bleprinter.bluetooth.BluetoothScanner
import com.example.bleprinter.databinding.ActivityMainBinding
import com.example.bleprinter.model.BleDevice
import com.example.bleprinter.printer.PrinterCommands
import com.example.bleprinter.sdk.PuQuPrinterManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothScanner: BluetoothScanner
    private lateinit var connectionManager: BluetoothConnectionManager
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var puquPrinterManager: PuQuPrinterManager
    private var isUsingSDK = false  // 标记是否使用SDK模式
    
    // 权限请求
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            checkBluetoothEnabled()
        } else {
            Toast.makeText(this, "需要蓝牙权限才能使用此应用", Toast.LENGTH_LONG).show()
        }
    }
    
    // 启用蓝牙
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (bluetoothScanner.isBluetoothEnabled()) {
            Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要启用蓝牙才能使用此应用", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化蓝牙组件
        bluetoothScanner = BluetoothScanner(this)
        connectionManager = BluetoothConnectionManager(this)
        
        // 初始化PUQU SDK
        puquPrinterManager = PuQuPrinterManager(this)
        setupSDKCallbacks()
        
        setupRecyclerView()
        setupUI()
        observeData()
        
        // 请求权限
        requestBluetoothPermissions()
    }
    
    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter { device ->
            onDeviceClicked(device)
        }
        
        binding.recyclerViewDevices.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = deviceAdapter
        }
    }
    
    private fun setupSDKCallbacks() {
        // SDK连接成功回调
        puquPrinterManager.setOnConnectSuccess {
            runOnUiThread {
                binding.textViewConnectionStatus.text = "已连接 (SDK模式 - 就绪)"
                binding.buttonDisconnect.isEnabled = true
                binding.buttonTestPrint.isEnabled = true
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.holo_green_light)
                )
                Toast.makeText(this, "SDK连接成功,可以打印", Toast.LENGTH_SHORT).show()
                
                android.util.Log.d("MainActivity", "SDK连接成功,打印机就绪")
            }
        }
        
        // SDK连接失败回调
        puquPrinterManager.setOnConnectFailed {
            runOnUiThread {
                binding.textViewConnectionStatus.text = "连接失败 (SDK模式)"
                binding.buttonDisconnect.isEnabled = false
                binding.buttonTestPrint.isEnabled = false
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.holo_red_light)
                )
                Toast.makeText(this, "SDK连接失败", Toast.LENGTH_SHORT).show()
                isUsingSDK = false
            }
        }
        
        // SDK连接关闭回调
        puquPrinterManager.setOnConnectClosed {
            runOnUiThread {
                binding.textViewConnectionStatus.text = "未连接"
                binding.buttonDisconnect.isEnabled = false
                binding.buttonTestPrint.isEnabled = false
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.darker_gray)
                )
                Toast.makeText(this, "SDK连接已关闭", Toast.LENGTH_SHORT).show()
                isUsingSDK = false
            }
        }
    }
    
    private fun setupUI() {
        // 扫描按钮
        binding.buttonScan.setOnClickListener {
            if (bluetoothScanner.isScanning.value) {
                stopScanning()
            } else {
                startScanning()
            }
        }
        
        // 断开连接按钮
        binding.buttonDisconnect.setOnClickListener {
            if (isUsingSDK) {
                puquPrinterManager.disconnect()
                Toast.makeText(this, "SDK断开连接", Toast.LENGTH_SHORT).show()
            } else {
                connectionManager.disconnect()
            }
        }
        
        // 测试打印按钮
        binding.buttonTestPrint.setOnClickListener {
            testPrint()
        }
    }
    
    private fun observeData() {
        // 观察扫描状态
        lifecycleScope.launch {
            bluetoothScanner.isScanning.collect { isScanning ->
                binding.buttonScan.text = if (isScanning) "停止扫描" else "开始扫描"
                binding.progressBar.visibility = if (isScanning) 
                    android.view.View.VISIBLE else android.view.View.GONE
            }
        }
        
        // 观察扫描结果
        lifecycleScope.launch {
            bluetoothScanner.scanResults.collect { devices ->
                deviceAdapter.submitList(devices)
                binding.textViewDeviceCount.text = "找到 ${devices.size} 个设备"
            }
        }
        
        // 观察连接状态
        lifecycleScope.launch {
            connectionManager.connectionState.collect { state ->
                updateConnectionUI(state)
            }
        }
        
        // 观察连接的设备名称
        lifecycleScope.launch {
            connectionManager.connectedDeviceName.collect { name ->
                if (name.isNotEmpty()) {
                    binding.textViewConnectionStatus.text = "已连接: $name"
                }
            }
        }
    }
    
    private fun updateConnectionUI(state: BluetoothConnectionManager.ConnectionState) {
        when (state) {
            BluetoothConnectionManager.ConnectionState.DISCONNECTED -> {
                binding.textViewConnectionStatus.text = "未连接"
                binding.buttonDisconnect.isEnabled = false
                binding.buttonTestPrint.isEnabled = false
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.darker_gray)
                )
            }
            BluetoothConnectionManager.ConnectionState.CONNECTING -> {
                binding.textViewConnectionStatus.text = "连接中..."
                binding.buttonDisconnect.isEnabled = true
                binding.buttonTestPrint.isEnabled = false
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.holo_orange_light)
                )
            }
            BluetoothConnectionManager.ConnectionState.CONNECTED -> {
                binding.textViewConnectionStatus.text = "已连接 (搜索服务中...)"
                binding.buttonDisconnect.isEnabled = true
                binding.buttonTestPrint.isEnabled = false
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.holo_blue_light)
                )
            }
            BluetoothConnectionManager.ConnectionState.READY -> {
                binding.textViewConnectionStatus.text = "就绪 (可以打印)"
                binding.buttonDisconnect.isEnabled = true
                binding.buttonTestPrint.isEnabled = true
                binding.cardViewConnection.setCardBackgroundColor(
                    getColor(android.R.color.holo_green_light)
                )
            }
        }
    }
    
    private fun startScanning() {
        if (!bluetoothScanner.isBluetoothEnabled()) {
            checkBluetoothEnabled()
            return
        }
        
        if (!bluetoothScanner.checkBluetoothPermissions()) {
            requestBluetoothPermissions()
            return
        }
        
        bluetoothScanner.startScan()
        Toast.makeText(this, "开始扫描BLE设备...", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopScanning() {
        bluetoothScanner.stopScan()
        Toast.makeText(this, "停止扫描", Toast.LENGTH_SHORT).show()
    }
    
    private fun onDeviceClicked(device: BleDevice) {
        // 停止扫描
        if (bluetoothScanner.isScanning.value) {
            bluetoothScanner.stopScan()
        }
        
        // 判断是否是AQ设备
        val deviceName = device.name
        val isAQDevice = deviceName.startsWith("AQ", ignoreCase = true)
        
        if (isAQDevice) {
            // AQ设备需要检查是否已配对
            connectAQPrinterWithSDK(device)
        } else {
            // 非AQ设备使用标准BLE连接
            val message = "检测到非AQ设备,将使用标准模式连接\n设备: $deviceName"
            AlertDialog.Builder(this)
                .setTitle("连接设备")
                .setMessage(message)
                .setPositiveButton("连接") { _, _ ->
                    connectionManager.connect(device.device)
                    Toast.makeText(this, "正在连接 ${device.name}...", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }
    
    /**
     * 连接AQ打印机(使用SDK)
     * 关键: AQ打印机有两个蓝牙地址
     * - BLE地址: AQ-V258000114(BLE) - 用于发现,无法配对
     * - Classic地址: AQ-V258000114 - 用于实际连接,需要配对
     */
    private fun connectAQPrinterWithSDK(device: BleDevice) {
        android.util.Log.d("MainActivity", "========== 检测AQ打印机配对状态 ==========")
        android.util.Log.d("MainActivity", "BLE扫描到的设备: ${device.name}")
        android.util.Log.d("MainActivity", "BLE地址: ${device.device.address}")
        
        // 检查设备是否已配对
        val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        
        // 尝试查找对应的Classic Bluetooth配对设备
        // BLE名称通常是 "AQ-XXX(BLE)", Classic名称是 "AQ-XXX"
        val bleDeviceName = device.name
        val classicDeviceName = bleDeviceName.replace("(BLE)", "").trim()
        
        android.util.Log.d("MainActivity", "预期的Classic设备名: $classicDeviceName")
        
        val pairedDevice = try {
            // 先尝试精确匹配地址(可能BLE和Classic地址相同)
            val exactMatch = bluetoothAdapter?.bondedDevices?.find { 
                it.address.equals(device.device.address, ignoreCase = true) 
            }
            
            if (exactMatch != null) {
                android.util.Log.d("MainActivity", "找到精确地址匹配的配对设备: ${exactMatch.name} (${exactMatch.address})")
                exactMatch
            } else {
                // 如果地址不匹配,尝试通过名称查找Classic设备
                val nameMatch = bluetoothAdapter?.bondedDevices?.find {
                    it.name.equals(classicDeviceName, ignoreCase = true)
                }
                
                if (nameMatch != null) {
                    android.util.Log.d("MainActivity", "找到名称匹配的配对设备: ${nameMatch.name} (${nameMatch.address})")
                    android.util.Log.d("MainActivity", "注意: Classic地址 ${nameMatch.address} 与 BLE地址 ${device.device.address} 不同")
                }
                
                nameMatch
            }
        } catch (e: SecurityException) {
            android.util.Log.e("MainActivity", "检查配对状态权限异常", e)
            null
        }
        
        if (pairedDevice == null) {
            // 设备未配对,显示配对指引
            android.util.Log.w("MainActivity", "未找到配对的Classic设备,需要用户手动配对")
            showPairInstructionDialog(device, classicDeviceName)
        } else {
            // 设备已配对,使用SDK连接
            android.util.Log.d("MainActivity", "设备已配对,准备使用SDK连接")
            android.util.Log.d("MainActivity", "将使用Classic地址: ${pairedDevice.address}")
            
            val message = "检测到已配对的AQ打印机\n\n" +
                    "BLE设备: ${device.name}\n" +
                    "Classic设备: ${pairedDevice.name}\n" +
                    "连接地址: ${pairedDevice.address}\n\n" +
                    "将使用SDK模式连接"
            
            AlertDialog.Builder(this)
                .setTitle("连接设备")
                .setMessage(message)
                .setPositiveButton("连接") { _, _ ->
                    // 使用配对设备的地址(Classic地址)连接
                    connectWithSDK(device, pairedDevice.address)
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }
    
    /**
     * 显示配对指引对话框
     */
    private fun showPairInstructionDialog(device: BleDevice, classicDeviceName: String) {
        AlertDialog.Builder(this)
            .setTitle("需要配对AQ打印机")
            .setMessage(
                "此AQ打印机需要先配对才能使用SDK连接。\n\n" +
                "⚠️ 重要提示:\n" +
                "• 您扫描到的是: ${device.name}\n" +
                "• 但需要配对的是: $classicDeviceName\n" +
                "  (注意没有\"(BLE)\"后缀)\n\n" +
                "操作步骤:\n" +
                "1. 点击下方「打开设置」\n" +
                "2. 在可用设备中找到 $classicDeviceName\n" +
                "3. 点击该设备进行配对\n" +
                "4. 可能需要输入PIN码: 0000 或 1234\n" +
                "5. 配对成功后返回本APP重新连接\n\n" +
                "注: 配对只需执行一次,之后可直接连接"
            )
            .setPositiveButton("打开设置") { _, _ ->
                // 打开系统蓝牙设置
                val intent = android.content.Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(intent)
                Toast.makeText(
                    this,
                    "请配对 $classicDeviceName (不是${device.name})",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("取消", null)
            .setNeutralButton("已配对,重试") { _, _ ->
                // 用户表示已配对,重新检查
                connectAQPrinterWithSDK(device)
            }
            .show()
    }
    
    /**
     * 使用SDK连接打印机
     * @param device BLE扫描到的设备(仅用于显示)
     * @param classicAddress Classic Bluetooth的真实地址(用于SDK连接)
     */
    private fun connectWithSDK(device: BleDevice, classicAddress: String) {
        isUsingSDK = true
        
        android.util.Log.d("MainActivity", "========== 开始SDK连接 ==========")
        android.util.Log.d("MainActivity", "显示名称: ${device.name}")
        android.util.Log.d("MainActivity", "BLE地址: ${device.device.address}")
        android.util.Log.d("MainActivity", "Classic地址(实际连接): $classicAddress")
        
        // 更新UI状态
        binding.textViewConnectionStatus.text = "连接中... (SDK模式)"
        binding.cardViewConnection.setCardBackgroundColor(
            getColor(android.R.color.holo_orange_light)
        )
        
        Toast.makeText(this, "使用SDK连接 ${device.name}...", Toast.LENGTH_SHORT).show()
        
        // 使用Classic地址连接
        puquPrinterManager.connect(classicAddress)
    }
    
    private fun testPrint() {
        // 如果使用SDK模式
        if (isUsingSDK) {
            Toast.makeText(this, "使用SDK打印测试页...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                val success = puquPrinterManager.printTestPage()
                if (success) {
                    Toast.makeText(this@MainActivity, "SDK打印命令已发送", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "SDK打印失败", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        
        // 原有的标准BLE打印逻辑
        if (connectionManager.connectionState.value != BluetoothConnectionManager.ConnectionState.READY) {
            Toast.makeText(this, "打印机未就绪", Toast.LENGTH_SHORT).show()
            return
        }
        
        val deviceName = connectionManager.connectedDeviceName.value
        val isAQDevice = deviceName.startsWith("AQ", ignoreCase = true)
        
        android.util.Log.d("MainActivity", "=== Test Print ===")
        android.util.Log.d("MainActivity", "Device: $deviceName")
        android.util.Log.d("MainActivity", "Is AQ device: $isAQDevice")
        
        // 生成测试打印数据 - AQ设备使用超级简单命令
        val printData = if (isAQDevice) {
            PrinterCommands.generateAQSimpleTest()
        } else {
            PrinterCommands.generateTestPrint()
        }
        android.util.Log.d("MainActivity", "Print data size: ${printData.size} bytes")
        
        // AQ设备尝试不同策略
        if (isAQDevice) {
            Toast.makeText(this, "AQ设备使用简化命令测试...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                try {
                    android.util.Log.d("MainActivity", "Sending simple test data directly...")
                    // 直接发送,不分片
                    val success = connectionManager.sendData(printData)
                    if (success) {
                        Toast.makeText(this@MainActivity, "简化命令已发送", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "发送失败", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Error sending data", e)
                    Toast.makeText(this@MainActivity, "发送失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // 发送打印数据
            val success = connectionManager.sendData(printData)
            
            if (success) {
                Toast.makeText(this, "打印命令已发送", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "发送打印命令失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        
        val missingPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            requestMultiplePermissions.launch(missingPermissions.toTypedArray())
        } else {
            checkBluetoothEnabled()
        }
    }
    
    private fun checkBluetoothEnabled() {
        if (!bluetoothScanner.isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        bluetoothScanner.stopScan()
        connectionManager.disconnect()
        if (isUsingSDK) {
            puquPrinterManager.disconnect()
        }
    }
}
