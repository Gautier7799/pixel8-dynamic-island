package com.island.pixel8

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class IslandService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var islandView: LinearLayout
    private lateinit var islandText: TextView
    private lateinit var batteryReceiver: BroadcastReceiver
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // إنشاء تصميم الجزيرة الديناميكية برمجياً حول ثقب كاميرا Pixel 8
        islandView = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(36, 12, 36, 12)
            
            // خلفية سوداء بحواف دائرية كاملة (Pill Shape)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#000000"))
                cornerRadius = 100f
            }
        }

        islandText = TextView(this).apply {
            text = "Pixel 8 Island"
            setTextColor(Color.WHITE)
            textSize = 12f
        }
        islandView.addView(islandText)

        // إعدادات تموضع الجزيرة فوق شاشة الهاتف
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 35 // المسافة لأسفل ثقب كاميرا Pixel 8
        }

        // تفاعل النقر على الجزيرة (توسيع / تصغير)
        islandView.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                islandView.setPadding(60, 24, 60, 24)
                islandText.text = "⚡ Dynamic Island جاهزة!"
            } else {
                islandView.setPadding(36, 12, 36, 12)
                islandText.text = "Pixel 8 Island"
            }
            windowManager.updateViewLayout(islandView, params)
        }

        windowManager.addView(islandView, params)

        // الاستماع لحالة الشحن الحقيقية للهاتف
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                 status == BatteryManager.BATTERY_STATUS_FULL
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1

                if (isCharging) {
                    islandView.setPadding(55, 20, 55, 20)
                    islandText.text = "⚡ جاري الشحن: $level%"
                } else {
                    islandView.setPadding(36, 12, 36, 12)
                    islandText.text = "Pixel 8 Island"
                }
                windowManager.updateViewLayout(islandView, params)
            }
        }
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun startForegroundServiceNotification() {
        val channelId = "island_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Dynamic Island Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Pixel 8 Dynamic Island")
                .setContentText("الجزيرة نشطة فوق الشاشة")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Pixel 8 Dynamic Island")
                .setContentText("الجزيرة نشطة فوق الشاشة")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .build()
        }

        startForeground(1001, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
        if (::islandView.isInitialized) {
            windowManager.removeView(islandView)
        }
    }
}
