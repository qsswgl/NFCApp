package com.nfc.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NFCRecord::class], version = 3, exportSchema = false)
abstract class NFCDatabase : RoomDatabase() {
    abstract fun nfcRecordDao(): NFCRecordDao

    companion object {
        @Volatile
        private var INSTANCE: NFCDatabase? = null

        fun getDatabase(context: Context): NFCDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NFCDatabase::class.java,
                    "nfc_database"
                )
                .fallbackToDestructiveMigration() // 简单处理：删除旧数据库重建
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
