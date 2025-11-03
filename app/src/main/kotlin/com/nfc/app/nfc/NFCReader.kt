package com.nfc.app.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

class NFCReader {
    fun readTag(tag: Tag): Map<String, Any>? {
        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                ndef.close()

                if (ndefMessage != null) {
                    parseNdefMessage(ndefMessage, tag)
                } else {
                    mapOf("id" to tag.id.joinToString(""))
                }
            } else {
                mapOf("id" to tag.id.joinToString(""))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseNdefMessage(
        ndefMessage: NdefMessage,
        tag: Tag
    ): Map<String, Any> {
        val records = ndefMessage.records
        val data = mutableMapOf<String, Any>()
        data["id"] = tag.id.joinToString("")
        data["recordCount"] = records.size

        records.forEachIndexed { index, record ->
            when {
                record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(
                    NdefRecord.RTD_TEXT
                ) -> {
                    val text = String(record.payload)
                    data["text_$index"] = text
                }
                record.tnf == NdefRecord.TNF_MIME_MEDIA -> {
                    data["mime_$index"] = String(record.type)
                }
                else -> {
                    data["record_$index"] = String(record.payload)
                }
            }
        }

        return data
    }
}
