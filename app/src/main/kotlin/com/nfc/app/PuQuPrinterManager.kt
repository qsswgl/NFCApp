package com.nfc.app

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.puqu.sdk.PuQuPrintManager
import com.puqu.sdk.Base.PuQuPrint
import com.nfc.app.bluetooth.BluetoothScanner
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 基于 PUQU SDK 的打印机管理类
 * 支持 AV 系列打印机 (需要先配对 Classic Bluetooth)
 * 
 * AV 打印机工作原理:
 * 1. BLE 扫描: 发现 AQ-V258000114(BLE) - 地址 CD:AC:B0:F0:31:FC
 * 2. Classic 配对: 必须配对 AQ-V258000114 (无BLE后缀) - 地址 CD:AC:B0:07:00:72
 * 3. SDK 连接: 使用 Classic 地址 (CD:AC:B0:07:00:72) 连接
 * 4. 打印: startJob -> addText -> printJob (后台线程)
 */
class PuQuPrinterManager(private val context: Context) {
    
    private var printManager: PuQuPrintManager? = null
    private var isConnected = false
    private var connectedAddress: String? = null
    private val bluetoothScanner = BluetoothScanner(context)
    
    // 回调接口
    interface PrinterCallback {
        fun onConnecting(printerName: String)
        fun onConnected(printerName: String)
        fun onDisconnected()
        fun onPrintStart()
        fun onPrintSuccess()
        fun onPrintFailed(error: String)
    }
    
    private var callback: PrinterCallback? = null
    
    fun setCallback(callback: PrinterCallback?) {
        this.callback = callback
    }
    
    /**
     * 初始化 PuQuPrintManager
     */
    fun initialize() {
        if (printManager == null) {
            printManager = PuQuPrintManager(context.applicationContext)
            
            // 连接成功回调
            printManager?.setConnectSuccess(object : PuQuPrint.ConnectSuccess {
                override fun success() {
                    isConnected = true
                    val name = connectedAddress ?: "未知打印机"
                    Log.d(TAG, "✓ 打印机连接成功: $name")
                    callback?.onConnected(name)
                }
            })
            
            // 连接失败回调
            printManager?.setConnectFailed(object : PuQuPrint.ConnectFailed {
                override fun failed() {
                    isConnected = false
                    Log.e(TAG, "❌ 打印机连接失败")
                    callback?.onDisconnected()
                }
            })
            
            // 连接关闭回调
            printManager?.setConnectClosed(object : PuQuPrint.ConnectClosed {
                override fun closed() {
                    isConnected = false
                    connectedAddress = null
                    Log.d(TAG, "打印机连接已关闭")
                    callback?.onDisconnected()
                }
            })
            
            Log.d(TAG, "PuQuPrintManager 初始化完成")
        }
    }
    
    /**
     * 检查蓝牙状态
     */
    fun checkBluetoothStatus(): String? {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        
        if (btAdapter == null) {
            return "设备不支持蓝牙"
        }
        
        if (!btAdapter.isEnabled) {
            return "请先开启蓝牙"
        }
        
        return null // 蓝牙正常
    }
    
    /**
     * 获取所有已配对的打印机设备
     * 优先返回 AQ-V 开头的打印机 (AV系列)
     */
    fun getAllPrinters(): List<BluetoothDevice> {
        val btAdapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        
        return try {
            val allDevices = btAdapter.bondedDevices.filter { 
                !it.name.isNullOrEmpty() 
            }.toList()
            
            // 优先查找 AQ-V 开头的打印机 (AV系列)
            val avPrinters = allDevices.filter { device ->
                device.name.startsWith("AQ-V", ignoreCase = true)
            }
            
            if (avPrinters.isNotEmpty()) {
                Log.d(TAG, "✓ 找到 ${avPrinters.size} 个 AQ-V 系列打印机")
                avPrinters.forEach { device ->
                    Log.d(TAG, "  - ${device.name} (${device.address})")
                }
                return avPrinters
            }
            
            // 如果没有 AQ-V,再查找其他打印机关键词
            val printerKeywords = listOf("printer", "print", "打印", "AV", "AQ", "GP-", "XP-")
            val printers = allDevices.filter { device ->
                printerKeywords.any { keyword -> 
                    device.name.contains(keyword, ignoreCase = true) 
                }
            }
            
            // 记录所有配对设备
            Log.d(TAG, "找到 ${printers.size} 个打印机设备, ${allDevices.size - printers.size} 个其他设备")
            printers.forEach { device ->
                Log.d(TAG, "打印机: ${device.name} (${device.address})")
            }
            
            // 优先返回打印机,如果没有则返回所有设备
            printers.ifEmpty { allDevices }
        } catch (e: Exception) {
            Log.e(TAG, "获取配对设备失败", e)
            emptyList()
        }
    }
    
    /**
     * 获取已配对的 AQ-V 打印机
     * 用于用户选择打印机
     */
    fun getPairedPrinters(): List<BluetoothDevice> {
        val btAdapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        
        return try {
            val pairedDevices = btAdapter.bondedDevices?.filter { device ->
                !device.name.isNullOrEmpty() && 
                device.name.startsWith("AQ-V", ignoreCase = true)
            }?.toList() ?: emptyList()
            
            Log.d(TAG, "========== 已配对的 AQ-V 打印机 ==========")
            if (pairedDevices.isEmpty()) {
                Log.w(TAG, "未找到已配对的 AQ-V 打印机")
            } else {
                Log.d(TAG, "找到 ${pairedDevices.size} 个已配对的打印机:")
                pairedDevices.forEachIndexed { index, device ->
                    Log.d(TAG, "  [${index + 1}] ${device.name} - ${device.address}")
                }
            }
            Log.d(TAG, "========================================")
            
            pairedDevices
        } catch (e: SecurityException) {
            Log.e(TAG, "获取已配对设备权限异常", e)
            emptyList()
        }
    }
    
    /**
     * 扫描并查找 AQ-V 打印机
     * @return 已配对的 AQ-V 打印机列表
     */
    suspend fun scanAndGetAQPrinters(): List<BluetoothDevice> = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "========== 开始扫描 AQ-V 打印机 ==========")
        
        // 检查蓝牙状态
        if (!bluetoothScanner.isBluetoothEnabled()) {
            Log.e(TAG, "蓝牙未开启")
            continuation.resume(emptyList())
            return@suspendCancellableCoroutine
        }
        
        if (!bluetoothScanner.checkBluetoothPermissions()) {
            Log.e(TAG, "缺少蓝牙权限")
            continuation.resume(emptyList())
            return@suspendCancellableCoroutine
        }
        
        // 先获取已配对的打印机
        val pairedPrinters = bluetoothScanner.getPairedAQPrinters()
        if (pairedPrinters.isNotEmpty()) {
            Log.d(TAG, "✓ 找到 ${pairedPrinters.size} 个已配对的 AQ-V 打印机:")
            pairedPrinters.forEach { device ->
                Log.d(TAG, "  - ${device.name} (${device.address})")
            }
            continuation.resume(pairedPrinters)
            return@suspendCancellableCoroutine
        }
        
        // 如果没有已配对的,进行BLE扫描
        Log.d(TAG, "未找到已配对的 AQ-V 打印机,开始 BLE 扫描...")
        bluetoothScanner.startScan(10000, object : BluetoothScanner.ScanCallback {
            override fun onDeviceFound(device: BluetoothDevice, rssi: Int) {
                Log.d(TAG, "扫描到: ${device.name} (${device.address}) RSSI: $rssi")
            }
            
            override fun onScanComplete(devices: List<BluetoothDevice>) {
                Log.d(TAG, "扫描完成,发现 ${devices.size} 个 AQ-V 打印机")
                
                // 检查这些设备是否已配对
                val pairedDevices = devices.filter { device ->
                    isDevicePaired(device.address)
                }
                
                if (pairedDevices.isEmpty() && devices.isNotEmpty()) {
                    Log.w(TAG, "发现打印机但未配对,需要先在系统设置中配对")
                }
                
                continuation.resume(pairedDevices)
            }
            
            override fun onScanFailed(error: String) {
                Log.e(TAG, "扫描失败: $error")
                continuation.resume(emptyList())
            }
        })
    }
    
    /**
     * 检查设备是否已配对
     */
    private fun isDevicePaired(address: String): Boolean {
        val btAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
        return try {
            btAdapter.bondedDevices?.any { it.address == address } ?: false
        } catch (e: SecurityException) {
            false
        }
    }
    
    /**
     * 根据 BLE 扫描设备查找对应的 Classic 配对设备
     * 
     * AV 打印机有两个地址:
     * - BLE 地址: CD:AC:B0:F0:31:FC (用于扫描发现)
     * - Classic 地址: CD:AC:B0:07:00:72 (用于配对和打印)
     * 
     * @param bleDeviceName BLE 扫描到的设备名称,例如 "AQ-V258000114(BLE)"
     * @param bleAddress BLE 地址
     * @return Classic 设备的蓝牙地址,如果未配对则返回 null
     */
    fun findClassicAddressForAVPrinter(bleDeviceName: String, bleAddress: String): String? {
        Log.d(TAG, "========== 查找 AV 打印机 Classic 地址 ==========")
        Log.d(TAG, "BLE 设备名: $bleDeviceName")
        Log.d(TAG, "BLE 地址: $bleAddress")
        
        val btAdapter = BluetoothAdapter.getDefaultAdapter() ?: return null
        
        // AV 打印机名称规则: BLE 名称有 "(BLE)" 后缀,Classic 名称没有
        val classicDeviceName = bleDeviceName.replace("(BLE)", "").trim()
        Log.d(TAG, "预期 Classic 设备名: $classicDeviceName")
        
        return try {
            // 1. 先尝试精确匹配地址 (某些打印机 BLE 和 Classic 地址相同)
            val exactMatch = btAdapter.bondedDevices?.find { 
                it.address.equals(bleAddress, ignoreCase = true) 
            }
            
            if (exactMatch != null) {
                Log.d(TAG, "✓ 找到地址匹配: ${exactMatch.name} (${exactMatch.address})")
                return exactMatch.address
            }
            
            // 2. 通过名称查找 Classic 设备
            val nameMatch = btAdapter.bondedDevices?.find {
                it.name.equals(classicDeviceName, ignoreCase = true)
            }
            
            if (nameMatch != null) {
                Log.d(TAG, "✓ 找到名称匹配: ${nameMatch.name} (${nameMatch.address})")
                Log.d(TAG, "注意: Classic 地址 ${nameMatch.address} ≠ BLE 地址 $bleAddress")
                return nameMatch.address
            }
            
            // 3. 未找到配对设备
            Log.w(TAG, "❌ 未找到配对的 Classic 设备")
            Log.w(TAG, "需要在系统设置中配对: $classicDeviceName")
            null
        } catch (e: SecurityException) {
            Log.e(TAG, "检查配对状态权限异常", e)
            null
        }
    }
    
    /**
     * 自动连接第一个可用的打印机
     */
    suspend fun autoConnect(): Boolean = suspendCancellableCoroutine { continuation ->
        try {
            // 检查蓝牙状态
            val error = checkBluetoothStatus()
            if (error != null) {
                callback?.onPrintFailed(error)
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            
            // 如果已连接，直接返回成功
            if (isConnected) {
                Log.d(TAG, "打印机已连接")
                continuation.resume(true)
                return@suspendCancellableCoroutine
            }
            
            // 获取所有已配对的打印机
            val printers = getAllPrinters()
            
            if (printers.isEmpty()) {
                Log.e(TAG, "❌ 未找到已配对的打印机")
                callback?.onPrintFailed("未找到已配对的打印机，请先在系统设置中配对 AQ-V258000114")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            
            Log.d(TAG, "✓ 找到 ${printers.size} 个已配对的打印机:")
            printers.forEachIndexed { index, device ->
                Log.d(TAG, "  [$index] ${device.name} - ${device.address}")
            }
            
            // 连接第一个打印机
            val printer = printers[0]
            val address = printer.address
            
            Log.d(TAG, "========== 开始连接打印机 ==========")
            Log.d(TAG, "打印机名称: ${printer.name}")
            Log.d(TAG, "打印机地址: $address")
            Log.d(TAG, "======================================")
            callback?.onConnecting(printer.name)
            
            // 使用 PUQU SDK 连接打印机
            printManager?.openPrinter(address)
            
            // 等待连接回调
            continuation.resume(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "自动连接失败", e)
            callback?.onPrintFailed("连接异常: ${e.message}")
            continuation.resume(false)
        }
    }
    
    /**
     * 连接指定的打印机
     * @param address Classic Bluetooth 地址 (不是 BLE 地址!)
     */
    fun connectToPrinter(address: String) {
        try {
            Log.d(TAG, "========== SDK连接请求 ==========")
            Log.d(TAG, "目标地址: $address")
            
            // 检查是否已连接
            if (isConnected && connectedAddress == address) {
                Log.w(TAG, "已连接到此设备,无需重复连接")
                return
            }
            
            // 断开旧连接
            if (isConnected) {
                Log.w(TAG, "断开旧连接...")
                printManager?.closePrinter()
                Thread.sleep(500)
            }
            
            connectedAddress = address
            Log.d(TAG, "调用 SDK openPrinter...")
            printManager?.openPrinter(address)
            Log.d(TAG, "SDK 调用完成,等待回调 (约6秒)...")
            
        } catch (e: Exception) {
            Log.e(TAG, "连接打印机失败", e)
            callback?.onPrintFailed("连接失败: ${e.message}")
        }
    }
    
    /**
     * 打印小票 - 使用 PUQU SDK API
     * 关键流程: startJob -> addText/setFontSize -> printJob (后台线程)
     */
    suspend fun autoPrintReceipt(
        cardNumber: String,
        carNumber: String,
        unitName: String,
        deviceName: String,
        amount: String,
        readTime: Long
    ): Boolean {
        return try {
            Log.d(TAG, "========== 开始自动打印 ==========")
            Log.d(TAG, "卡号: $cardNumber, 车号: $carNumber, 金额: $amount")
            
            // 1. 扫描并查找 AQ-V 打印机
            Log.d(TAG, "步骤1: 扫描 AQ-V 系列打印机...")
            val printers = scanAndGetAQPrinters()
            
            if (printers.isEmpty()) {
                Log.e(TAG, "❌ 未找到已配对的 AQ-V 打印机")
                callback?.onPrintFailed("未找到 AQ-V 打印机!\n\n请确保:\n• 打印机已开机\n• 已在系统设置中配对 AQ-V 打印机\n• 打印机名称以 AQ-V 开头")
                return false
            }
            
            // 选择第一个 AQ-V 打印机 (如果有多个,选择第一个)
            val targetPrinter = printers.first()
            
            Log.d(TAG, "========== 选中打印机 ==========")
            Log.d(TAG, "打印机名称: ${targetPrinter.name}")
            Log.d(TAG, "打印机地址: ${targetPrinter.address}")
            if (printers.size > 1) {
                Log.d(TAG, "注意: 发现 ${printers.size} 个打印机,已选择第一个")
                printers.drop(1).forEach { p ->
                    Log.d(TAG, "  其他: ${p.name} (${p.address})")
                }
            }
            Log.d(TAG, "======================================")
            Log.d(TAG, "打印机地址: ${targetPrinter.address}")
            Log.d(TAG, "======================================")
            
            // 如果是不同的打印机,先断开旧连接
            if (isConnected && connectedAddress != targetPrinter.address) {
                Log.d(TAG, "检测到更换打印机,断开旧连接: $connectedAddress")
                printManager?.closePrinter()
                isConnected = false
                connectedAddress = null
                kotlinx.coroutines.delay(1000)
            }
            
            // 连接打印机
            if (!isConnected || connectedAddress != targetPrinter.address) {
                callback?.onConnecting(targetPrinter.name)
                connectToPrinter(targetPrinter.address)
                
                // 等待连接成功 (最多20秒)
                Log.d(TAG, "步骤2: 等待连接成功...")
                var waitTime = 0
                while (!isConnected && waitTime < 20000) {
                    kotlinx.coroutines.delay(1000)
                    waitTime += 1000
                    if (waitTime % 5000 == 0) {
                        Log.d(TAG, "等待连接中... ${waitTime/1000}秒")
                    }
                }
                
                if (!isConnected) {
                    Log.e(TAG, "❌ 连接超时 (20秒)")
                    Log.e(TAG, "可能原因:")
                    Log.e(TAG, "  1. 打印机未开机")
                    Log.e(TAG, "  2. 打印机蓝牙未开启")
                    Log.e(TAG, "  3. 打印机未配对")
                    Log.e(TAG, "  4. 打印机距离太远")
                    callback?.onPrintFailed("连接超时!\n请检查:\n• 打印机是否开机\n• 蓝牙是否开启\n• 是否已在系统设置中配对")
                    return false
                }
            }
            
            Log.d(TAG, "✓ 打印机已连接")
            
            // 2. 执行打印
            Log.d(TAG, "步骤3: 开始打印...")
            callback?.onPrintStart()
            
            val result = printReceiptContent(
                cardNumber,
                carNumber,
                unitName,
                deviceName,
                amount,
                readTime
            )
            
            if (result) {
                Log.d(TAG, "✓✓✓ 打印成功")
                callback?.onPrintSuccess()
            } else {
                Log.e(TAG, "❌ 打印失败")
                callback?.onPrintFailed("打印失败")
            }
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ 打印异常", e)
            callback?.onPrintFailed("打印异常: ${e.message}")
            false
        }
    }
    
    /**
     * 打印到指定地址的打印机
     * 用于用户选择打印机后打印
     */
    suspend fun printToAddress(
        printerAddress: String,
        cardNumber: String,
        carNumber: String,
        unitName: String,
        deviceName: String,
        amount: String,
        readTime: Long
    ): Boolean {
        return try {
            Log.d(TAG, "========== 打印到指定打印机 ==========")
            Log.d(TAG, "打印机地址: $printerAddress")
            Log.d(TAG, "卡号: $cardNumber, 车号: $carNumber, 金额: $amount")
            
            // 检查蓝牙状态
            val error = checkBluetoothStatus()
            if (error != null) {
                callback?.onPrintFailed(error)
                return false
            }
            
            // 如果是不同的打印机,先断开旧连接
            if (isConnected && connectedAddress != printerAddress) {
                Log.d(TAG, "检测到更换打印机,断开旧连接: $connectedAddress")
                printManager?.closePrinter()
                isConnected = false
                connectedAddress = null
                kotlinx.coroutines.delay(1000)
            }
            
            // 连接打印机
            if (!isConnected || connectedAddress != printerAddress) {
                Log.d(TAG, "连接打印机: $printerAddress")
                connectToPrinter(printerAddress)
                
                // 等待连接成功 (最多20秒)
                Log.d(TAG, "等待连接...")
                var waitTime = 0
                while (!isConnected && waitTime < 20000) {
                    kotlinx.coroutines.delay(1000)
                    waitTime += 1000
                    if (waitTime % 5000 == 0) {
                        Log.d(TAG, "等待连接中... ${waitTime/1000}秒")
                    }
                }
                
                if (!isConnected) {
                    Log.e(TAG, "❌ 连接超时 (20秒)")
                    callback?.onPrintFailed("连接超时!\n请检查:\n• 打印机是否开机\n• 蓝牙是否开启")
                    return false
                }
            }
            
            Log.d(TAG, "✓ 打印机已连接")
            
            // 执行打印
            Log.d(TAG, "开始打印...")
            callback?.onPrintStart()
            
            val result = printReceiptContent(
                cardNumber,
                carNumber,
                unitName,
                deviceName,
                amount,
                readTime
            )
            
            if (result) {
                Log.d(TAG, "✓✓✓ 打印成功")
                callback?.onPrintSuccess()
            } else {
                Log.e(TAG, "❌ 打印失败")
                callback?.onPrintFailed("打印失败")
            }
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ 打印异常", e)
            callback?.onPrintFailed("打印异常: ${e.message}")
            false
        }
    }
    
    /**
     * 打印小票内容 - 使用 SDK API
     * 关键: 每次打印都需要 startJob -> addText -> printJob
     */
    private fun printReceiptContent(
        cardNumber: String,
        carNumber: String,
        unitName: String,
        deviceName: String,
        amount: String,
        readTime: Long
    ): Boolean {
        val currentManager = printManager ?: return false
        
        return try {
            Log.d(TAG, "========== 开始生成打印内容 ==========")
            
            val dateStr = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date(readTime))
            
            // 步骤1: 开始打印任务
            Log.d(TAG, "步骤1: startJob(400, -1)")
            currentManager.startJob(400, -1)
            
            // 保留顶部1cm空白 (约40像素)
            currentManager.addBlank(40)
            
            // 步骤2: 标题 - 居中,粗体,大字体
            Log.d(TAG, "步骤2: 打印标题")
            currentManager.setLeft(0)
            currentManager.setFontSize(36f)
            currentManager.setAlignment(1)  // 1=居中
            currentManager.setBold(true)
            currentManager.addText("===== 缴费小票 =====")
            currentManager.addBlank(16)
            
            // 步骤3: 内容 - 左对齐,字号增大50% (24f → 36f)
            Log.d(TAG, "步骤3: 打印内容")
            currentManager.setLeft(16)
            currentManager.setBold(false)
            currentManager.setFontSize(36f)  // 字号增大50%: 24f → 36f
            currentManager.setAlignment(0)  // 0=左对齐
            
            currentManager.addText("卡号: $cardNumber")
            currentManager.addText("车号: $carNumber")
            
            if (unitName.isNotEmpty()) {
                currentManager.addText("单位: $unitName")
            }
            
            if (deviceName.isNotEmpty()) {
                currentManager.addText("设备: $deviceName")
            }
            
            // 金额 - 加粗
            currentManager.setBold(true)
            currentManager.addText("金额: $amount 元")
            currentManager.setBold(false)
            
            currentManager.addBlank(8)
            currentManager.addText("时间: $dateStr")
            currentManager.addBlank(24)
            
            // 步骤4: 底部 - 居中
            currentManager.setAlignment(1)
            currentManager.addText("- - - - - - - - - - - - - - - -")
            currentManager.addText("谢谢惠顾!")
            currentManager.addBlank(24)
            
            // 客户签字
            currentManager.setAlignment(0)  // 左对齐
            currentManager.setFontSize(32f)
            currentManager.addText("客户签字：___________________")
            currentManager.addBlank(80)
            
            // 步骤5: 在后台线程执行打印
            Log.d(TAG, "步骤5: 后台线程执行 printJob()")
            Thread {
                try {
                    Log.d(TAG, "后台线程: 开始 printJob()...")
                    currentManager.printJob()
                    Log.d(TAG, "后台线程: printJob() 完成")
                } catch (e: Exception) {
                    Log.e(TAG, "后台线程: printJob() 异常", e)
                }
            }.start()
            
            Log.d(TAG, "========== 打印命令已发送 ==========")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ 打印内容生成失败", e)
            false
        }
    }
    
    /**
     * 打印测试文本
     */
    fun printTestText(text: String): Boolean {
        val currentManager = printManager ?: return false
        
        return try {
            callback?.onPrintStart()
            
            val printData = ByteArrayOutputStream()
            // 初始化
            printData.write(byteArrayOf(0x1B, 0x40))
            // 居中
            printData.write(byteArrayOf(0x1B, 0x61, 0x01))
            // 加粗
            printData.write(byteArrayOf(0x1B, 0x45, 0x01))
            printData.write(text.toByteArray(Charsets.UTF_8))
            printData.write("\n\n\n".toByteArray(Charsets.UTF_8))
            
            // 尝试发送
            try {
                currentManager.javaClass.getMethod("sendDataByte", ByteArray::class.java)
                    .invoke(currentManager, printData.toByteArray())
            } catch (e1: Exception) {
                currentManager.javaClass.getMethod("writeBytes", ByteArray::class.java)
                    .invoke(currentManager, printData.toByteArray())
            }
            
            Log.d(TAG, "测试打印已发送")
            true
        } catch (e: Exception) {
            Log.e(TAG, "测试打印失败", e)
            callback?.onPrintFailed("打印异常: ${e.message}")
            false
        }
    }
    
    /**
     * 检查打印机是否已连接
     */
    fun isPrinterConnected(): Boolean {
        return isConnected && printManager?.isConnected() == true
    }
    
    /**
     * 断开连接
     */
    fun disconnect() {
        try {
            printManager?.closePrinter()
            isConnected = false
            Log.d(TAG, "打印机已断开")
        } catch (e: Exception) {
            Log.e(TAG, "断开打印机失败", e)
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        try {
            disconnect()
            printManager = null
            isConnected = false
            Log.d(TAG, "PuQuPrintManager 资源已释放")
        } catch (e: Exception) {
            Log.e(TAG, "释放资源失败", e)
        }
    }
    
    companion object {
        private const val TAG = "PuQuPrinterManager"
    }
}
