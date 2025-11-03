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
    val readTime: Long,
    val content: String,
    val uploadStatus: Boolean = false
)
