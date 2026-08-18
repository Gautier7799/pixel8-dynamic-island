package com.island.pixel8

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class IslandService : Service() {

    private var windowManager: WindowManager? = null
    private var islandContainer: LinearLayout? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createAndShowIsland()
        return START_STICKY
    }

    private fun createAndShowIsland() {
        if (islandContainer != null) {
            Toast.makeText(this, "الجزيرة معروضة بالفعل!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            // حاوية الجزيرة بتصميم كبسولة سوداء مقوسة لـ Pixel 8
            islandContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                
                // خلفية سوداء نقية مع زوايا دائرية بالكامل
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#000000"))
                    cornerRadius = 100f
                    setStroke(2, Color.parseColor("#333333")) // إطار خفيف لزيادة الوضوح
                }

                // نص وأيقونة داخل الجزيرة
                val title = TextView(context).apply {
                    text = "🏝️ Pixel 8 Island"
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    gravity = Gravity.CENTER
                }
                addView(title)

                setOnClickListener {
                    Toast.makeText(context, "تم النقر على جزيرة Pixel 8! 🔥", Toast.LENGTH_SHORT).show()
                }
            }

            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            // أبعاد ثابتة وصريحة لتظهر الجزيرة بشكل كبسولة واضحة حول الكاميرا
            val params = WindowManager.LayoutParams(
                460, // العرض بالبكسل
                120, // الارتفاع بالبكسل
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 15 // المسافة لأسفل مباشرة حول كاميرا الشاشة
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            windowManager?.addView(islandContainer, params)
            Toast.makeText(this, "تم إظهار الجزيرة بنجاح! 🚀", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "خطأ في العرض: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        islandContainer?.let {
            windowManager?.removeView(it)
            islandContainer = null
        }
    }
}
