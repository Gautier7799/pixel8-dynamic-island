package com.island.pixel8

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class IslandService : AccessibilityService() {

    private var windowManager: WindowManager? = null
    private var islandContainer: LinearLayout? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        showIsland()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    private fun showIsland() {
        if (islandContainer != null) return

        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            islandContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#000000"))
                    cornerRadius = 100f
                    setStroke(2, Color.parseColor("#444444"))
                }

                val title = TextView(context).apply {
                    text = "🏝️ Pixel 8 Island"
                    setTextColor(Color.WHITE)
                    textSize = 13f
                    gravity = Gravity.CENTER
                }
                addView(title)

                setOnClickListener {
                    Toast.makeText(context, "الجزيرة نشطة بنجاح! 🔥", Toast.LENGTH_SHORT).show()
                }
            }

            val params = WindowManager.LayoutParams(
                460,
                120,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 10
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            windowManager?.addView(islandContainer, params)
            Toast.makeText(this, "تم إطلاق الجزيرة التفاعلية! 🚀", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
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
