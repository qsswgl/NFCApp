package com.nfc.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nfc_records")
data class NFCRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nfcId: String,
    val cardNumber: String,
    val carNumber: String,
    val unitName: String = "",
    val deviceName: String = "",
    val amount: String = "",
    val readTime: Long,
    val content: String,
    val uploadStatus: Boolean = false,  // false=未上传, true=已上传
    val uploadTime: Long = 0,           // 上传时间戳
    val uploadSuccess: Boolean = true   // true=成功, false=失败（仅在uploadStatus=true时有意义）
)
