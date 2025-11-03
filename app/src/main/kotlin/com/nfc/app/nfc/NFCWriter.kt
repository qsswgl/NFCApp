package com.nfc.app.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

class NFCWriter {
    fun writeTag(tag: Tag, data: Map<String, String>): Boolean {
        return try {
            val ndefMessage = createNdefMessage(data)
            val ndef = Ndef.get(tag)

            if (ndef != null) {
                ndef.connect()
                if (ndef.maxSize < ndefMessage.toByteArray().size) {
                    return false
                }
                ndef.writeNdefMessage(ndefMessage)
                ndef.close()
                true
            } else {
                // Try formatting as NDEF
                val ndefFormatable = NdefFormatable.get(tag)
                if (ndefFormatable != null) {
                    ndefFormatable.connect()
                    ndefFormatable.format(ndefMessage)
                    ndefFormatable.close()
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun createNdefMessage(data: Map<String, String>): NdefMessage {
        val records = mutableListOf<NdefRecord>()

        data.forEach { (key, value) ->
            val text = "$key:$value"
            val ndefRecord = NdefRecord.createTextRecord("en", text)
            records.add(ndefRecord)
        }

        if (records.isEmpty()) {
            records.add(NdefRecord.createTextRecord("en", "empty"))
        }

        return NdefMessage(records.toTypedArray())
    }
}
