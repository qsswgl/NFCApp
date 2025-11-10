package com.example.bleprinter.sdk

import android.app.Application
import android.content.Context
import android.util.Log
import com.puqu.sdk.Base.PuQuPrint
import com.puqu.sdk.PuQuPrintManager

/**
 * PUQU打印机SDK管理器包装类
 * 用于简化SDK调用和管理连接状态
 */
class PuQuPrinterManager(context: Context) {
    
    private val TAG = "PuQuPrinterManager"
    private val printManager: PuQuPrintManager
    
    // 连接状态回调
    private var onConnectSuccess: (() -> Unit)? = null
    private var onConnectFailed: (() -> Unit)? = null
    private var onConnectClosed: (() -> Unit)? = null
    
    init {
        // 获取SDK打印管理器实例
        val application = context.applicationContext as Application
        try {
            printManager = PuQuPrintManager(application)
            Log.d(TAG, "SDK打印管理器初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "SDK打印管理器初始化失败", e)
            throw e
        }
        
        // 设置连接成功回调
        printManager.setConnectSuccess(object : PuQuPrint.ConnectSuccess {
            override fun success() {
                Log.d(TAG, "SDK回调: 连接成功")
                onConnectSuccess?.invoke()
            }
        })
        
        // 设置连接失败回调
        printManager.setConnectFailed(object : PuQuPrint.ConnectFailed {
            override fun failed() {
                Log.e(TAG, "SDK回调: 连接失败")
                onConnectFailed?.invoke()
            }
        })
        
        // 设置连接关闭回调
        printManager.setConnectClosed(object : PuQuPrint.ConnectClosed {
            override fun closed() {
                Log.d(TAG, "SDK回调: 连接关闭")
                onConnectClosed?.invoke()
            }
        })
    }
    
    /**
     * 连接到打印机
     * @param address 蓝牙设备地址 (MAC地址)
     */
    fun connect(address: String) {
        Log.d(TAG, "========== SDK连接请求 ==========")
        Log.d(TAG, "目标设备地址: $address")
        
        try {
            // 检查是否已连接
            if (printManager.isConnected) {
                Log.w(TAG, "打印机已连接,先断开旧连接")
                printManager.closePrinter()
                Thread.sleep(500) // 等待断开完成
            }
            
            Log.d(TAG, "调用SDK openPrinter...")
            Log.d(TAG, "注意: SDK需要设备已经在系统蓝牙中配对")
            printManager.openPrinter(address)
            Log.d(TAG, "SDK openPrinter调用完成,等待回调...")
            Log.d(TAG, "通常6秒左右会收到连接结果回调")
        } catch (e: Exception) {
            Log.e(TAG, "连接打印机异常: ${e.message}", e)
            e.printStackTrace()
            onConnectFailed?.invoke()
        }
    }
    
    /**
     * 断开连接
     */
    fun disconnect() {
        Log.d(TAG, "断开连接")
        try {
            printManager.closePrinter()
        } catch (e: Exception) {
            Log.e(TAG, "断开连接异常", e)
        }
    }
    
    /**
     * 检查是否已连接
     */
    fun isConnected(): Boolean {
        return try {
            printManager.isConnected
        } catch (e: Exception) {
            Log.e(TAG, "检查连接状态异常", e)
            false
        }
    }
    
    /**
     * 准备打印内容(但不执行打印)
     * 这个方法应该在连接成功后立即调用
     */
    fun preparePrintJob() {
        try {
            Log.d(TAG, "========== 准备打印任务 ==========")
            
            // 注意: SDK连接成功回调后 isConnected 可能还是 false
            // 所以这里不检查连接状态,直接尝试准备
            Log.d(TAG, "SDK isConnected状态: ${printManager.isConnected}")
            
            Log.d(TAG, "调用 startJob(400, -1) 准备打印缓冲区")
            // 开始打印任务 (宽度400, 高度-1表示自动)
            printManager.startJob(400, -1)
            
            Log.d(TAG, "准备完成,等待打印命令")
        } catch (e: Exception) {
            Log.e(TAG, "准备打印任务异常: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    /**
     * 打印测试页
     * 重要: 每次打印都需要重新调用startJob
     */
    fun printTestPage(): Boolean {
        return try {
            Log.d(TAG, "========== 开始打印测试页 ==========")
            Log.d(TAG, "SDK isConnected状态: ${printManager.isConnected}")
            
            // 每次打印都重新开始一个打印任务
            Log.d(TAG, "步骤1: 开始新的打印任务 startJob(400, -1)")
            printManager.startJob(400, -1)
            
            Log.d(TAG, "步骤2: 设置标题样式")
            // 标题 - 居中对齐,粗体,大字体
            printManager.setLeft(0)
            printManager.setFontSize(36f)
            printManager.setAlignment(1)  // 1=居中
            printManager.setBold(true)
            printManager.addText("打印测试页")
            printManager.addBlank(16)
            
            Log.d(TAG, "步骤3: 打印内容")
            // 内容 - 左对齐,正常字体
            printManager.setLeft(16)
            printManager.setBold(false)
            printManager.setFontSize(24f)
            printManager.setAlignment(0)  // 0=左对齐
            printManager.addText("设备测试")
            printManager.addText("时间: ${getCurrentTime()}")
            printManager.addText("- - - - - - - - - - - - - - - -")
            
            // 测试内容
            printManager.addText("这是测试打印内容")
            printManager.addText("数字: 1234567890")
            printManager.addText("英文: ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            printManager.addText("小写: abcdefghijklmnopqrstuvwxyz")
            printManager.addText("- - - - - - - - - - - - - - - -")
            
            // 测试中文
            printManager.addText("中文测试: 你好世界")
            printManager.addText("符号: !@#$%^&*()")
            
            Log.d(TAG, "步骤4: 添加条形码")
            // 添加条形码
            printManager.setLeft(50)
            printManager.addBlank(24)
            printManager.addBarCode("12345678", 200, 100)
            printManager.setLeft(16)
            printManager.addBlank(80)
            
            Log.d(TAG, "步骤5: 在后台线程执行打印 printJob()")
            // 在后台线程执行打印 - 这很重要!
            Thread {
                try {
                    Log.d(TAG, "后台线程: 开始执行printJob()...")
                    printManager.printJob()
                    Log.d(TAG, "后台线程: printJob()执行完成")
                } catch (e: Exception) {
                    Log.e(TAG, "后台线程: printJob()执行异常", e)
                }
            }.start()
            
            Log.d(TAG, "========== 测试页打印命令已启动 ==========")
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印测试页异常: ${e.message}", e)
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 打印简单文本
     * @param text 要打印的文本
     */
    fun printSimpleText(text: String): Boolean {
        return try {
            Log.d(TAG, "打印简单文本: $text")
            
            // 开始打印任务
            printManager.startJob(400, -1)
            
            // 设置样式
            printManager.setLeft(16)
            printManager.setFontSize(24f)
            printManager.setAlignment(0)
            printManager.setBold(false)
            
            // 添加文本
            printManager.addText(text)
            printManager.addBlank(80)
            
            // 执行打印
            printManager.printJob()
            
            Log.d(TAG, "文本打印完成")
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印文本异常", e)
            false
        }
    }
    
    /**
     * 设置连接成功回调
     */
    fun setOnConnectSuccess(callback: () -> Unit) {
        onConnectSuccess = callback
    }
    
    /**
     * 设置连接失败回调
     */
    fun setOnConnectFailed(callback: () -> Unit) {
        onConnectFailed = callback
    }
    
    /**
     * 设置连接关闭回调
     */
    fun setOnConnectClosed(callback: () -> Unit) {
        onConnectClosed = callback
    }
    
    /**
     * 获取当前时间字符串
     */
    private fun getCurrentTime(): String {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return format.format(java.util.Date())
    }
}
