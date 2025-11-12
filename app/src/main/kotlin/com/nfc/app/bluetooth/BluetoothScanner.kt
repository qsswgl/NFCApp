package com.nfc.app.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

/**
 * 蓝牙扫描器
 * 用于扫描可用的蓝牙打印机设备
 */
class BluetoothScanner(private val context: Context) {
    
    private val TAG = "BluetoothScanner"
    
    private val bluetoothManager: BluetoothManager = 
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    
    // 扫描结果回调
    interface ScanCallback {
        fun onDeviceFound(device: BluetoothDevice, rssi: Int)
        fun onScanComplete(devices: List<BluetoothDevice>)
        fun onScanFailed(error: String)
    }
    
    private var scanCallback: ScanCallback? = null
    private val discoveredDevices = mutableMapOf<String, BluetoothDevice>()
    private var isScanning = false
    
    private val bleScanCallback = object : android.bluetooth.le.ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let { scanResult ->
                if (checkBluetoothPermissions()) {
                    val device = scanResult.device
                    val deviceName = device.name ?: return
                    val deviceAddress = device.address
                    val rssi = scanResult.rssi
                    
                    // 只关注 AQ-V 开头的打印机
                    if (deviceName.startsWith("AQ-V", ignoreCase = true)) {
                        if (!discoveredDevices.containsKey(deviceAddress)) {
                            discoveredDevices[deviceAddress] = device
                            Log.d(TAG, "发现 AQ-V 打印机: $deviceName ($deviceAddress) RSSI: $rssi")
                            scanCallback?.onDeviceFound(device, rssi)
                        }
                    }
                }
            }
        }
        
        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "扫描失败,错误码: $errorCode")
            isScanning = false
            scanCallback?.onScanFailed("扫描失败,错误码: $errorCode")
        }
    }
    
    /**
     * 开始扫描
     * @param durationMillis 扫描持续时间(毫秒)
     * @param callback 扫描回调
     */
    fun startScan(durationMillis: Long = 10000, callback: ScanCallback) {
        if (!isBluetoothEnabled()) {
            callback.onScanFailed("蓝牙未开启")
            return
        }
        
        if (!checkBluetoothPermissions()) {
            callback.onScanFailed("缺少蓝牙权限")
            return
        }
        
        if (isScanning) {
            Log.w(TAG, "已在扫描中")
            return
        }
        
        this.scanCallback = callback
        discoveredDevices.clear()
        
        try {
            bluetoothLeScanner?.startScan(bleScanCallback)
            isScanning = true
            Log.d(TAG, "开始扫描 AQ-V 打印机...")
            
            // 定时停止扫描
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                stopScan()
            }, durationMillis)
            
        } catch (e: SecurityException) {
            Log.e(TAG, "扫描权限异常", e)
            callback.onScanFailed("权限异常: ${e.message}")
        }
    }
    
    /**
     * 停止扫描
     */
    fun stopScan() {
        if (!isScanning) {
            return
        }
        
        try {
            if (checkBluetoothPermissions()) {
                bluetoothLeScanner?.stopScan(bleScanCallback)
                isScanning = false
                Log.d(TAG, "扫描结束,发现 ${discoveredDevices.size} 个 AQ-V 打印机")
                scanCallback?.onScanComplete(discoveredDevices.values.toList())
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "停止扫描权限异常", e)
        }
    }
    
    /**
     * 获取已配对的 AQ-V 打印机
     */
    fun getPairedAQPrinters(): List<BluetoothDevice> {
        if (!checkBluetoothPermissions()) {
            return emptyList()
        }
        
        return try {
            bluetoothAdapter?.bondedDevices?.filter { device ->
                !device.name.isNullOrEmpty() && 
                device.name.startsWith("AQ-V", ignoreCase = true)
            }?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            Log.e(TAG, "获取已配对设备权限异常", e)
            emptyList()
        }
    }
    
    /**
     * 检查蓝牙是否开启
     */
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    /**
     * 检查蓝牙权限
     */
    fun checkBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 11及以下
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
