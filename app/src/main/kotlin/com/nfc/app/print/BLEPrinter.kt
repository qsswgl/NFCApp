package com.nfc.app.print

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.LinkedHashMap
import java.util.Locale
import java.util.UUID

class BLEPrinter(private val context: Context) {

    companion object {
        private const val TAG = "BLEPrinter"
        private const val DEFAULT_SCAN_TIMEOUT_MS = 10_000L
        private const val WRITE_ACK_TIMEOUT_MS = 900L
        private const val NO_RESPONSE_ACK_DELAY_MS = 60L
        private const val RETRY_COOLDOWN_MS = 140L
        private const val MAX_WRITE_RETRIES = 3

        private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        private val KNOWN_SERVICE_UUIDS = setOf(
            UUID.fromString("0000AE00-0000-1000-8000-00805F9B34FB"),
            UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455"),
            UUID.fromString("000018F0-0000-1000-8000-00805F9B34FB")
        )

        private val KNOWN_WRITE_UUIDS = setOf(
            UUID.fromString("0000AE01-0000-1000-8000-00805F9B34FB"),
            UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3"),
            UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616"),
            UUID.fromString("0000FF02-0000-1000-8000-00805F9B34FB")
        )

        private val KNOWN_NOTIFY_UUIDS = setOf(
            UUID.fromString("0000AE02-0000-1000-8000-00805F9B34FB"),
            UUID.fromString("49535343-ACA3-481C-91EC-D85E28A60318"),
            UUID.fromString("49535343-3D8C-46A8-AE5D-2A6F4F4C0C3F"),
            UUID.fromString("0000FF03-0000-1000-8000-00805F9B34FB")
        )

        private val ESC: Byte = 0x1B
        private val GS: Byte = 0x1D

        val INIT = byteArrayOf(ESC, 0x40)
        val ALIGN_LEFT = byteArrayOf(ESC, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(ESC, 0x61, 0x01)
        val ALIGN_RIGHT = byteArrayOf(ESC, 0x61, 0x02)
        val FONT_CONTENT = byteArrayOf(GS, 0x21, 0x11)
        val BOLD_ON = byteArrayOf(ESC, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(ESC, 0x45, 0x00)
        val LINE_FEED = byteArrayOf(0x0A)
        val CUT_PAPER = byteArrayOf(GS, 0x56, 0x00)
        const val SEPARATOR = "--------------------------------"
    }

    var onDeviceFound: ((String?, String?) -> Unit)? = null
    var onScanComplete: ((List<Pair<String, String>>) -> Unit)? = null
    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val handler = Handler(Looper.getMainLooper())

    private var bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private val discoveredDevices = LinkedHashMap<String, BluetoothDevice>()

    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null

    private val writeLock = Object()
    private var lastWriteSuccess: Boolean? = null

    private var maxPayloadSize = 20
    private var notificationsEnabled = false
    private var supportsResponseWrite = false
    private var supportsNoResponseWrite = false
    private var lastSuccessfulWriteType: Int? = null
    private var isConnected = false
    private var isScanning = false

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            handleScanResult(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { handleScanResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "❌ BLE扫描失败: errorCode=$errorCode")
            onError?.invoke("BLE扫描失败: $errorCode")
            stopScanInternal("scanFailed:$errorCode")
        }
    }

    private fun handleScanResult(result: ScanResult) {
        val device = result.device ?: return
        val address = device.address ?: return

        val existing = discoveredDevices.putIfAbsent(address, device)
        if (existing == null) {
            val name = device.name ?: "未知设备"
            Log.d(TAG, "🔍 发现BLE设备 -> name=$name, address=$address, rssi=${result.rssi}")
            onDeviceFound?.invoke(name, address)
        }
    }

    @SuppressLint("MissingPermission")
    fun scanForPrinters(scanDurationMs: Long = DEFAULT_SCAN_TIMEOUT_MS) {
        Log.d(TAG, "========== 开始扫描BLE打印机 ==========")

        if (bluetoothAdapter == null) {
            Log.e(TAG, "❌ 蓝牙适配器不可用")
            onError?.invoke("设备不支持蓝牙")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.e(TAG, "❌ 蓝牙未开启")
            onError?.invoke("请先开启蓝牙")
            return
        }

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "❌ BLE扫描器不可用")
            onError?.invoke("设备不支持BLE")
            return
        }

        if (isScanning) {
            Log.w(TAG, "⚠️ 已在扫描中，先停止当前扫描")
            stopScanInternal("restart")
        }

        discoveredDevices.clear()
        isScanning = true

        val filters: List<ScanFilter> = emptyList()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        Log.d(TAG, "→ 准备启动BLE扫描，时长=${scanDurationMs}ms")
        try {
            bluetoothLeScanner?.startScan(filters, settings, scanCallback)
            handler.postDelayed({ stopScanInternal("timeout") }, scanDurationMs)
        } catch (e: Exception) {
            Log.e(TAG, "❌ 启动BLE扫描失败: ${e.message}", e)
            onError?.invoke("BLE扫描失败: ${e.message}")
            stopScanInternal("exception")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        stopScanInternal("manual")
    }

    @SuppressLint("MissingPermission")
    private fun stopScanInternal(reason: String) {
        if (!isScanning) return

        try {
            bluetoothLeScanner?.stopScan(scanCallback)
            Log.d(TAG, "✓ 停止BLE扫描 (reason=$reason)，共发现${discoveredDevices.size}台设备")
        } catch (e: Exception) {
            Log.w(TAG, "停止扫描时出现异常: ${e.message}", e)
        }

        isScanning = false
        handler.removeCallbacksAndMessages(null)

        val snapshot = discoveredDevices.values.map { device ->
            Pair(device.name ?: "未知设备", device.address ?: "")
        }
        onScanComplete?.invoke(snapshot)
    }

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String?): Boolean {
        if (deviceAddress.isNullOrBlank()) {
            Log.e(TAG, "❌ 设备地址为空，无法连接")
            onError?.invoke("BLE地址无效")
            return false
        }

        Log.d(TAG, "========== 开始连接BLE打印机 ==========")
        Log.d(TAG, "目标地址: $deviceAddress")

        val adapter = bluetoothAdapter
        if (adapter == null) {
            Log.e(TAG, "❌ 蓝牙适配器不可用")
            onError?.invoke("设备不支持蓝牙")
            return false
        }

        if (!adapter.isEnabled) {
            Log.e(TAG, "❌ 蓝牙未开启")
            onError?.invoke("请先开启蓝牙")
            return false
        }

        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        } catch (e: Exception) {
            Log.w(TAG, "旧连接清理失败: ${e.message}")
        } finally {
            bluetoothGatt = null
            writeCharacteristic = null
            notifyCharacteristic = null
            isConnected = false
        }

        return try {
            val device = adapter.getRemoteDevice(deviceAddress)
            Log.d(TAG, "✓ 获取到远程设备: name=${device.name}, address=${device.address}, type=${device.type}, bond=${device.bondState}")

            if (isScanning) {
                stopScanInternal("connect")
            }

            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            Log.d(TAG, "→ connectGatt 已调用，等待回调")
            true
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "❌ 非法的BLE地址: ${e.message}")
            onError?.invoke("BLE地址无效")
            false
        } catch (e: Exception) {
            Log.e(TAG, "❌ 发起连接失败: ${e.message}", e)
            onError?.invoke("连接失败: ${e.message}")
            false
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "GATT状态变更: status=$status -> newState=$newState")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "✓ GATT连接成功，开始发现服务")
                    isConnected = true
                    writeCharacteristic = null
                    notifyCharacteristic = null
                    supportsResponseWrite = false
                    supportsNoResponseWrite = false
                    lastSuccessfulWriteType = null
                    bluetoothGatt = gatt
                    if (!gatt.discoverServices()) {
                        Log.w(TAG, "⚠️ discoverServices 返回false，稍后重试")
                        handler.postDelayed({ gatt.discoverServices() }, 400)
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.w(TAG, "❌ GATT连接断开，status=$status")
                    isConnected = false
                    writeCharacteristic = null
                    notifyCharacteristic = null
                    onDisconnected?.invoke()
                    try {
                        gatt.close()
                    } catch (_: Exception) {
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "onServicesDiscovered status=$status")
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "❌ 服务发现失败: $status")
                onError?.invoke("服务发现失败: $status")
                return
            }

            gatt.services?.forEach { service ->
                Log.d(TAG, "  服务 -> ${service.uuid}, 特征数量=${service.characteristics.size}")
            }

            findWriteCharacteristic(gatt)
            if (writeCharacteristic == null) {
                Log.e(TAG, "❌ 未找到可写特征，无法打印")
                onError?.invoke("未找到写入特征")
                return
            }

            optimizeConnection(gatt)
            enableNotifications(gatt)
            Log.d(TAG, "✓ BLE打印机可写特征初始化完成")
            onConnected?.invoke()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.d(TAG, "写入回调 -> uuid=${characteristic.uuid}, status=$status")
            synchronized(writeLock) {
                lastWriteSuccess = status == BluetoothGatt.GATT_SUCCESS
                writeLock.notifyAll()
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            Log.d(TAG, "描述符写入 -> uuid=${descriptor.uuid}, status=$status")
            if (descriptor.uuid == CLIENT_CHARACTERISTIC_CONFIG_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                notificationsEnabled = true
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val payload = characteristic.value ?: return
            val hex = payload.joinToString(" ") { String.format("%02X", it) }
            Log.d(TAG, "📨 收到通知 -> uuid=${characteristic.uuid}, len=${payload.size}, data=$hex")
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d(TAG, "MTU变更 -> mtu=$mtu, status=$status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val payload = (mtu - 3).coerceAtLeast(20)
                maxPayloadSize = payload
                Log.d(TAG, "✓ MTU更新成功，可用负载=$payload")
            }
        }
    }

    private fun findWriteCharacteristic(gatt: BluetoothGatt) {
        gatt.services?.forEach { service ->
            if (service.uuid in KNOWN_SERVICE_UUIDS && pickWriteCharacteristicFromService(service)) {
                return
            }
        }

        gatt.services?.forEach { service ->
            if (pickWriteCharacteristicFromService(service)) {
                return
            }
        }
    }

    private fun pickWriteCharacteristicFromService(service: BluetoothGattService): Boolean {
        service.characteristics.forEach { characteristic ->
            val props = characteristic.properties
            val writable =
                (props and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
                    (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0

            if (!writable) return@forEach

            if (writeCharacteristic == null || characteristic.uuid in KNOWN_WRITE_UUIDS) {
                writeCharacteristic = characteristic
                supportsResponseWrite = (props and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
                supportsNoResponseWrite = (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
                writeCharacteristic?.writeType = determineInitialWriteType(props)
                Log.d(
                    TAG,
                    "✓ 选定写特征 -> uuid=${characteristic.uuid}, props=0x${props.toString(16)}, supportsResponse=$supportsResponseWrite, supportsNoResp=$supportsNoResponseWrite"
                )
                captureNotifyCharacteristic(service)
                return true
            }
        }
        return false
    }

    private fun captureNotifyCharacteristic(service: BluetoothGattService) {
        if (notifyCharacteristic != null) return

        KNOWN_NOTIFY_UUIDS.forEach { known ->
            service.getCharacteristic(known)?.let { candidate ->
                val props = candidate.properties
                val hasNotify = (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
                val hasIndicate = (props and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0
                if (hasNotify || hasIndicate) {
                    notifyCharacteristic = candidate
                    notificationsEnabled = false
                    Log.d(TAG, "✓ 记录已知通知特征 -> uuid=$known")
                    return
                }
            }
        }

        service.characteristics.firstOrNull { char ->
            val props = char.properties
            (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 ||
                (props and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0
        }?.let {
            notifyCharacteristic = it
            notificationsEnabled = false
            Log.d(TAG, "✓ 记录通知特征 -> uuid=${it.uuid}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt) {
        val characteristic = notifyCharacteristic ?: run {
            Log.d(TAG, "未找到通知特征，跳过启用通知")
            return
        }

        if (notificationsEnabled) {
            Log.d(TAG, "通知已启用，跳过重复设置")
            return
        }

        val localEnable = gatt.setCharacteristicNotification(characteristic, true)
        Log.d(TAG, "设置本地通知结果: $localEnable")

        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        if (descriptor == null) {
            Log.w(TAG, "通知特征缺少CCCD描述符，无法写入远程启用")
            notificationsEnabled = localEnable
            return
        }

        val wantsIndicate = (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0
        descriptor.value = if (wantsIndicate) {
            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        } else {
            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }

        val result = gatt.writeDescriptor(descriptor)
        Log.d(TAG, "发送CCCD写入请求 -> $result")
    }

    @SuppressLint("MissingPermission")
    private fun optimizeConnection(gatt: BluetoothGatt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val priorityOk = gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
            Log.d(TAG, "请求高优先级连接: $priorityOk")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val targetMtu = 185
            val mtuOk = gatt.requestMtu(targetMtu)
            Log.d(TAG, "请求MTU=$targetMtu -> $mtuOk")
        }
    }

    private fun determineInitialWriteType(properties: Int): Int {
        return when {
            (properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ->
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0 ->
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            else -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        }
    }

    private fun preferredWriteModes(): List<Int> {
        val modes = mutableListOf<Int>()
        lastSuccessfulWriteType?.let { modes.add(it) }
        if (supportsResponseWrite) modes.add(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        if (supportsNoResponseWrite) modes.add(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
        if (modes.isEmpty()) modes.add(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        return modes.distinct()
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        Log.d(TAG, "执行BLE断开")
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        } catch (e: Exception) {
            Log.w(TAG, "断开过程中出现异常: ${e.message}")
        } finally {
            bluetoothGatt = null
            writeCharacteristic = null
            notifyCharacteristic = null
            notificationsEnabled = false
            supportsResponseWrite = false
            supportsNoResponseWrite = false
            lastSuccessfulWriteType = null
            maxPayloadSize = 20
            isConnected = false
        }
    }

    fun isConnected(): Boolean = isConnected && bluetoothGatt != null && writeCharacteristic != null

    @SuppressLint("MissingPermission")
    fun writeData(data: ByteArray): Boolean {
        val gatt = bluetoothGatt
        val characteristic = writeCharacteristic

        if (gatt == null || characteristic == null) {
            Log.e(TAG, "❌ 写入失败: GATT或特征未就绪 (gatt=${gatt != null}, char=${characteristic != null})")
            return false
        }

        var offset = 0
        val total = data.size

        while (offset < total) {
            val chunkSize = minOf(maxPayloadSize, total - offset)
            val chunk = data.copyOfRange(offset, offset + chunkSize)
            Log.v(TAG, "→ 准备写入分包 offset=$offset size=$chunkSize/$total")

            var chunkSent = false
            val modes = preferredWriteModes()

            for (mode in modes) {
                if (characteristic.writeType != mode) {
                    characteristic.writeType = mode
                    Log.d(TAG, "切换写入模式 -> ${writeTypeToString(mode)}")
                }

                var attempt = 0
                while (attempt < MAX_WRITE_RETRIES && !chunkSent) {
                    attempt++
                    val startResult: Boolean
                    synchronized(writeLock) {
                        characteristic.value = chunk
                        lastWriteSuccess = null
                        startResult = gatt.writeCharacteristic(characteristic)
                        if (!startResult) {
                            Log.w(TAG, "⚠️ 写入启动失败(${writeTypeToString(mode)})，第${attempt}次")
                        } else if (mode == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
                            Thread.sleep(NO_RESPONSE_ACK_DELAY_MS)
                            lastWriteSuccess = true
                        } else {
                            writeLock.wait(WRITE_ACK_TIMEOUT_MS)
                        }
                    }

                    val success = startResult && lastWriteSuccess == true
                    if (success) {
                        chunkSent = true
                        lastSuccessfulWriteType = mode
                        Log.v(TAG, "✓ 分包发送成功 (${offset + chunkSize}/$total)")
                    } else {
                        Log.w(
                            TAG,
                            "⚠️ 分包写入失败，mode=${writeTypeToString(mode)} attempt=$attempt/$MAX_WRITE_RETRIES"
                        )
                        Thread.sleep(RETRY_COOLDOWN_MS)
                    }
                }

                if (chunkSent) break
            }

            if (!chunkSent) {
                Log.e(TAG, "❌ 分包发送失败(offset=$offset size=$chunkSize)，终止写入")
                return false
            }

            offset += chunkSize
        }

        Log.d(TAG, "✓ 数据写入完成，共${data.size}字节")
        return true
    }

    fun printReceipt(
        cardNumber: String,
        carNumber: String,
        unitName: String,
        deviceName: String,
        amount: String
    ): Boolean {
        if (!isConnected()) {
            Log.e(TAG, "❌ 打印机未连接，无法打印")
            onError?.invoke("打印机未连接")
            return false
        }

        Log.d(TAG, "========== 开始BLE小票打印 ==========")

        if (!writeData(INIT)) {
            Log.e(TAG, "❌ 初始化打印机失败")
            onError?.invoke("初始化打印机失败")
            return false
        }

        Thread.sleep(120)

        val payload = buildReceiptBody(cardNumber, carNumber, unitName, deviceName, amount)
        val success = writeData(payload)
        if (success) {
            Thread.sleep(200)
            Log.d(TAG, "✓ 小票发送完成，payload=${payload.size}字节")
        } else {
            Log.e(TAG, "❌ 小票发送失败")
            onError?.invoke("小票发送失败")
        }
        return success
    }

    private fun buildReceiptBody(
        cardNumber: String,
        carNumber: String,
        unitName: String,
        deviceName: String,
        amount: String
    ): ByteArray {
        val buffer = ByteArrayOutputStream()
        fun add(vararg pieces: ByteArray) = pieces.forEach { buffer.write(it) }

        val gbk = Charset.forName("GBK")
        val utf8 = Charsets.UTF_8

        add(ALIGN_CENTER, FONT_CONTENT, BOLD_ON)
        add("消费小票".toByteArray(gbk), LINE_FEED, LINE_FEED)

        add(BOLD_OFF, ALIGN_LEFT)
        add(SEPARATOR.toByteArray(utf8), LINE_FEED)

        add("卡号: ".toByteArray(gbk), cardNumber.toByteArray(utf8), LINE_FEED, LINE_FEED)
        add("机号: ".toByteArray(gbk), carNumber.toByteArray(utf8), LINE_FEED, LINE_FEED)

        if (unitName.isNotEmpty()) {
            add("单位: ".toByteArray(gbk), unitName.toByteArray(gbk), LINE_FEED, LINE_FEED)
        }

        if (deviceName.isNotEmpty()) {
            add("设备: ".toByteArray(gbk), deviceName.toByteArray(gbk), LINE_FEED, LINE_FEED)
        }

        add(BOLD_ON)
        add("消费金额: ".toByteArray(gbk), amount.toByteArray(utf8), " 元".toByteArray(gbk))
        add(BOLD_OFF, LINE_FEED, LINE_FEED)

        add(SEPARATOR.toByteArray(utf8), LINE_FEED)

        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(java.util.Date())
        add(ALIGN_CENTER, currentTime.toByteArray(utf8), LINE_FEED, LINE_FEED)

        add("联系电话: 138-0000-0000".toByteArray(gbk), LINE_FEED)
        add(LINE_FEED)
        add("谢谢使用!".toByteArray(gbk), LINE_FEED, LINE_FEED, LINE_FEED)
        add(CUT_PAPER)

        return buffer.toByteArray()
    }

    private fun writeTypeToString(type: Int): String = when (type) {
        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT -> "DEFAULT"
        BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE -> "NO_RESPONSE"
        BluetoothGattCharacteristic.WRITE_TYPE_SIGNED -> "SIGNED"
        else -> type.toString()
    }
}
