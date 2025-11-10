package com.nfc.app.print;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\t\u0018\u0000  2\u00020\u0001:\u0001 B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007J\b\u0010\r\u001a\u00020\nH\u0007J\u0006\u0010\u000e\u001a\u00020\u000fJ\u001a\u0010\u0010\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u00120\u0011H\u0007J\u0006\u0010\u0013\u001a\u00020\nJ\u000e\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\fJ\u000e\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0018J\u0006\u0010\u0019\u001a\u00020\nJ.\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020\fR\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/nfc/app/print/BluetoothPrinter;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "bluetoothSocket", "Landroid/bluetooth/BluetoothSocket;", "connect", "", "deviceAddress", "", "connectToPrinter", "disconnect", "", "getAvailableDevices", "", "Lkotlin/Pair;", "isConnected", "print", "content", "printBytes", "data", "", "printRawTest", "printReceipt", "cardNumber", "carNumber", "unitName", "deviceName", "amount", "Companion", "app_debug"})
public final class BluetoothPrinter {
    private final android.content.Context context = null;
    private android.bluetooth.BluetoothSocket bluetoothSocket;
    private final android.bluetooth.BluetoothAdapter bluetoothAdapter = null;
    @org.jetbrains.annotations.NotNull
    public static final com.nfc.app.print.BluetoothPrinter.Companion Companion = null;
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
    private static final byte[] FONT_NORMAL = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] FONT_MEDIUM = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] FONT_LARGE = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] FONT_TITLE = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] FONT_CONTENT = null;
    @org.jetbrains.annotations.NotNull
    private static final byte[] FONT_TIME = null;
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
    
    /**
     * 打印小票到POS58打印机
     * @param cardNumber 卡号
     * @param carNumber 机号
     * @param unitName 单位名称
     * @param deviceName 设备名称
     * @param amount 消费金额
     */
    public final boolean printReceipt(@org.jetbrains.annotations.NotNull
    java.lang.String cardNumber, @org.jetbrains.annotations.NotNull
    java.lang.String carNumber, @org.jetbrains.annotations.NotNull
    java.lang.String unitName, @org.jetbrains.annotations.NotNull
    java.lang.String deviceName, @org.jetbrains.annotations.NotNull
    java.lang.String amount) {
        return false;
    }
    
    /**
     * 发送最小化测试数据用于验证打印机是否会出纸/响应
     */
    public final boolean printRawTest() {
        return false;
    }
    
    /**
     * 自动连接已配对的打印机
     * 优先连接名称包含"POS"或"Print"或"GP"的设备
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean connectToPrinter() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\r\n\u0002\u0010\u0005\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\t\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006R\u0011\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0006R\u0011\u0010\r\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0006R\u0011\u0010\u000f\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0006R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082D\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0013\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0006R\u0011\u0010\u0015\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0006R\u0011\u0010\u0017\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0006R\u0011\u0010\u0019\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0006R\u0011\u0010\u001b\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0006R\u0011\u0010\u001d\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0006R\u000e\u0010\u001f\u001a\u00020\u0012X\u0082D\u00a2\u0006\u0002\n\u0000R\u0011\u0010 \u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0006R\u0011\u0010\"\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0006R\u000e\u0010$\u001a\u00020%X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/nfc/app/print/BluetoothPrinter$Companion;", "", "()V", "ALIGN_CENTER", "", "getALIGN_CENTER", "()[B", "ALIGN_LEFT", "getALIGN_LEFT", "ALIGN_RIGHT", "getALIGN_RIGHT", "BOLD_OFF", "getBOLD_OFF", "BOLD_ON", "getBOLD_ON", "CUT_PAPER", "getCUT_PAPER", "ESC", "", "FONT_CONTENT", "getFONT_CONTENT", "FONT_LARGE", "getFONT_LARGE", "FONT_MEDIUM", "getFONT_MEDIUM", "FONT_NORMAL", "getFONT_NORMAL", "FONT_TIME", "getFONT_TIME", "FONT_TITLE", "getFONT_TITLE", "GS", "INIT", "getINIT", "LINE_FEED", "getLINE_FEED", "SEPARATOR", "", "app_debug"})
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
        public final byte[] getFONT_NORMAL() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getFONT_MEDIUM() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getFONT_LARGE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getFONT_TITLE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getFONT_CONTENT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] getFONT_TIME() {
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