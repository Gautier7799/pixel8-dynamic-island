package com.pixel8.dynamicisland

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_UPDATE_CONFIG = "com.pixel8.dynamicisland.UPDATE_CONFIG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("island_prefs", MODE_PRIVATE)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#0F0F0F"))
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 50, 40, 50)
        }

        // ترويسة أنيقة
        val title = TextView(this).apply {
            text = "Pixel 8 Dynamic Island"
            setTextColor(Color.WHITE)
            textSize = 22f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        root.addView(title)

        val subTitle = TextView(this).apply {
            text = "تحكم شامل: الشاحن • الموسيقى • الإشعارات"
            setTextColor(Color.parseColor("#888888"))
            textSize = 12f
            setPadding(0, 4, 0, 25)
        }
        root.addView(subTitle)

        // 1. بطاقة الأذونات
        val permCard = createCard().apply {
            addView(createCardTitle("🛡️ أذونات التشغيل"))
            addView(createCleanButton("1. إذن الظهور فوق الشاشة (Accessibility)", "#1E88E5").apply {
                setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
            })
            addView(createCleanButton("2. إذن قراءة الإشعارات والموسيقى", "#2E7D32").apply {
                setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
            })
        }
        root.addView(permCard)

        // 2. بطاقة الشاحن والبطارية
        val batteryCard = createCard().apply {
            addView(createCardTitle("⚡ إعدادات الشاحن والبطارية"))
            addView(createSwitchRow("أنيميشن توصيل الشاحن السريع", "enable_charging", true, prefs))
            addView(createSwitchRow("تنبيه اكتمال الشحن (100%)", "enable_full_battery", true, prefs))
            addView(createSwitchRow("تنبيه انخفاض البطارية (20%)", "enable_low_battery", true, prefs))
        }
        root.addView(batteryCard)

        // 3. بطاقة الموسيقى
        val musicCard = createCard().apply {
            addView(createCardTitle("🎵 إعدادات مشغل الموسيقى"))
            addView(createSwitchRow("إظهار شريط الموسيقى عند التشغيل", "enable_music", true, prefs))
            addView(createSwitchRow("موجة صوتية متحركة (Waveform)", "enable_waveform", true, prefs))
            addView(createSwitchRow("عرض تفاصيل الأغنية والفنان", "enable_track_info", true, prefs))
        }
        root.addView(musicCard)

        // 4. بطاقة محاذاة الكاميرا
        val positionCard = createCard().apply {
            addView(createCardTitle("📐 أبعاد وموضع الكاميرا"))

            val currentY = prefs.getInt("island_y", 12)
            val txtY = TextView(context).apply {
                text = "المسافة من الأعلى: $currentY px"
                setTextColor(Color.parseColor("#AAAAAA"))
                textSize = 12f
                setPadding(0, 5, 0, 5)
            }
            addView(txtY)

            val seekY = SeekBar(context).apply {
                max = 60
                progress = currentY
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        txtY.text = "المسافة من الأعلى: $progress px"
                        prefs.edit().putInt("island_y", progress).apply()
                        sendLiveUpdate(progress, prefs.getInt("island_width", 280))
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }
            addView(seekY)

            val currentW = prefs.getInt("island_width", 280)
            val txtW = TextView(context).apply {
                text = "عرض الكبسولة في وضع السكون: $currentW px"
                setTextColor(Color.parseColor("#AAAAAA"))
                textSize = 12f
                setPadding(0, 15, 0, 5)
            }
            addView(txtW)

            val seekW = SeekBar(context).apply {
                min = 180
                max = 380
                progress = currentW
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        txtW.text = "عرض الكبسولة في وضع السكون: $progress px"
                        prefs.edit().putInt("island_width", progress).apply()
                        sendLiveUpdate(prefs.getInt("island_y", 12), progress)
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }
            addView(seekW)
        }
        root.addView(positionCard)

        // زر التجربة
        val btnTest = createCleanButton("✨ تجربة إشعار تفاعلي", "#242426").apply {
            setTextColor(Color.parseColor("#00E676"))
            setOnClickListener {
                val intent = Intent(NotificationService.ACTION_NEW_NOTIFICATION).apply {
                    putExtra("title", "رسالة جديدة")
                    putExtra("text", "إشعارات الجزيرة تعمل بامتياز على Pixel 8 🔥")
                }
                sendBroadcast(intent)
                Toast.makeText(this@MainActivity, "تم إرسال إشعار تجريبي!", Toast.LENGTH_SHORT).show()
            }
        }
        root.addView(btnTest)

        scrollView.addView(root)
        setContentView(scrollView)
    }

    private fun createCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35, 28, 35, 28)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1C1C1E"))
                cornerRadius = 26f
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 20)
            }
        }
    }

    private fun createCardTitle(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 12)
        }
    }

    private fun createSwitchRow(title: String, prefKey: String, defVal: Boolean, prefs: SharedPreferences): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 8, 0, 8)

            val label = TextView(context).apply {
                text = title
                setTextColor(Color.parseColor("#CCCCCC"))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val sw = Switch(context).apply {
                isChecked = prefs.getBoolean(prefKey, defVal)
                setOnCheckedChangeListener { _, isChecked ->
                    prefs.edit().putBoolean(prefKey, isChecked).apply()
                }
            }

            addView(label)
            addView(sw)
        }
    }

    private fun createCleanButton(text: String, colorHex: String): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 12f
            background = GradientDrawable().apply {
                setColor(Color.parseColor(colorHex))
                cornerRadius = 18f
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 115
            ).apply {
                setMargins(0, 4, 0, 8)
            }
        }
    }

    private fun sendLiveUpdate(y: Int, width: Int) {
        val intent = Intent(ACTION_UPDATE_CONFIG).apply {
            putExtra("island_y", y)
            putExtra("island_width", width)
        }
        sendBroadcast(intent)
    }
}
