package com.example.bleprinter.bluetooth

import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import java.util.*

class BluetoothConnectionManager(private val context: Context) {
    
    private val TAG = "BluetoothConnection"
    
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _connectedDeviceName = MutableStateFlow("")
    val connectedDeviceName: StateFlow<String> = _connectedDeviceName.asStateFlow()
    
    // MTU大小（最大传输单元）
    private var mtuSize = 23 // 默认BLE MTU，实际可用payload为20字节
    private val MAX_CHUNK_SIZE = 512 // 每次发送的最大字节数
    
    // 常用的打印机服务UUID（可能需要根据实际打印机调整）
    private val PRINTER_SERVICE_UUID = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb")
    private val PRINTER_WRITE_UUID = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb")
    
    // 备用通用串口服务UUID
    private val SERIAL_PORT_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    
    private val gattCallback = object : BluetoothGattCallback() {
        
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d(TAG, "=== Connection State Change ===")
            Log.d(TAG, "Status: $status, New State: $newState")
            Log.d(TAG, "Device: ${gatt?.device?.name} (${gatt?.device?.address})")
            
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "✓ Connected to GATT server")
                    _connectionState.value = ConnectionState.CONNECTED
                    
                    // 发现服务
                    try {
                        if (checkBluetoothPermissions()) {
                            gatt?.discoverServices()
                        }
                    } catch (e: SecurityException) {
                        Log.e(TAG, "Security exception when discovering services", e)
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server")
                    _connectionState.value = ConnectionState.DISCONNECTED
                    _connectedDeviceName.value = ""
                    bluetoothGatt = null
                    writeCharacteristic = null
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    _connectionState.value = ConnectionState.CONNECTING
                }
            }
        }
        
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.d(TAG, "=== Services Discovered ===")
            Log.d(TAG, "Status: $status (${if (status == BluetoothGatt.GATT_SUCCESS) "SUCCESS" else "FAILED"})")
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "✓ Services discovered successfully")
                Log.d(TAG, "Total services found: ${gatt?.services?.size}")
                
                // 查找写特征
                var foundWriteCharacteristics = mutableListOf<BluetoothGattCharacteristic>()
                
                gatt?.services?.forEach { service ->
                    Log.d(TAG, "─────────────────────────────")
                    Log.d(TAG, "Service UUID: ${service.uuid}")
                    Log.d(TAG, "Service Type: ${service.type}")
                    Log.d(TAG, "Characteristics count: ${service.characteristics.size}")
                    
                    service.characteristics.forEach { characteristic ->
                        val props = characteristic.properties
                        val propsStr = buildString {
                            if (props and BluetoothGattCharacteristic.PROPERTY_READ > 0) append("READ ")
                            if (props and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) append("WRITE ")
                            if (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) append("WRITE_NO_RESP ")
                            if (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) append("NOTIFY ")
                            if (props and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) append("INDICATE ")
                        }
                        
                        Log.d(TAG, "  Characteristic UUID: ${characteristic.uuid}")
                        Log.d(TAG, "  Properties: $props [$propsStr]")
                        Log.d(TAG, "  Permissions: ${characteristic.permissions}")
                        Log.d(TAG, "  Write Type: ${characteristic.writeType}")
                        
                        // 查找可写特征
                        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE > 0 ||
                            props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
                            foundWriteCharacteristics.add(characteristic)
                            Log.d(TAG, "  ★ Found WRITABLE characteristic!")
                        }
                    }
                }
                
                Log.d(TAG, "─────────────────────────────")
                Log.d(TAG, "Total writable characteristics found: ${foundWriteCharacteristics.size}")
                
                if (foundWriteCharacteristics.isNotEmpty()) {
                    // 对于AQ打印机,使用ae01特征并启用WRITE模式
                    val deviceName = gatt?.device?.name ?: ""
                    writeCharacteristic = if (deviceName.startsWith("AQ", ignoreCase = true)) {
                        Log.d(TAG, "AQ device detected, using ae01 with WRITE mode (response required)")
                        val aqChar = foundWriteCharacteristics.firstOrNull {
                            it.uuid.toString().contains("ae01", ignoreCase = true)
                        } ?: foundWriteCharacteristics.first()
                        // 强制使用WRITE模式(需要响应)
                        aqChar?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        aqChar
                    } else {
                        // 其他设备优先选择WRITE_NO_RESPONSE属性的特征
                        foundWriteCharacteristics.firstOrNull { 
                            it.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0 
                        } ?: foundWriteCharacteristics.first()
                    }
                    
                    Log.d(TAG, "✓ Selected write characteristic: ${writeCharacteristic?.uuid}")
                    Log.d(TAG, "  Properties: ${writeCharacteristic?.properties}")
                    Log.d(TAG, "  Write Type: ${writeCharacteristic?.writeType}")
                    
                    // 尝试请求更大的MTU
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            if (checkBluetoothPermissions()) {
                                Log.d(TAG, "Requesting MTU size: 512")
                                gatt?.requestMtu(512)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to request MTU", e)
                        }
                    }
                    
                    // 对于AQ设备,启用NOTIFY特征以接收打印机响应
                    if (deviceName.startsWith("AQ", ignoreCase = true)) {
                        Log.d(TAG, "AQ device: Attempting to enable NOTIFY characteristic...")
                        gatt?.services?.forEach { service ->
                            service.characteristics.forEach { characteristic ->
                                val props = characteristic.properties
                                if (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                                    Log.d(TAG, "Found NOTIFY characteristic: ${characteristic.uuid}")
                                    try {
                                        if (checkBluetoothPermissions()) {
                                            val success = gatt?.setCharacteristicNotification(characteristic, true)
                                            Log.d(TAG, "Enable NOTIFY result: $success")
                                            
                                            // 写入descriptor启用通知
                                            val descriptor = characteristic.getDescriptor(
                                                java.util.UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                                            )
                                            if (descriptor != null) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    gatt?.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                                                } else {
                                                    @Suppress("DEPRECATION")
                                                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                                    @Suppress("DEPRECATION")
                                                    gatt?.writeDescriptor(descriptor)
                                                }
                                                Log.d(TAG, "Wrote NOTIFY descriptor")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Failed to enable NOTIFY", e)
                                    }
                                }
                            }
                        }
                    }
                    
                    _connectionState.value = ConnectionState.READY
                    Log.d(TAG, "✓ Connection ready for printing")
                } else {
                    Log.w(TAG, "✗ No write characteristic found!")
                    Log.w(TAG, "Device may not support writing data")
                }
            } else {
                Log.e(TAG, "✗ Service discovery failed with status: $status")
            }
        }
        
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.d(TAG, "=== Characteristic Write Callback ===")
            Log.d(TAG, "Characteristic UUID: ${characteristic?.uuid}")
            Log.d(TAG, "Status: $status (${if (status == BluetoothGatt.GATT_SUCCESS) "SUCCESS" else "FAILED"})")
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "✓ Characteristic write successful")
            } else {
                Log.e(TAG, "✗ Characteristic write failed!")
                Log.e(TAG, "Error code: $status")
            }
        }
        
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            Log.d(TAG, "=== MTU Changed ===")
            Log.d(TAG, "MTU: $mtu, Status: $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mtuSize = mtu
                Log.d(TAG, "✓ MTU changed to: $mtu (usable payload: ${mtu - 3} bytes)")
            } else {
                Log.e(TAG, "✗ MTU change failed")
            }
        }
        
        @Deprecated("Deprecated in API 33")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            val value = characteristic?.value ?: byteArrayOf()
            Log.d(TAG, "=== Characteristic Changed (NOTIFY) ===")
            Log.d(TAG, "Characteristic UUID: ${characteristic?.uuid}")
            Log.d(TAG, "Data size: ${value.size} bytes")
            Log.d(TAG, "Data (hex): ${value.joinToString(" ") { "%02X".format(it) }}")
            
            // 解析打印机响应
            if (value.isNotEmpty()) {
                when (value[0].toInt() and 0xFF) {
                    0x10 -> Log.d(TAG, "Printer status response received")
                    0x1D -> Log.d(TAG, "Printer real-time status received")
                    else -> Log.d(TAG, "Unknown response from printer")
                }
            }
        }
    }
    
    fun connect(device: BluetoothDevice) {
        try {
            if (!checkBluetoothPermissions()) {
                Log.e(TAG, "Missing Bluetooth permissions")
                return
            }
            
            // 先断开现有连接
            disconnect()
            
            _connectionState.value = ConnectionState.CONNECTING
            _connectedDeviceName.value = device.name ?: "Unknown"
            
            // 连接到GATT服务器
            bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            } else {
                device.connectGatt(context, false, gattCallback)
            }
            
            Log.d(TAG, "Connecting to ${device.name} (${device.address})")
            
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when connecting", e)
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }
    
    fun disconnect() {
        try {
            if (checkBluetoothPermissions()) {
                bluetoothGatt?.disconnect()
                bluetoothGatt?.close()
                bluetoothGatt = null
                writeCharacteristic = null
                _connectionState.value = ConnectionState.DISCONNECTED
                _connectedDeviceName.value = ""
                Log.d(TAG, "Disconnected")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when disconnecting", e)
        }
    }
    
    fun sendData(data: ByteArray): Boolean {
        Log.d(TAG, "=== Send Data Request ===")
        Log.d(TAG, "Data size: ${data.size} bytes")
        Log.d(TAG, "Data (hex): ${data.joinToString(" ") { "%02X".format(it) }}")
        
        val characteristic = writeCharacteristic
        val gatt = bluetoothGatt
        
        if (characteristic == null) {
            Log.e(TAG, "✗ Cannot send data: no write characteristic available")
            return false
        }
        
        if (gatt == null) {
            Log.e(TAG, "✗ Cannot send data: not connected to GATT server")
            return false
        }
        
        Log.d(TAG, "Using characteristic: ${characteristic.uuid}")
        Log.d(TAG, "Characteristic properties: ${characteristic.properties}")
        Log.d(TAG, "Characteristic write type: ${characteristic.writeType}")
        
        try {
            if (!checkBluetoothPermissions()) {
                Log.e(TAG, "✗ Missing Bluetooth permissions")
                return false
            }
            
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+
                Log.d(TAG, "Using Android 13+ write API")
                val writeType = if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
                    Log.d(TAG, "Using WRITE_TYPE_NO_RESPONSE")
                    BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                } else {
                    Log.d(TAG, "Using WRITE_TYPE_DEFAULT")
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                }
                
                val writeResult = gatt.writeCharacteristic(characteristic, data, writeType)
                Log.d(TAG, "Write result: $writeResult")
                writeResult == BluetoothGatt.GATT_SUCCESS
            } else {
                // Android 12及以下
                Log.d(TAG, "Using legacy write API")
                
                // 设置写入类型
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
                    Log.d(TAG, "Setting WRITE_TYPE_NO_RESPONSE")
                    characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                } else {
                    Log.d(TAG, "Setting WRITE_TYPE_DEFAULT")
                    characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                }
                
                @Suppress("DEPRECATION")
                characteristic.value = data
                @Suppress("DEPRECATION")
                val writeResult = gatt.writeCharacteristic(characteristic)
                Log.d(TAG, "Write initiated: $writeResult")
                writeResult
            }
            
            if (result) {
                Log.d(TAG, "✓ Data send initiated successfully")
            } else {
                Log.e(TAG, "✗ Failed to initiate data send")
            }
            
            return result
            
        } catch (e: SecurityException) {
            Log.e(TAG, "✗ Security exception when sending data", e)
            return false
        } catch (e: Exception) {
            Log.e(TAG, "✗ Exception when sending data: ${e.message}", e)
            return false
        }
    }
    
    /**
     * 分片发送数据（适用于大数据量）
     * 某些打印机需要分片发送，避免数据丢失
     */
    suspend fun sendDataInChunks(data: ByteArray, chunkSize: Int = 20): Boolean {
        Log.d(TAG, "=== Send Data In Chunks ===")
        Log.d(TAG, "Total data size: ${data.size} bytes")
        Log.d(TAG, "Chunk size: $chunkSize bytes")
        Log.d(TAG, "Total chunks: ${(data.size + chunkSize - 1) / chunkSize}")
        
        if (writeCharacteristic == null || bluetoothGatt == null) {
            Log.e(TAG, "✗ Not connected")
            return false
        }
        
        var offset = 0
        var chunkIndex = 0
        
        while (offset < data.size) {
            val remaining = data.size - offset
            val currentChunkSize = minOf(chunkSize, remaining)
            val chunk = data.copyOfRange(offset, offset + currentChunkSize)
            
            Log.d(TAG, "Sending chunk ${chunkIndex + 1}: offset=$offset, size=$currentChunkSize")
            Log.d(TAG, "Chunk data (hex): ${chunk.joinToString(" ") { "%02X".format(it) }}")
            
            val success = sendData(chunk)
            if (!success) {
                Log.e(TAG, "✗ Failed to send chunk $chunkIndex")
                return false
            }
            
            // 延迟一小段时间，避免发送过快
            delay(50) // 50ms延迟
            
            offset += currentChunkSize
            chunkIndex++
        }
        
        Log.d(TAG, "✓ All chunks sent successfully")
        return true
    }
    
    private fun checkBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        READY
    }
}
