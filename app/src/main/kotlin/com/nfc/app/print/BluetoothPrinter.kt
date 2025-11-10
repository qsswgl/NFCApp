package com.nfc.app.print

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.OutputStream
import java.nio.charset.Charset

class BluetoothPrinter(private val context: Context) {
    private var bluetoothSocket: BluetoothSocket? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    
    // ESC/POS 打印机指令
    companion object {
        private val ESC: Byte = 0x1B
        private val GS: Byte = 0x1D
        
        // 初始化打印机
        val INIT = byteArrayOf(ESC, 0x40)
        
        // 设置对齐方式
        val ALIGN_LEFT = byteArrayOf(ESC, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(ESC, 0x61, 0x01)
        val ALIGN_RIGHT = byteArrayOf(ESC, 0x61, 0x02)
        
        // 设置字体大小（3号字标准版本）
        // GS ! n 命令: n的低4位控制宽度，高4位控制高度
        // 0x00 = 1号字(1x1), 0x11 = 2号字(2x2), 0x22 = 3号字(3x3)
        val FONT_NORMAL = byteArrayOf(GS, 0x21, 0x00)   // 1号字 - 正常大小(1x)
        val FONT_MEDIUM = byteArrayOf(GS, 0x21, 0x11)   // 2号字 - 中等(2x)
        val FONT_LARGE = byteArrayOf(GS, 0x21, 0x22)    // 3号字 - 大字(3x)
        
        // 自定义字体大小（用于小票打印）
        val FONT_TITLE = byteArrayOf(GS, 0x21, 0x11)    // 标题：2x2（方形）
        val FONT_CONTENT = byteArrayOf(GS, 0x21, 0x11)  // 内容：2x2（方形，80%大小）
        val FONT_TIME = byteArrayOf(GS, 0x21, 0x00)     // 时间：1x1
        
        // 加粗
        val BOLD_ON = byteArrayOf(ESC, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(ESC, 0x45, 0x00)
        
        // 换行
        val LINE_FEED = byteArrayOf(0x0A)
        
        // 切纸
        val CUT_PAPER = byteArrayOf(GS, 0x56, 0x00)
        
        // 分隔线
        const val SEPARATOR = "--------------------------------"
    }

    fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected ?: false
    }

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String): Boolean {
        Log.d("BluetoothPrinter", "========== 开始连接蓝牙设备 ==========")
        Log.d("BluetoothPrinter", "目标设备地址: $deviceAddress")
        
        return try {
            // 检查蓝牙适配器
            if (bluetoothAdapter == null) {
                Log.e("BluetoothPrinter", "❌ 蓝牙适配器为空，设备不支持蓝牙")
                return false
            }
            
            if (!bluetoothAdapter.isEnabled) {
                Log.e("BluetoothPrinter", "❌ 蓝牙未开启")
                return false
            }
            
            Log.d("BluetoothPrinter", "✓ 蓝牙适配器正常，蓝牙已开启")
            
            // 如果已连接，先断开
            if (isConnected()) {
                Log.d("BluetoothPrinter", "检测到已有连接，先断开...")
                disconnect()
            }

            // 获取远程设备
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            if (device == null) {
                Log.e("BluetoothPrinter", "❌ 无法获取设备对象")
                return false
            }
            
            Log.d("BluetoothPrinter", "✓ 设备对象获取成功")
            Log.d("BluetoothPrinter", "设备名称: ${device.name}")
            Log.d("BluetoothPrinter", "设备地址: ${device.address}")
            Log.d("BluetoothPrinter", "设备类型: ${device.type}")
            Log.d("BluetoothPrinter", "绑定状态: ${device.bondState}")
            
            // 检查设备是否已配对
            if (device.bondState != 12) { // BOND_BONDED = 12
                Log.w("BluetoothPrinter", "⚠️ 设备未配对或配对状态异常: ${device.bondState}")
            }
            
            // 在连接前取消设备扫描，避免 discovery 干扰连接（会显著影响 RFCOMM 连接成功率）
            try {
                bluetoothAdapter.cancelDiscovery()
                Log.d("BluetoothPrinter", "✓ 已取消蓝牙扫描（cancelDiscovery）")
            } catch (e: Exception) {
                Log.d("BluetoothPrinter", "取消扫描失败或不支持: ${e.message}")
            }

            // 创建RFCOMM套接字（尝试多种方法以提高兼容性）
            val uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            Log.d("BluetoothPrinter", "准备连接，RFCOMM UUID: $uuid")

            // 优先尝试不安全的 RFCOMM Socket（很多打印机在 secure handshake 时会失败）
            try {
                Log.d("BluetoothPrinter", "尝试调用 createInsecureRfcommSocketToServiceRecord(uuid) ...")
                val insecureSocket = try {
                    // 直接调用（在较新 API 中可用）
                    device.createInsecureRfcommSocketToServiceRecord(uuid)
                } catch (nsme: NoSuchMethodError) {
                    // 方法不可用，回退到反射调用
                    Log.d("BluetoothPrinter", "createInsecureRfcommSocketToServiceRecord 方法不可用，尝试反射调用")
                    try {
                        device.javaClass
                            .getMethod("createInsecureRfcommSocketToServiceRecord", java.util.UUID::class.java)
                            .invoke(device, uuid) as? BluetoothSocket
                    } catch (e: Exception) {
                        Log.d("BluetoothPrinter", "反射调用 createInsecureRfcommSocketToServiceRecord 失败: ${e.message}")
                        null
                    }
                } catch (e: Exception) {
                    Log.d("BluetoothPrinter", "直接调用 createInsecureRfcommSocketToServiceRecord 抛异常: ${e.message}")
                    null
                }

                    if (insecureSocket != null) {
                    bluetoothSocket = insecureSocket
                    Log.d("BluetoothPrinter", "✓ 已创建不安全 Socket，尝试连接...")
                    try {
                        bluetoothSocket?.connect()
                            // 等待短时以让连接稳定
                            Thread.sleep(200)
                            Log.d("BluetoothPrinter", "✓✓✓ 不安全 Socket 连接成功！")
                            return true
                    } catch (e: Exception) {
                        Log.w("BluetoothPrinter", "不安全 Socket 连接失败: ${e.message}")
                        try {
                            bluetoothSocket?.close()
                        } catch (closeEx: Exception) {
                            Log.d("BluetoothPrinter", "关闭不安全 socket 时出错: ${closeEx.message}")
                        }
                        bluetoothSocket = null
                    }
                } else {
                    Log.d("BluetoothPrinter", "未获得不安全 Socket 的实例，继续尝试其他方法")
                }
            } catch (e: Exception) {
                Log.d("BluetoothPrinter", "尝试不安全 Socket 过程发生异常: ${e.message}")
            }

            // 尝试标准的 createRfcommSocketToServiceRecord
            try {
                Log.d("BluetoothPrinter", "尝试 createRfcommSocketToServiceRecord(uuid) ...")
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                    if (bluetoothSocket == null) {
                    Log.e("BluetoothPrinter", "❌ 标准 Socket 创建返回 null")
                } else {
                    Log.d("BluetoothPrinter", "✓ Socket创建成功（标准）")
                    Log.d("BluetoothPrinter", "正在连接...")
                    try {
                        bluetoothSocket?.connect()
                            // 等待短时以让连接稳定
                            Thread.sleep(200)
                            Log.d("BluetoothPrinter", "✓✓✓ 标准 Socket 连接成功！")
                            return true
                    } catch (e: java.io.IOException) {
                        Log.e("BluetoothPrinter", "❌ 标准 Socket 连接失败: ${e.message}", e)
                        Log.e("BluetoothPrinter", "错误类型: ${e.javaClass.simpleName}")
                        Log.e("BluetoothPrinter", "堆栈跟踪: ${e.stackTraceToString()}")
                        try {
                            bluetoothSocket?.close()
                        } catch (closeEx: Exception) {
                            Log.d("BluetoothPrinter", "关闭标准 socket 时出错: ${closeEx.message}")
                        }
                        bluetoothSocket = null
                    }
                }
            } catch (e: Exception) {
                Log.d("BluetoothPrinter", "调用 createRfcommSocketToServiceRecord 抛出异常: ${e.message}")
            }

            // 反射备用方法（createRfcommSocket(int)）
            try {
                Log.d("BluetoothPrinter", "尝试备用连接方法（反射 createRfcommSocket）...")
                val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                bluetoothSocket = method.invoke(device, 1) as? BluetoothSocket
                if (bluetoothSocket != null) {
                    try {
                        bluetoothSocket?.connect()
                        // 等待短时以让连接稳定
                        Thread.sleep(200)
                        Log.d("BluetoothPrinter", "✓✓✓ 反射备用方法连接成功！")
                        return true
                    } catch (e2: Exception) {
                        Log.e("BluetoothPrinter", "❌ 反射备用方法也失败: ${e2.message}", e2)
                        try {
                            bluetoothSocket?.close()
                        } catch (closeEx: Exception) {
                            Log.d("BluetoothPrinter", "关闭反射 socket 时出错: ${closeEx.message}")
                        }
                        bluetoothSocket = null
                        return false
                    }
                } else {
                    Log.e("BluetoothPrinter", "❌ 反射创建 Socket 返回 null")
                    return false
                }
            } catch (e: Exception) {
                Log.e("BluetoothPrinter", "❌ 备用连接方法也失败: ${e.message}", e)
                bluetoothSocket = null
                return false
            }
            
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "❌ 连接过程发生异常: ${e.message}", e)
            Log.e("BluetoothPrinter", "异常类型: ${e.javaClass.name}")
            Log.e("BluetoothPrinter", "完整堆栈: ${e.stackTraceToString()}")
            bluetoothSocket = null
            return false
        } finally {
            Log.d("BluetoothPrinter", "========== 连接流程结束 ==========")
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
    
    /**
     * 打印小票到POS58打印机
     * @param cardNumber 卡号
     * @param carNumber 机号
     * @param unitName 单位名称
     * @param deviceName 设备名称
     * @param amount 消费金额
     */
    fun printReceipt(
        cardNumber: String, 
        carNumber: String, 
        unitName: String,
        deviceName: String,
        amount: String
    ): Boolean {
        // 封装打印操作为可重试的函数：若在写入过程中遇到 socket closed / IOException，则尝试重新连接并重试一次
        fun doPrint(): Boolean {
            try {
                val outputStream = bluetoothSocket?.outputStream
                if (outputStream == null) {
                    Log.e("BluetoothPrinter", "OutputStream is null")
                    return false
                }

                fun safeWrite(bytes: ByteArray, desc: String = "data") {
                    try {
                        outputStream.write(bytes)
                        outputStream.flush()
                        Log.d("BluetoothPrinter", "wrote ${bytes.size} bytes for: $desc")
                        // 给打印机短暂缓冲处理时间
                        Thread.sleep(80)
                    } catch (e: java.io.IOException) {
                        Log.e("BluetoothPrinter", "Write failed for $desc: ${e.message}", e)
                        throw e
                    }
                }

                // 初始化打印机
                safeWrite(INIT, "INIT")

                // 打印标题 - 居中、1.5倍字体（80%大小）、加粗
                safeWrite(ALIGN_CENTER, "ALIGN_CENTER")
                safeWrite(FONT_CONTENT, "FONT_CONTENT")
                safeWrite(BOLD_ON, "BOLD_ON")
                safeWrite("消费小票".toByteArray(Charset.forName("GBK")), "TITLE")
                safeWrite(LINE_FEED, "LF")
                safeWrite(LINE_FEED, "LF")

                // 内容
                safeWrite(BOLD_OFF, "BOLD_OFF")
                safeWrite(ALIGN_LEFT, "ALIGN_LEFT")

                // 打印分隔线
                safeWrite(SEPARATOR.toByteArray(), "SEPARATOR")
                safeWrite(LINE_FEED, "LF")

                // 打印卡号
                safeWrite("卡号: ".toByteArray(Charset.forName("GBK")), "CARD_LABEL")
                safeWrite(cardNumber.toByteArray(), "CARD_VALUE")
                safeWrite(LINE_FEED, "LF")
                safeWrite(LINE_FEED, "LF")

                // 打印机号
                safeWrite("机号: ".toByteArray(Charset.forName("GBK")), "TERMINAL_LABEL")
                safeWrite(carNumber.toByteArray(), "TERMINAL_VALUE")
                safeWrite(LINE_FEED, "LF")
                safeWrite(LINE_FEED, "LF")

                // 单位名称
                if (unitName.isNotEmpty()) {
                    safeWrite("单位: ".toByteArray(Charset.forName("GBK")), "UNIT_LABEL")
                    safeWrite(unitName.toByteArray(Charset.forName("GBK")), "UNIT_VALUE")
                    safeWrite(LINE_FEED, "LF")
                    safeWrite(LINE_FEED, "LF")
                }

                // 设备名称
                if (deviceName.isNotEmpty()) {
                    safeWrite("设备: ".toByteArray(Charset.forName("GBK")), "DEVICE_LABEL")
                    safeWrite(deviceName.toByteArray(Charset.forName("GBK")), "DEVICE_VALUE")
                    safeWrite(LINE_FEED, "LF")
                    safeWrite(LINE_FEED, "LF")
                }

                // 打印金额
                safeWrite(BOLD_ON, "BOLD_ON")
                safeWrite("消费金额: ".toByteArray(Charset.forName("GBK")), "AMOUNT_LABEL")
                safeWrite(amount.toByteArray(), "AMOUNT_VALUE")
                safeWrite(" 元".toByteArray(Charset.forName("GBK")), "CNY")
                safeWrite(BOLD_OFF, "BOLD_OFF")
                safeWrite(LINE_FEED, "LF")
                safeWrite(LINE_FEED, "LF")

                // 分隔线
                safeWrite(SEPARATOR.toByteArray(), "SEPARATOR")
                safeWrite(LINE_FEED, "LF")

                // 时间
                val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)
                    .format(java.util.Date())
                safeWrite(ALIGN_CENTER, "ALIGN_CENTER")
                safeWrite(currentTime.toByteArray(), "TIME")
                safeWrite(LINE_FEED, "LF")
                safeWrite(LINE_FEED, "LF")

                // 联系电话与感谢语
                safeWrite("联系电话: 138-0000-0000".toByteArray(Charset.forName("GBK")), "PHONE")
                safeWrite(LINE_FEED, "LF")
                safeWrite(LINE_FEED, "LF")
                safeWrite("谢谢使用!".toByteArray(Charset.forName("GBK")), "THANKS")
                safeWrite(LINE_FEED, "LF")

                // 多进纸，确保纸头露出
                repeat(5) {
                    safeWrite(LINE_FEED, "FEED")
                }

                // 切纸
                safeWrite(CUT_PAPER, "CUT_PAPER")

                Log.d("BluetoothPrinter", "Receipt printed successfully")
                return true
            } catch (e: java.io.IOException) {
                Log.e("BluetoothPrinter", "IO error during print: ${e.message}", e)
                return false
            } catch (e: Exception) {
                Log.e("BluetoothPrinter", "Unexpected error during print: ${e.message}", e)
                return false
            }
        }

        // 首次尝试
        if (doPrint()) return true

        // 如遇 socket closed / IOError，尝试一次断开->重连->重试
        Log.w("BluetoothPrinter", "首次打印尝试失败，尝试重新连接并重试一次")
        try {
            val addr = bluetoothSocket?.remoteDevice?.address
            disconnect()
            if (!addr.isNullOrEmpty()) {
                // 再次尝试连接
                if (connect(addr)) {
                    // 等待短时稳定
                    Thread.sleep(200)
                    return doPrint()
                }
            }
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "重试打印时发生异常: ${e.message}", e)
        }

        return false
    }

    /**
     * 发送最小化测试数据用于验证打印机是否会出纸/响应
     */
    fun printRawTest(): Boolean {
        // 与 printReceipt 类似，若写入出错则尝试一次重连并重试
        fun doRaw(): Boolean {
            return try {
                val outputStream = bluetoothSocket?.outputStream
                if (outputStream == null) {
                    Log.e("BluetoothPrinter", "OutputStream is null for raw test")
                    return false
                }
                val test = ("\n\n\nTEST\n\n\n").toByteArray()
                outputStream.write(test)
                outputStream.flush()
                Log.d("BluetoothPrinter", "Raw test written ${test.size} bytes")
                true
            } catch (e: java.io.IOException) {
                Log.e("BluetoothPrinter", "Raw test IO failed: ${e.message}", e)
                false
            } catch (e: Exception) {
                Log.e("BluetoothPrinter", "Raw test failed: ${e.message}", e)
                false
            }
        }

        if (doRaw()) return true

        // 失败则尝试重连一次再重试
        Log.w("BluetoothPrinter", "Raw test first attempt failed, retrying after reconnect")
        try {
            val addr = bluetoothSocket?.remoteDevice?.address
            disconnect()
            if (!addr.isNullOrEmpty() && connect(addr)) {
                Thread.sleep(200)
                return doRaw()
            }
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "Raw test retry failed: ${e.message}", e)
        }

        return false
    }
    
    /**
     * 自动连接已配对的打印机
     * 优先连接名称包含"POS"或"Print"或"GP"的设备
     */
    @SuppressLint("MissingPermission")
    fun connectToPrinter(): Boolean {
        Log.d("BluetoothPrinter", "========== 自动查找并连接打印机 ==========")
        
        try {
            // 检查蓝牙适配器
            if (bluetoothAdapter == null) {
                Log.e("BluetoothPrinter", "❌ 蓝牙适配器不可用")
                return false
            }
            
            if (!bluetoothAdapter.isEnabled) {
                Log.e("BluetoothPrinter", "❌ 蓝牙未开启，请先开启蓝牙")
                return false
            }
            
            Log.d("BluetoothPrinter", "✓ 蓝牙适配器状态正常")
            
            // 获取已配对设备
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.isNullOrEmpty()) {
                Log.e("BluetoothPrinter", "❌ 没有找到任何已配对的蓝牙设备")
                Log.e("BluetoothPrinter", "请先在手机设置中配对打印机")
                return false
            }
            
            Log.d("BluetoothPrinter", "✓ 找到 ${pairedDevices.size} 个已配对设备:")
            pairedDevices.forEachIndexed { index, device ->
                Log.d("BluetoothPrinter", "  [$index] 名称: ${device.name ?: "未知"}, 地址: ${device.address}, 类型: ${device.type}")
            }

            // 检查是否有用户在 App 内保存的首选打印机地址（优先使用）
            try {
                val prefs = context.getSharedPreferences("nfc_prefs", Context.MODE_PRIVATE)
                val preferred = prefs.getString("pref_printer_address", null)
                if (!preferred.isNullOrEmpty()) {
                    Log.d("BluetoothPrinter", "检测到首选打印机地址: $preferred，尝试优先连接")
                    val found = pairedDevices.firstOrNull { it.address.equals(preferred, ignoreCase = true) }
                    if (found != null) {
                        Log.d("BluetoothPrinter", "在已配对设备中找到首选打印机: ${found.name} / ${found.address}")
                        // 直接使用已有的 connect 方法（包含多种兼容性尝试）
                        try {
                            if (connect(found.address)) {
                                Log.d("BluetoothPrinter", "✅ 成功连接首选打印机：${found.name}")
                                return true
                            } else {
                                Log.w("BluetoothPrinter", "⚠️ 无法连接首选打印机 ${found.name}，回退到自动选择逻辑")
                            }
                        } catch (e: Exception) {
                            Log.e("BluetoothPrinter", "连接首选打印机时异常: ${e.message}", e)
                        }
                    } else {
                        Log.w("BluetoothPrinter", "首选打印机地址 $preferred 未在已配对设备中找到")
                    }
                }
            } catch (e: Exception) {
                Log.d("BluetoothPrinter", "读取首选打印机配置失败: ${e.message}")
            }
            
            // 优先选择经典蓝牙设备（RFCOMM 可用），并跳过名称中带有 BLE 的设备
            val keywords = listOf("GP-C58", "GP", "POS", "Print", "Printer")
            var printerDevice: android.bluetooth.BluetoothDevice? = null

            // 先尝试在经典蓝牙设备中匹配关键字
            val classicDevices = pairedDevices.filter { device ->
                try {
                    device.type == android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC
                } catch (e: Exception) {
                    // 兼容性保护，回退到数值比较
                    device.type == 1
                }
            }.filter { device ->
                // 排除名称包含 BLE 的设备（例如 "Printer_AF35_BLE"）
                val name = device.name ?: ""
                !name.contains("BLE", ignoreCase = true) && !name.contains("(BLE)", ignoreCase = true)
            }

            for (keyword in keywords) {
                printerDevice = classicDevices.firstOrNull { device ->
                    device.name?.contains(keyword, ignoreCase = true) == true
                }
                if (printerDevice != null) {
                    Log.d("BluetoothPrinter", "✓ 在经典设备中找到匹配关键字 '$keyword' 的设备: ${printerDevice.name}")
                    break
                }
            }

            // 如果经典设备中没有匹配的，再在所有已配对设备中匹配（但仍尽量避开带 BLE 的设备）
            if (printerDevice == null) {
                for (keyword in keywords) {
                    printerDevice = pairedDevices.firstOrNull { device ->
                        val name = device.name ?: ""
                        !name.contains("BLE", ignoreCase = true) &&
                                !name.contains("(BLE)", ignoreCase = true) &&
                                name.contains(keyword, ignoreCase = true)
                    }
                    if (printerDevice != null) {
                        Log.d("BluetoothPrinter", "✓ 在所有配对设备中找到匹配关键字 '$keyword' 的设备: ${printerDevice.name}")
                        break
                    }
                }
            }

            // 先优先选择名字像 AQ- 开头的设备（很多出厂打印机会用 AQ- 前缀）
            if (printerDevice == null) {
                val aqDevice = pairedDevices.firstOrNull { device ->
                    val name = device.name ?: ""
                    name.startsWith("AQ-", ignoreCase = true) || name.startsWith("AQ", ignoreCase = true)
                }
                if (aqDevice != null) {
                    printerDevice = aqDevice
                    Log.d("BluetoothPrinter", "✓ 优先选择 AQ-* 设备: ${printerDevice.name}")
                }
            }

            // 如果仍然没找到匹配的经典设备，则尝试直接选择第一个非BLE配对设备
            if (printerDevice == null) {
                printerDevice = pairedDevices.firstOrNull { device ->
                    val name = device.name ?: ""
                    !name.contains("BLE", ignoreCase = true) && !name.contains("(BLE)", ignoreCase = true)
                }
                if (printerDevice != null) {
                    Log.w("BluetoothPrinter", "⚠️ 未找到关键字匹配的打印机，使用第一个非-BLE 配对设备: ${printerDevice.name}")
                }
            }

            // 最后退回到任何已配对设备（极端情况下）
            if (printerDevice == null) {
                printerDevice = pairedDevices.first()
                Log.w("BluetoothPrinter", "⚠️ 未找到非-BLE 配对设备，退回使用第一个配对设备: ${printerDevice.name}")
            }
            
            Log.d("BluetoothPrinter", "========================================")
            Log.d("BluetoothPrinter", "目标打印机信息:")
            Log.d("BluetoothPrinter", "  名称: ${printerDevice?.name ?: "未知"}")
            Log.d("BluetoothPrinter", "  地址: ${printerDevice?.address ?: "未知"}")
            Log.d("BluetoothPrinter", "  类型: ${printerDevice?.type ?: -1}")
            Log.d("BluetoothPrinter", "  绑定状态: ${printerDevice?.bondState ?: -1}")
            Log.d("BluetoothPrinter", "========================================")
            
            // 尝试连接
            if (printerDevice != null) {
                // 使用更宽松的 RFCOMM 连接尝试（有些设备对 secure socket 有兼容性问题）
                try {
                    val uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                    Log.d("BluetoothPrinter", "尝试不安全 RFCOMM Socket（createInsecureRfcommSocket）...")
                    try {
                        val insecureSocket = printerDevice.javaClass
                            .getMethod("createInsecureRfcommSocketToServiceRecord", java.util.UUID::class.java)
                            .invoke(printerDevice, uuid) as? android.bluetooth.BluetoothSocket

                        if (insecureSocket != null) {
                            bluetoothSocket = insecureSocket
                            try {
                                bluetoothSocket?.connect()
                                Log.d("BluetoothPrinter", "✓ 不安全 Socket 连接成功！")
                                return true
                            } catch (e: Exception) {
                                Log.w("BluetoothPrinter", "不安全 Socket 连接失败: ${e.message}")
                            }
                        }
                    } catch (ignore: Exception) {
                        // 方法可能不存在或调用失败，继续后续尝试
                        Log.d("BluetoothPrinter", "createInsecureRfcommSocket 方法不可用或调用失败: ${ignore.message}")
                    }

                    // 回退到标准连接逻辑
                    return connect(printerDevice.address)
                } catch (e: Exception) {
                    Log.e("BluetoothPrinter", "❌ 连接打印机时发生异常: ${e.message}", e)
                    return false
                }
            } else {
                Log.e("BluetoothPrinter", "❌ 打印机设备为空")
                return false
            }
            
        } catch (e: Exception) {
            Log.e("BluetoothPrinter", "❌ 自动连接失败: ${e.message}", e)
            Log.e("BluetoothPrinter", "异常堆栈: ${e.stackTraceToString()}")
            return false
        }
    }
}
