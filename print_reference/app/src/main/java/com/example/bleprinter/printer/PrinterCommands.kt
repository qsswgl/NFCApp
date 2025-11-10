package com.example.bleprinter.printer

import java.nio.charset.Charset

/**
 * ESC/POS 打印机命令封装类
 * 支持基本的文本打印、格式化和纸张控制
 */
object PrinterCommands {
    
    // ESC/POS 命令常量
    private const val ESC: Byte = 0x1B
    private const val GS: Byte = 0x1D
    private const val LF: Byte = 0x0A  // 换行
    private const val CR: Byte = 0x0D  // 回车
    
    /**
     * AQ打印机特殊初始化序列
     * 某些打印机需要特殊的唤醒命令
     */
    fun initAQPrinter(): ByteArray {
        return byteArrayOf(
            0x10, 0x14, 0x01, 0x00, 0x05,  // 唤醒命令
            ESC, 0x40,  // 初始化
            0x1D, 0x61, 0x30  // 自动状态返回关闭
        )
    }
    
    /**
     * AQ打印机超级简单测试 - 最小化命令
     */
    fun generateAQSimpleTest(): ByteArray {
        return byteArrayOf(
            ESC, 0x40,  // 初始化
            0x54, 0x45, 0x53, 0x54,  // "TEST"
            LF, LF, LF,  // 换行
            0x1D, 0x56, 0x00  // 切纸
        )
    }
    
    /**
     * 初始化打印机
     */
    fun initPrinter(): ByteArray {
        return byteArrayOf(ESC, 0x40)
    }
    
    /**
     * 换行
     */
    fun lineFeed(lines: Int = 1): ByteArray {
        return ByteArray(lines) { LF }
    }
    
    /**
     * 打印文本
     */
    fun printText(text: String, charset: String = "GBK"): ByteArray {
        return try {
            text.toByteArray(Charset.forName(charset))
        } catch (e: Exception) {
            text.toByteArray()
        }
    }
    
    /**
     * 打印文本并换行
     */
    fun printLine(text: String, charset: String = "GBK"): ByteArray {
        return printText(text, charset) + lineFeed()
    }
    
    /**
     * 设置对齐方式
     * @param align 0=左对齐, 1=居中, 2=右对齐
     */
    fun setAlign(align: Int): ByteArray {
        return byteArrayOf(ESC, 0x61, align.toByte())
    }
    
    /**
     * 左对齐
     */
    fun alignLeft(): ByteArray = setAlign(0)
    
    /**
     * 居中对齐
     */
    fun alignCenter(): ByteArray = setAlign(1)
    
    /**
     * 右对齐
     */
    fun alignRight(): ByteArray = setAlign(2)
    
    /**
     * 设置字体大小
     * @param width 宽度倍数 (0-7)
     * @param height 高度倍数 (0-7)
     */
    fun setFontSize(width: Int, height: Int): ByteArray {
        val size = (width shl 4) or height
        return byteArrayOf(GS, 0x21, size.toByte())
    }
    
    /**
     * 正常字体大小
     */
    fun fontSizeNormal(): ByteArray = setFontSize(0, 0)
    
    /**
     * 2倍大小
     */
    fun fontSizeDouble(): ByteArray = setFontSize(1, 1)
    
    /**
     * 设置粗体
     * @param bold true=粗体, false=正常
     */
    fun setBold(bold: Boolean): ByteArray {
        return byteArrayOf(ESC, 0x45, if (bold) 1 else 0)
    }
    
    /**
     * 设置下划线
     * @param underline 0=关闭, 1=1点, 2=2点
     */
    fun setUnderline(underline: Int): ByteArray {
        return byteArrayOf(ESC, 0x2D, underline.toByte())
    }
    
    /**
     * 切纸
     * @param mode 0=全切, 1=半切
     */
    fun cutPaper(mode: Int = 0): ByteArray {
        return byteArrayOf(GS, 0x56, mode.toByte())
    }
    
    /**
     * 打开钱箱（如果打印机支持）
     */
    fun openCashDrawer(): ByteArray {
        return byteArrayOf(ESC, 0x70, 0x00, 0x32, 0xFA.toByte())
    }
    
    /**
     * 打印二维码
     * @param content 二维码内容
     * @param size 模块大小 (1-16)
     */
    fun printQRCode(content: String, size: Int = 6): ByteArray {
        val data = content.toByteArray()
        val pL = (data.size + 3) % 256
        val pH = (data.size + 3) / 256
        
        return byteArrayOf(
            // 设置模块大小
            GS, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, size.toByte(),
            // 存储数据
            GS, 0x28, 0x6B, pL.toByte(), pH.toByte(), 0x31, 0x50, 0x30
        ) + data + byteArrayOf(
            // 打印二维码
            GS, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30
        )
    }
    
    /**
     * 打印条形码
     * @param content 条形码内容
     * @param type 条形码类型 (默认CODE128)
     * @param height 高度 (1-255)
     * @param width 宽度 (2-6)
     */
    fun printBarcode(content: String, type: Int = 73, height: Int = 80, width: Int = 3): ByteArray {
        val data = content.toByteArray()
        
        return byteArrayOf(
            // 设置条形码高度
            GS, 0x68, height.toByte(),
            // 设置条形码宽度
            GS, 0x77, width.toByte(),
            // 设置HRI字符位置（在条形码下方）
            GS, 0x48, 0x02,
            // 打印条形码
            GS, 0x6B, type.toByte(), data.size.toByte()
        ) + data
    }
    
    /**
     * 打印分割线
     */
    fun printDivider(char: String = "-", length: Int = 32): ByteArray {
        return printLine(char.repeat(length))
    }
    
    /**
     * 生成测试打印数据
     */
    fun generateTestPrint(): ByteArray {
        return initPrinter() +
                alignCenter() +
                fontSizeDouble() +
                setBold(true) +
                printLine("测试打印") +
                fontSizeNormal() +
                setBold(false) +
                lineFeed() +
                alignLeft() +
                printLine("设备: BLE蓝牙打印机") +
                printLine("时间: ${getCurrentDateTime()}") +
                printDivider() +
                printLine("项目1: 测试商品A ¥10.00") +
                printLine("项目2: 测试商品B ¥20.00") +
                printLine("项目3: 测试商品C ¥30.00") +
                printDivider() +
                alignRight() +
                printLine("合计: ¥60.00") +
                alignCenter() +
                lineFeed() +
                printLine("谢谢惠顾!") +
                lineFeed(3) +
                cutPaper()
    }
    
    /**
     * 生成AQ打印机测试打印数据
     */
    fun generateAQTestPrint(): ByteArray {
        return initAQPrinter() +
                lineFeed(2) +  // 额外空行
                alignCenter() +
                fontSizeDouble() +
                setBold(true) +
                printLine("测试打印") +
                fontSizeNormal() +
                setBold(false) +
                lineFeed() +
                alignLeft() +
                printLine("设备: AQ蓝牙打印机") +
                printLine("时间: ${getCurrentDateTime()}") +
                printDivider() +
                printLine("项目1: 测试商品A ¥10.00") +
                printLine("项目2: 测试商品B ¥20.00") +
                printLine("项目3: 测试商品C ¥30.00") +
                printDivider() +
                alignRight() +
                printLine("合计: ¥60.00") +
                alignCenter() +
                lineFeed() +
                printLine("谢谢惠顾!") +
                lineFeed(5) +  // 更多空行
                cutPaper()
    }
    
    private fun getCurrentDateTime(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }
}
