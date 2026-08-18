package com.pixel8.dynamicisland

import android.accessibilityservice.AccessibilityService
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import android.widget.TextView

class DynamicIslandService : AccessibilityService() {

    companion object {
        var instance: DynamicIslandService? = null
    }

    private var windowManager: WindowManager? = null
    private var islandContainer: LinearLayout? = null
    private var leftIcon: TextView? = null
    private var mainText: TextView? = null
    private var rightText: TextView? = null
    private var params: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isExpanded = false
    private var lastChargingState = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                             status == BatteryManager.BATTERY_STATUS_FULL
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1

            if (isCharging != lastChargingState) {
                lastChargingState = isCharging
                if (isCharging) {
                    showIsland("⚡ جاري الشحن", "$level%", "🔋", Color.parseColor("#00E676"))
                } else {
                    showIsland("تم فصل الشاحن", "", "🔋", Color.LTGRAY)
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        // رصد مباشر لحالة البطارية والشحن
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)

        createIsland()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    private fun createIsland() {
        if (islandContainer != null) return

        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val prefs = getSharedPreferences("island_prefs", MODE_PRIVATE)
            val baseWidth = prefs.getInt("island_width", 280)
            val customY = prefs.getInt("island_y", 12)

            islandContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(25, 0, 25, 0)
                background = GradientDrawable().apply {
                    setColor(Color.BLACK)
                    cornerRadius = 100f
                    setStroke(1, Color.parseColor("#222222"))
                }
            }

            leftIcon = TextView(this).apply {
                textSize = 15f
                visibility = View.GONE
            }
            islandContainer?.addView(leftIcon)

            mainText = TextView(this).apply {
                setTextColor(Color.WHITE)
                textSize = 12f
                isSingleLine = true
                setPadding(12, 0, 12, 0)
                visibility = View.GONE
            }
            islandContainer?.addView(mainText)

            rightText = TextView(this).apply {
                textSize = 13f
                visibility = View.GONE
            }
            islandContainer?.addView(rightText)

            params = WindowManager.LayoutParams(
                baseWidth,
                90,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = customY
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            windowManager?.addView(islandContainer, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showIsland(title: String, subtitle: String, symbol: String, color: Int) {
        handler.post {
            isExpanded = true
            leftIcon?.visibility = if (symbol.isNotEmpty()) View.VISIBLE else View.GONE
            leftIcon?.text = symbol

            mainText?.visibility = View.VISIBLE
            mainText?.text = if (subtitle.isNotEmpty()) "$title: $subtitle" else title

            rightText?.visibility = if (symbol.isNotEmpty()) View.VISIBLE else View.GONE
            rightText?.text = symbol
            rightText?.setTextColor(color)

            animateIsland(params?.width ?: 280, 680, params?.height ?: 90, 120)

            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ collapseIsland() }, 3800)
        }
    }

    private fun collapseIsland() {
        val prefs = getSharedPreferences("island_prefs", MODE_PRIVATE)
        val baseWidth = prefs.getInt("island_width", 280)
        leftIcon?.visibility = View.GONE
        mainText?.visibility = View.GONE
        rightText?.visibility = View.GONE
        animateIsland(params?.width ?: 680, baseWidth, params?.height ?: 120, 90)
        isExpanded = false
    }

    fun updateDimensions(y: Int, width: Int) {
        if (islandContainer != null && windowManager != null && !isExpanded) {
            params?.y = y
            params?.width = width
            try {
                windowManager?.updateViewLayout(islandContainer, params)
            } catch (ignored: Exception) {}
        }
    }

    private fun animateIsland(fromW: Int, toW: Int, fromH: Int, toH: Int) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 240
            addUpdateListener {
                val f = it.animatedFraction
                params?.width = (fromW + (toW - fromW) * f).toInt()
                params?.height = (fromH + (toH - fromH) * f).toInt()
                try {
                    windowManager?.updateViewLayout(islandContainer, params)
                } catch (ignored: Exception) {}
            }
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        try { unregisterReceiver(batteryReceiver) } catch (ignored: Exception) {}
        if (islandContainer != null && windowManager != null) {
            windowManager?.removeView(islandContainer)
            islandContainer = null
        }
    }
}
