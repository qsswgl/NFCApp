package com.nfc.app.print;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007J\u0006\u0010\r\u001a\u00020\u000eJ\u001a\u0010\u000f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u00110\u0010H\u0007J\u0006\u0010\u0012\u001a\u00020\nJ\u000e\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\fJ\u000e\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0017R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/nfc/app/print/BluetoothPrinter;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "bluetoothSocket", "Landroid/bluetooth/BluetoothSocket;", "connect", "", "deviceAddress", "", "disconnect", "", "getAvailableDevices", "", "Lkotlin/Pair;", "isConnected", "print", "content", "printBytes", "data", "", "app_debug"})
public final class BluetoothPrinter {
    private final android.content.Context context = null;
    private android.bluetooth.BluetoothSocket bluetoothSocket;
    private final android.bluetooth.BluetoothAdapter bluetoothAdapter = null;
    
    public BluetoothPrinter(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    public final boolean isConnected() {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean connect(@org.jetbrains.annotations.NotNull
    java.lang.String deviceAddress) {
        return false;
    }
    
    public final void disconnect() {
    }
    
    public final boolean print(@org.jetbrains.annotations.NotNull
    java.lang.String content) {
        return false;
    }
    
    public final boolean printBytes(@org.jetbrains.annotations.NotNull
    byte[] data) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final java.util.List<kotlin.Pair<java.lang.String, java.lang.String>> getAvailableDevices() {
        return null;
    }
}