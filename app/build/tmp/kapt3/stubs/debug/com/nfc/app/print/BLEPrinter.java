package com.nfc.app.print;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u00b0\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\t\u0018\u0000 f2\u00020\u0001:\u0001fB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J0\u0010C\u001a\u00020D2\u0006\u0010E\u001a\u00020\u000f2\u0006\u0010F\u001a\u00020\u000f2\u0006\u0010G\u001a\u00020\u000f2\u0006\u0010H\u001a\u00020\u000f2\u0006\u0010I\u001a\u00020\u000fH\u0002J\u0010\u0010J\u001a\u00020#2\u0006\u0010K\u001a\u00020LH\u0002J\u0012\u0010M\u001a\u00020\u00162\b\u0010N\u001a\u0004\u0018\u00010\u000fH\u0007J\u0010\u0010O\u001a\u00020\u00192\u0006\u0010P\u001a\u00020\u0019H\u0002J\b\u0010Q\u001a\u00020#H\u0007J\u0010\u0010R\u001a\u00020#2\u0006\u0010S\u001a\u00020\bH\u0003J\u0010\u0010T\u001a\u00020#2\u0006\u0010S\u001a\u00020\bH\u0002J\u0010\u0010U\u001a\u00020#2\u0006\u0010V\u001a\u00020WH\u0002J\u0006\u0010\u0015\u001a\u00020\u0016J\u0010\u0010X\u001a\u00020#2\u0006\u0010S\u001a\u00020\bH\u0003J\u0010\u0010Y\u001a\u00020\u00162\u0006\u0010K\u001a\u00020LH\u0002J\u000e\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u001908H\u0002J.\u0010[\u001a\u00020\u00162\u0006\u0010E\u001a\u00020\u000f2\u0006\u0010F\u001a\u00020\u000f2\u0006\u0010G\u001a\u00020\u000f2\u0006\u0010H\u001a\u00020\u000f2\u0006\u0010I\u001a\u00020\u000fJ\u0012\u0010\\\u001a\u00020#2\b\b\u0002\u0010]\u001a\u00020^H\u0007J\b\u0010_\u001a\u00020#H\u0007J\u0010\u0010`\u001a\u00020#2\u0006\u0010a\u001a\u00020\u000fH\u0003J\u0010\u0010b\u001a\u00020\u00162\u0006\u0010c\u001a\u00020DH\u0007J\u0010\u0010d\u001a\u00020\u000f2\u0006\u0010e\u001a\u00020\u0019H\u0002R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u001aR\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u0016X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\"\u0010!\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\"X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R2\u0010(\u001a\u001a\u0012\u0006\u0012\u0004\u0018\u00010\u000f\u0012\u0006\u0012\u0004\u0018\u00010\u000f\u0012\u0004\u0012\u00020#\u0018\u00010)X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R\"\u0010.\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\"X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b/\u0010%\"\u0004\b0\u0010\'R(\u00101\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020#\u0018\u000102X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106R:\u00107\u001a\"\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0908\u0012\u0004\u0012\u00020#\u0018\u000102X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b:\u00104\"\u0004\b;\u00106R\u000e\u0010<\u001a\u00020=X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010>\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010?\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010@\u001a\u0004\u0018\u00010 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010A\u001a\u00020BX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006g"}, d2 = {"Lcom/nfc/app/print/BLEPrinter;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "bluetoothGatt", "Landroid/bluetooth/BluetoothGatt;", "bluetoothLeScanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "bluetoothManager", "Landroid/bluetooth/BluetoothManager;", "discoveredDevices", "Ljava/util/LinkedHashMap;", "", "Landroid/bluetooth/BluetoothDevice;", "gattCallback", "Landroid/bluetooth/BluetoothGattCallback;", "handler", "Landroid/os/Handler;", "isConnected", "", "isScanning", "lastSuccessfulWriteType", "", "Ljava/lang/Integer;", "lastWriteSuccess", "Ljava/lang/Boolean;", "maxPayloadSize", "notificationsEnabled", "notifyCharacteristic", "Landroid/bluetooth/BluetoothGattCharacteristic;", "onConnected", "Lkotlin/Function0;", "", "getOnConnected", "()Lkotlin/jvm/functions/Function0;", "setOnConnected", "(Lkotlin/jvm/functions/Function0;)V", "onDeviceFound", "Lkotlin/Function2;", "getOnDeviceFound", "()Lkotlin/jvm/functions/Function2;", "setOnDeviceFound", "(Lkotlin/jvm/functions/Function2;)V", "onDisconnected", "getOnDisconnected", "setOnDisconnected", "onError", "Lkotlin/Function1;", "getOnError", "()Lkotlin/jvm/functions/Function1;", "setOnError", "(Lkotlin/jvm/functions/Function1;)V", "onScanComplete", "", "Lkotlin/Pair;", "getOnScanComplete", "setOnScanComplete", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "supportsNoResponseWrite", "supportsResponseWrite", "writeCharacteristic", "writeLock", "Ljava/lang/Object;", "buildReceiptBody", "", "cardNumber", "carNumber", "unitName", "deviceName", "amount", "captureNotifyCharacteristic", "service", "Landroid/bluetooth/BluetoothGattService;", "connect", "deviceAddress", "determineInitialWriteType", "properties", "disconnect", "enableNotifications", "gatt", "findWriteCharacteristic", "handleScanResult", "result", "Landroid/bluetooth/le/ScanResult;", "optimizeConnection", "pickWriteCharacteristicFromService", "preferredWriteModes", "printReceipt", "scanForPrinters", "scanDurationMs", "", "stopScan", "stopScanInternal", "reason", "writeData", "data", "writeTypeToString", "type", "Companion", "app_debug"})
public final class BLEPrinter {
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    public static final com.nfc.app.print.BLEPrinter.Companion Companion = null;
    private static final java.lang.String TAG = "BLEPrinter";
    private static final long DEFAULT_SCAN_TIMEOUT_MS = 10000L;
    private static final long WRITE_ACK_TIMEOUT_MS = 900L;
    private static final long NO_RESPONSE_ACK_DELAY_MS = 60L;
    private static final long RETRY_COOLDOWN_MS = 140L;
    private static final int MAX_WRITE_RETRIES = 3;
    private static final java.util.UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = null;
    private static final java.util.Set<java.util.UUID> KNOWN_SERVICE_UUIDS = null;
    private static final java.util.Set<java.util.UUID> KNOWN_WRITE_UUIDS = null;
    private static final java.util.Set<java.util.UUID> KNOWN_NOTIFY_UUIDS = null;
    private static final byte ESC = (byte)27;
    private static final byte GS = (byte)29;
    @org.jetbrains.annotations.NotNull
    private static final byte[] INIT = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] ALIGN_LEFT = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] ALIGN_CENTER = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] ALIGN_RIGHT = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] FONT_CONTENT = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] BOLD_ON = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] BOLD_OFF = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] LINE_FEED = {(byte)10};
    @org.jetbrains.annotations.NotNull
    private static final byte[] CUT_PAPER = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String SEPARATOR = "--------------------------------";
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onDeviceFound;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.util.List<kotlin.Pair<java.lang.String, java.lang.String>>, kotlin.Unit> onScanComplete;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function0<kotlin.Unit> onConnected;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function0<kotlin.Unit> onDisconnected;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError;
    private final android.bluetooth.BluetoothManager bluetoothManager = null;
    private final android.bluetooth.BluetoothAdapter bluetoothAdapter = null;
    private final android.os.Handler handler = null;
    private android.bluetooth.le.BluetoothLeScanner bluetoothLeScanner;
    private final java.util.LinkedHashMap<java.lang.String, android.bluetooth.BluetoothDevice> discoveredDevices = null;
    private android.bluetooth.BluetoothGatt bluetoothGatt;
    private android.bluetooth.BluetoothGattCharacteristic writeCharacteristic;
    private android.bluetooth.BluetoothGattCharacteristic notifyCharacteristic;
    private final java.lang.Object writeLock = null;
    private java.lang.Boolean lastWriteSuccess;
    private int maxPayloadSize = 20;
    private boolean notificationsEnabled = false;
    private boolean supportsResponseWrite = false;
    private boolean supportsNoResponseWrite = false;
    private java.lang.Integer lastSuccessfulWriteType;
    private boolean isConnected = false;
    private boolean isScanning = false;
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    private final android.bluetooth.BluetoothGattCallback gattCallback = null;
    
    public BLEPrinter(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function2<java.lang.String, java.lang.String, kotlin.Unit> getOnDeviceFound() {
        return null;
    }
    
    public final void setOnDeviceFound(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.util.List<kotlin.Pair<java.lang.String, java.lang.String>>, kotlin.Unit> getOnScanComplete() {
        return null;
    }
    
    public final void setOnScanComplete(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.util.List<kotlin.Pair<java.lang.String, java.lang.String>>, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnConnected() {
        return null;
    }
    
    public final void setOnConnected(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnDisconnected() {
        return null;
    }
    
    public final void setOnDisconnected(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit> getOnError() {
        return null;
    }
    
    public final void setOnError(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> p0) {
    }
    
    private final void handleScanResult(android.bluetooth.le.ScanResult result) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void scanForPrinters(long scanDurationMs) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void stopScan() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void stopScanInternal(java.lang.String reason) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean connect(@org.jetbrains.annotations.Nullable
    java.lang.String deviceAddress) {
        return false;
    }
    
    private final void findWriteCharacteristic(android.bluetooth.BluetoothGatt gatt) {
    }
    
    private final boolean pickWriteCharacteristicFromService(android.bluetooth.BluetoothGattService service) {
        return false;
    }
    
    private final void captureNotifyCharacteristic(android.bluetooth.BluetoothGattService service) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void enableNotifications(android.bluetooth.BluetoothGatt gatt) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void optimizeConnection(android.bluetooth.BluetoothGatt gatt) {
    }
    
    private final int determineInitialWriteType(int properties) {
        return 0;
    }
    
    private final java.util.List<java.lang.Integer> preferredWriteModes() {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void disconnect() {
    }
    
    public final boolean isConnected() {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean writeData(@org.jetbrains.annotations.NotNull
    byte[] data) {
        return false;
    }
    
    public final boolean printReceipt(@org.jetbrains.annotations.NotNull
    java.lang.String cardNumber, @org.jetbrains.annotations.NotNull
    java.lang.String carNumber, @org.jetbrains.annotations.NotNull
    java.lang.String unitName, @org.jetbrains.annotations.NotNull
    java.lang.String deviceName, @org.jetbrains.annotations.NotNull
    java.lang.String amount) {
        return false;
    }
    
    private final byte[] buildReceiptBody(java.lang.String cardNumber, java.lang.String carNumber, java.lang.String unitName, java.lang.String deviceName, java.lang.String amount) {
        return null;
    }
    
    private final java.lang.String writeTypeToString(int type) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0006\n\u0002\u0010\"\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\t\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006R\u0011\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0006R\u0011\u0010\r\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0006R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0011\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0006R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082D\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0017\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0006R\u000e\u0010\u0019\u001a\u00020\u0016X\u0082D\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u001a\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0006R\u001c\u0010\u001c\u001a\u0010\u0012\f\u0012\n \u001e*\u0004\u0018\u00010\u00100\u00100\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u001f\u001a\u0010\u0012\f\u0012\n \u001e*\u0004\u0018\u00010\u00100\u00100\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010 \u001a\u0010\u0012\f\u0012\n \u001e*\u0004\u0018\u00010\u00100\u00100\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010!\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0006R\u000e\u0010#\u001a\u00020$X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0014X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0014X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020(X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020(X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0014X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/nfc/app/print/BLEPrinter$Companion;", "", "()V", "ALIGN_CENTER", "", "getALIGN_CENTER", "()[B", "ALIGN_LEFT", "getALIGN_LEFT", "ALIGN_RIGHT", "getALIGN_RIGHT", "BOLD_OFF", "getBOLD_OFF", "BOLD_ON", "getBOLD_ON", "CLIENT_CHARACTERISTIC_CONFIG_UUID", "Ljava/util/UUID;", "CUT_PAPER", "getCUT_PAPER", "DEFAULT_SCAN_TIMEOUT_MS", "", "ESC", "", "FONT_CONTENT", "getFONT_CONTENT", "GS", "INIT", "getINIT", "KNOWN_NOTIFY_UUIDS", "", "kotlin.jvm.PlatformType", "KNOWN_SERVICE_UUIDS", "KNOWN_WRITE_UUIDS", "LINE_FEED", "getLINE_FEED", "MAX_WRITE_RETRIES", "", "NO_RESPONSE_ACK_DELAY_MS", "RETRY_COOLDOWN_MS", "SEPARATOR", "", "TAG", "WRITE_ACK_TIMEOUT_MS", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getINIT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getALIGN_LEFT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getALIGN_CENTER() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getALIGN_RIGHT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getFONT_CONTENT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getBOLD_ON() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getBOLD_OFF() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getLINE_FEED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getCUT_PAPER() {
            return null;
        }
    }
}