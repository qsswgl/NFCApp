package com.nfc.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NFCRecordDao {
    @Query("SELECT * FROM nfc_records ORDER BY readTime DESC")
    suspend fun getAllRecords(): List<NFCRecord>

    @Query("SELECT * FROM nfc_records WHERE id = :id")
    suspend fun getRecordById(id: Int): NFCRecord?

    @Query("SELECT * FROM nfc_records WHERE nfcId = :nfcId")
    suspend fun getRecordByNFCId(nfcId: String): List<NFCRecord>

    @Query("SELECT * FROM nfc_records WHERE cardNumber = :cardNumber ORDER BY readTime DESC LIMIT 1")
    suspend fun getLastRecordByCardNumber(cardNumber: String): NFCRecord?

    @Query("SELECT * FROM nfc_records WHERE cardNumber = :cardNumber ORDER BY readTime DESC")
    suspend fun getRecordsByCardNumber(cardNumber: String): List<NFCRecord>

    @Query("SELECT * FROM nfc_records WHERE uploadStatus = 0 ORDER BY readTime ASC")
    suspend fun getUnuploadedRecords(): List<NFCRecord>

    @Insert
    suspend fun insert(record: NFCRecord): Long

    @Update
    suspend fun update(record: NFCRecord)

    @Delete
    suspend fun delete(record: NFCRecord)

    @Query("DELETE FROM nfc_records WHERE uploadStatus = 1")
    suspend fun deleteUploadedRecords()

    @Query("SELECT COUNT(*) FROM nfc_records")
    suspend fun getRecordCount(): Int
}
