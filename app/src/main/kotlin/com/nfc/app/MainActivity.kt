package com.nfc.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nfc.app.nfc.NFCReader
import com.nfc.app.nfc.NFCWriter
import com.nfc.app.ui.RecordAdapter

class MainActivity : AppCompatActivity() {

    private val TAG = "NFCApp"
    private lateinit var recordAdapter: RecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "=== onCreate started ===")
        
        try {
            // 使用LayoutInflater加载布局，避免R类问题
            val layoutResId = resources.getIdentifier("activity_main", "layout", packageName)
            Log.d(TAG, "Layout resource ID: $layoutResId")
            
            if (layoutResId == 0) {
                Log.e(TAG, "Cannot find activity_main layout")
                createFallbackLayout()
                return
            }
            
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(layoutResId, null)
            setContentView(view)
            
            Log.d(TAG, "Layout inflated successfully")
            
            // 初始化组件
            initializeComponents()
            
            Log.d(TAG, "=== onCreate completed successfully ===")
            Toast.makeText(this, "✓ NFC应用启动成功", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "!!! FATAL ERROR !!!", e)
            e.printStackTrace()
            Toast.makeText(this, "启动失败: ${e.message}", Toast.LENGTH_LONG).show()
            createFallbackLayout()
        }
    }
    
    private fun initializeComponents() {
        try {
            // 初始化NFC
            Log.d(TAG, "Initializing NFC components")
            val nfcReader = NFCReader()
            val nfcWriter = NFCWriter()
            
            // 获取视图
            val tvNfcid = findViewById<TextView>(getResId("tv_nfcid", "id"))
            val btnWrite = findViewById<LinearLayout>(getResId("btn_write", "id"))
            val btnRead = findViewById<LinearLayout>(getResId("btn_read", "id"))
            val btnPrint = findViewById<LinearLayout>(getResId("btn_print", "id"))
            val btnUpload = findViewById<LinearLayout>(getResId("btn_upload", "id"))
            val recyclerView = findViewById<RecyclerView>(getResId("recycler_view_records", "id"))
            
            // 设置RecyclerView
            recordAdapter = RecordAdapter()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = recordAdapter
            
            // 设置按钮点击监听
            btnWrite.setOnClickListener {
                Toast.makeText(this, "✏️ 写入模式：请靠近NFC标签", Toast.LENGTH_SHORT).show()
            }
            
            btnRead.setOnClickListener {
                Toast.makeText(this, "📖 读取模式：请靠近NFC标签", Toast.LENGTH_SHORT).show()
            }
            
            btnPrint.setOnClickListener {
                Toast.makeText(this, "🖨️ 打印功能待实现", Toast.LENGTH_SHORT).show()
            }
            
            btnUpload.setOnClickListener {
                Toast.makeText(this, "☁️ 上传功能待实现", Toast.LENGTH_SHORT).show()
            }
            
            tvNfcid.text = "NFC已就绪"
            Log.d(TAG, "Components initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing components", e)
            Toast.makeText(this, "部分功能初始化失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getResId(name: String, type: String): Int {
        val id = resources.getIdentifier(name, type, packageName)
        Log.d(TAG, "Resource $name ($type) ID: $id")
        return id
    }
    
    private fun createFallbackLayout() {
        try {
            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(50, 50, 50, 50)
            }
            
            val textView = TextView(this).apply {
                text = "NFC 读写系统\n\n资源加载失败，使用备用界面"
                textSize = 20f
            }
            
            layout.addView(textView)
            setContentView(layout)
            
            Log.d(TAG, "Fallback layout created")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create fallback layout", e)
        }
    }
}
