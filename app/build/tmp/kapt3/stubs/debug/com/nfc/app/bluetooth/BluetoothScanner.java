package com.nfc.app.bluetooth;

import java.lang.System;

/**
 * 蓝牙扫描器
 * 用于扫描可用的蓝牙打印机设备
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001 B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0016\u001a\u00020\u0013J\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00110\u0018J\u0006\u0010\u0019\u001a\u00020\u0013J\u0018\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u0015J\u0006\u0010\u001f\u001a\u00020\u001bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/nfc/app/bluetooth/BluetoothScanner;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "TAG", "", "bleScanCallback", "Landroid/bluetooth/le/ScanCallback;", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "bluetoothLeScanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "bluetoothManager", "Landroid/bluetooth/BluetoothManager;", "discoveredDevices", "", "Landroid/bluetooth/BluetoothDevice;", "isScanning", "", "scanCallback", "Lcom/nfc/app/bluetooth/BluetoothScanner$ScanCallback;", "checkBluetoothPermissions", "getPairedAQPrinters", "", "isBluetoothEnabled", "startScan", "", "durationMillis", "", "callback", "stopScan", "ScanCallback", "app_debug"})
public final class BluetoothScanner {
    private final android.content.Context context = null;
    private final java.lang.String TAG = "BluetoothScanner";
    private final android.bluetooth.BluetoothManager bluetoothManager = null;
    private final android.bluetooth.BluetoothAdapter bluetoothAdapter = null;
    private final android.bluetooth.le.BluetoothLeScanner bluetoothLeScanner = null;
    private com.nfc.app.bluetooth.BluetoothScanner.ScanCallback scanCallback;
    private final java.util.Map<java.lang.String, android.bluetooth.BluetoothDevice> discoveredDevices = null;
    private boolean isScanning = false;
    private final android.bluetooth.le.ScanCallback bleScanCallback = null;
    
    public BluetoothScanner(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    /**
     * 开始扫描
     * @param durationMillis 扫描持续时间(毫秒)
     * @param callback 扫描回调
     */
    public final void startScan(long durationMillis, @org.jetbrains.annotations.NotNull
    com.nfc.app.bluetooth.BluetoothScanner.ScanCallback callback) {
    }
    
    /**
     * 停止扫描
     */
    public final void stopScan() {
    }
    
    /**
     * 获取已配对的 AQ-V 打印机
     */
    @org.jetbrains.annotations.NotNull
    public final java.util.List<android.bluetooth.BluetoothDevice> getPairedAQPrinters() {
        return null;
    }
    
    /**
     * 检查蓝牙是否开启
     */
    public final boolean isBluetoothEnabled() {
        return false;
    }
    
    /**
     * 检查蓝牙权限
     */
    public final boolean checkBluetoothPermissions() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0016\u0010\b\u001a\u00020\u00032\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\nH&J\u0010\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH&\u00a8\u0006\u000e"}, d2 = {"Lcom/nfc/app/bluetooth/BluetoothScanner$ScanCallback;", "", "onDeviceFound", "", "device", "Landroid/bluetooth/BluetoothDevice;", "rssi", "", "onScanComplete", "devices", "", "onScanFailed", "error", "", "app_debug"})
    public static abstract interface ScanCallback {
        
        public abstract void onDeviceFound(@org.jetbrains.annotations.NotNull
        android.bluetooth.BluetoothDevice device, int rssi);
        
        public abstract void onScanComplete(@org.jetbrains.annotations.NotNull
        java.util.List<android.bluetooth.BluetoothDevice> devices);
        
        public abstract void onScanFailed(@org.jetbrains.annotations.NotNull
        java.lang.String error);
    }
}