package com.island.pixel8

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // واجهة برمجية سريعة ومباشرة بدون الحاجة لملفات xml
        val button = Button(this).apply {
            text = "تفعيل جزيرة Pixel 8 الديناميكية"
            textSize = 18f
            setOnClickListener {
                checkPermissionAndStart()
            }
        }
        setContentView(button)
    }

    private fun checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "يرجى تفعيل إذن الظهور في المقدمة للتطبيق", Toast.LENGTH_LONG).show()
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            val intent = Intent(this, IslandService::class.java)
            startService(intent)
        }
    }
}
