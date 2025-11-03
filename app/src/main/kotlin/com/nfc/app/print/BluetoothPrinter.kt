package com.nfc.app.print

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log

class BluetoothPrinter(private val context: Context) {
    private var bluetoothSocket: BluetoothSocket? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected ?: false
    }

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String): Boolean {
        return try {
            if (isConnected()) {
                disconnect()
            }

            val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
            if (device != null) {
                val uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                Log.d("BluetoothPrinter", "Connected to ${device.name}")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "Connection failed: ${e.message}")
            false
        }
    }

    fun disconnect() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "Disconnect error: ${e.message}")
        }
    }

    fun print(content: String): Boolean {
        return try {
            val outputStream = bluetoothSocket?.outputStream
            if (outputStream != null) {
                outputStream.write(content.toByteArray())
                outputStream.flush()
                Log.d("BluetoothPrinter", "Print sent successfully")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "Print error: ${e.message}")
            false
        }
    }

    fun printBytes(data: ByteArray): Boolean {
        return try {
            val outputStream = bluetoothSocket?.outputStream
            if (outputStream != null) {
                outputStream.write(data)
                outputStream.flush()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "Print bytes error: ${e.message}")
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun getAvailableDevices(): List<Pair<String, String>> {
        val devices = mutableListOf<Pair<String, String>>()
        bluetoothAdapter?.bondedDevices?.forEach { device ->
            devices.add(Pair(device.name, device.address))
        }
        return devices
    }
}
