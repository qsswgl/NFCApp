package com.nfc.app.database;

import java.lang.System;

@androidx.room.Entity(tableName = "nfc_records")
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b%\b\u0087\b\u0018\u00002\u00020\u0001Bs\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u0005\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\f\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000f\u00a2\u0006\u0002\u0010\u0012J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u000fH\u00c6\u0003J\t\u0010%\u001a\u00020\fH\u00c6\u0003J\t\u0010&\u001a\u00020\u000fH\u00c6\u0003J\t\u0010\'\u001a\u00020\u0005H\u00c6\u0003J\t\u0010(\u001a\u00020\u0005H\u00c6\u0003J\t\u0010)\u001a\u00020\u0005H\u00c6\u0003J\t\u0010*\u001a\u00020\u0005H\u00c6\u0003J\t\u0010+\u001a\u00020\u0005H\u00c6\u0003J\t\u0010,\u001a\u00020\u0005H\u00c6\u0003J\t\u0010-\u001a\u00020\fH\u00c6\u0003J\t\u0010.\u001a\u00020\u0005H\u00c6\u0003J\u0081\u0001\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\f2\b\b\u0002\u0010\u0011\u001a\u00020\u000fH\u00c6\u0001J\u0013\u00100\u001a\u00020\u000f2\b\u00101\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00102\u001a\u00020\u0003H\u00d6\u0001J\t\u00103\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\r\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0014R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u0011\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0011\u0010\u0010\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001d\u00a8\u00064"}, d2 = {"Lcom/nfc/app/database/NFCRecord;", "", "id", "", "nfcId", "", "cardNumber", "carNumber", "unitName", "deviceName", "amount", "readTime", "", "content", "uploadStatus", "", "uploadTime", "uploadSuccess", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;ZJZ)V", "getAmount", "()Ljava/lang/String;", "getCarNumber", "getCardNumber", "getContent", "getDeviceName", "getId", "()I", "getNfcId", "getReadTime", "()J", "getUnitName", "getUploadStatus", "()Z", "getUploadSuccess", "getUploadTime", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class NFCRecord {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final int id = 0;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String nfcId = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String cardNumber = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String carNumber = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String unitName = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String deviceName = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String amount = null;
    private final long readTime = 0L;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String content = null;
    private final boolean uploadStatus = false;
    private final long uploadTime = 0L;
    private final boolean uploadSuccess = false;
    
    @org.jetbrains.annotations.NotNull
    public final com.nfc.app.database.NFCRecord copy(int id, @org.jetbrains.annotations.NotNull
    java.lang.String nfcId, @org.jetbrains.annotations.NotNull
    java.lang.String cardNumber, @org.jetbrains.annotations.NotNull
    java.lang.String carNumber, @org.jetbrains.annotations.NotNull
    java.lang.String unitName, @org.jetbrains.annotations.NotNull
    java.lang.String deviceName, @org.jetbrains.annotations.NotNull
    java.lang.String amount, long readTime, @org.jetbrains.annotations.NotNull
    java.lang.String content, boolean uploadStatus, long uploadTime, boolean uploadSuccess) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public NFCRecord(int id, @org.jetbrains.annotations.NotNull
    java.lang.String nfcId, @org.jetbrains.annotations.NotNull
    java.lang.String cardNumber, @org.jetbrains.annotations.NotNull
    java.lang.String carNumber, @org.jetbrains.annotations.NotNull
    java.lang.String unitName, @org.jetbrains.annotations.NotNull
    java.lang.String deviceName, @org.jetbrains.annotations.NotNull
    java.lang.String amount, long readTime, @org.jetbrains.annotations.NotNull
    java.lang.String content, boolean uploadStatus, long uploadTime, boolean uploadSuccess) {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int getId() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getNfcId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getCardNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getCarNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getUnitName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getDeviceName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAmount() {
        return null;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final long getReadTime() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getContent() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean getUploadStatus() {
        return false;
    }
    
    public final long component11() {
        return 0L;
    }
    
    public final long getUploadTime() {
        return 0L;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean getUploadSuccess() {
        return false;
    }
}