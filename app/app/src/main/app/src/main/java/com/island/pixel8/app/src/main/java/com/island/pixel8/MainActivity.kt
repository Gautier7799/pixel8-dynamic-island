package com.island.pixel8

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // إنشاء واجهة تحكم بسيطة وأنيقة
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(android.graphics.Color.parseColor("#121212"))
            setPadding(48, 48, 48, 48)
        }

        val titleText = TextView(this).apply {
            text = "🏝️ Pixel 8 Dynamic Island"
            textSize = 22f
            setTextColor(android.graphics.Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }

        val descText = TextView(this).apply {
            text = "اضغط على الزر أدناه لمنح الإذن وتفعيل الجزيرة التفاعلية فوق ثقب الكاميرا الحقيقي."
            textSize = 14f
            setTextColor(android.graphics.Color.LTGRAY)
            gravity = android.view.Gravity.CENTER
            setPadding(0, 24, 0, 48)
        }

        val btnStart = Button(this).apply {
            text = "🚀 تفعيل الجزيرة فوق الشاشة"
            setBackgroundColor(android.graphics.Color.parseColor("#3B82F6"))
            setTextColor(android.graphics.Color.WHITE)
            setPadding(32, 16, 32, 16)
            setOnClickListener {
                checkPermissionAndStart()
            }
        }

        layout.addView(titleText)
        layout.addView(descText)
        layout.addView(btnStart)
        setContentView(layout)
    }

    private fun checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "يرجى تفعيل إذن الظهور فوق التطبيقات", Toast.LENGTH_LONG).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                return
            }
        }

        val serviceIntent = Intent(this, IslandService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        Toast.makeText(this, "تم تفعيل الجزيرة بنجاح!", Toast.LENGTH_SHORT).show()
    }
}
