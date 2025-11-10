package com.example.bleprinter.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.bleprinter.model.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothScanner(private val context: Context) {
    
    private val TAG = "BluetoothScanner"
    
    private val bluetoothManager: BluetoothManager = 
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    
    private val _scanResults = MutableStateFlow<List<BleDevice>>(emptyList())
    val scanResults: StateFlow<List<BleDevice>> = _scanResults.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val discoveredDevices = mutableMapOf<String, BleDevice>()
    
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let { scanResult ->
                if (checkBluetoothPermissions()) {
                    val device = scanResult.device
                    val deviceName = device.name ?: "Unknown Device"
                    val deviceAddress = device.address
                    val rssi = scanResult.rssi
                    
                    // 过滤掉打印机相关设备（可选）
                    if (deviceName.isNotEmpty() && deviceName != "Unknown Device") {
                        val bleDevice = BleDevice(device, deviceName, deviceAddress, rssi)
                        discoveredDevices[deviceAddress] = bleDevice
                        _scanResults.value = discoveredDevices.values.toList()
                        
                        // 标记特殊设备
                        val devicePrefix = deviceName.take(2)
                        val marker = when {
                            devicePrefix == "GP" -> "✓ [GP device]"
                            devicePrefix == "AQ" -> "⚠ [AQ device]"
                            else -> ""
                        }
                        Log.d(TAG, "Found device: $deviceName ($deviceAddress) RSSI: $rssi $marker")
                    }
                }
            }
        }
        
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                onScanResult(0, result)
            }
        }
        
        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error code: $errorCode")
            _isScanning.value = false
        }
    }
    
    fun startScan() {
        if (!isBluetoothEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled")
            return
        }
        
        if (!checkBluetoothPermissions()) {
            Log.e(TAG, "Missing Bluetooth permissions")
            return
        }
        
        if (_isScanning.value) {
            Log.w(TAG, "Already scanning")
            return
        }
        
        discoveredDevices.clear()
        _scanResults.value = emptyList()
        
        try {
            bluetoothLeScanner?.startScan(scanCallback)
            _isScanning.value = true
            Log.d(TAG, "Started BLE scan")
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when starting scan", e)
        }
    }
    
    fun stopScan() {
        if (!_isScanning.value) {
            return
        }
        
        try {
            if (checkBluetoothPermissions()) {
                bluetoothLeScanner?.stopScan(scanCallback)
                _isScanning.value = false
                Log.d(TAG, "Stopped BLE scan")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when stopping scan", e)
        }
    }
    
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
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
