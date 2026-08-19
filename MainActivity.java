package com.pixel8.dynamicisland;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);

            ScrollView scrollView = new ScrollView(this);
            scrollView.setBackgroundColor(Color.parseColor("#0F0F0F"));

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(40, 50, 40, 50);

            // ترويسة
            TextView title = new TextView(this);
            title.setText("Pixel 8 Dynamic Island");
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            root.addView(title);

            TextView subTitle = new TextView(this);
            subTitle.setText("لوحة التحكم: الشاحن • الموسيقى • الإشعارات");
            subTitle.setTextColor(Color.parseColor("#888888"));
            subTitle.setTextSize(12);
            subTitle.setPadding(0, 4, 0, 25);
            root.addView(subTitle);

            // 1. بطاقة الأذونات
            LinearLayout permCard = createCard();
            permCard.addView(createCardTitle("🛡️ أذونات التشغيل"));
            
            Button btnAccess = createCleanButton("1. تفعيل الجزيرة (ACCESSIBILITY)", "#1E88E5");
            btnAccess.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this, "تعذر فتح الإعدادات", Toast.LENGTH_SHORT).show();
                }
            });
            permCard.addView(btnAccess);

            Button btnNotif = createCleanButton("2. إذن الإشعارات والموسيقى (NOTIFICATIONS)", "#2E7D32");
            btnNotif.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this, "تعذر فتح إعدادات الإشعارات", Toast.LENGTH_SHORT).show();
                }
            });
            permCard.addView(btnNotif);
            root.addView(permCard);

            // 2. بطاقة الشاحن
            LinearLayout batteryCard = createCard();
            batteryCard.addView(createCardTitle("⚡ إعدادات الشاحن والبطارية"));
            batteryCard.addView(createSwitchRow("أنيميشن توصيل الشاحن السريع", "enable_charging", true, prefs));
            batteryCard.addView(createSwitchRow("تنبيه اكتمال الشحن (100%)", "enable_full_battery", true, prefs));
            batteryCard.addView(createSwitchRow("تنبيه انخفاض البطارية (20%)", "enable_low_battery", true, prefs));
            root.addView(batteryCard);

            // 3. بطاقة الموسيقى
            LinearLayout musicCard = createCard();
            musicCard.addView(createCardTitle("🎵 إعدادات مشغل الموسيقى"));
            musicCard.addView(createSwitchRow("إظهار شريط الموسيقى عند التشغيل", "enable_music", true, prefs));
            musicCard.addView(createSwitchRow("موجة صوتية متحركة (Waveform)", "enable_waveform", true, prefs));
            musicCard.addView(createSwitchRow("عرض اسم الفنان والأغنية", "enable_track_info", true, prefs));
            root.addView(musicCard);

            // 4. أبعاد وموضع الكاميرا
            LinearLayout positionCard = createCard();
            positionCard.addView(createCardTitle("📐 أبعاد وموضع الكاميرا"));

            int currentY = prefs.getInt("island_y", 12);
            TextView txtY = new TextView(this);
            txtY.setText("المسافة من الأعلى: " + currentY + " px");
            txtY.setTextColor(Color.parseColor("#AAAAAA"));
            txtY.setTextSize(12);
            txtY.setPadding(0, 5, 0, 5);
            positionCard.addView(txtY);

            SeekBar seekY = new SeekBar(this);
            seekY.setMax(60);
            seekY.setProgress(currentY);
            seekY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    txtY.setText("المسافة من الأعلى: " + progress + " px");
                    prefs.edit().putInt("island_y", progress).apply();
                    if (DynamicIslandService.instance != null) {
                        DynamicIslandService.instance.updateDimensions(progress, prefs.getInt("island_width", 280));
                    }
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            positionCard.addView(seekY);

            int currentW = prefs.getInt("island_width", 280);
            TextView txtW = new TextView(this);
            txtW.setText("عرض الكبسولة في وضع السكون: " + currentW + " px");
            txtW.setTextColor(Color.parseColor("#AAAAAA"));
            txtW.setTextSize(12);
            txtW.setPadding(0, 15, 0, 5);
            positionCard.addView(txtW);

            SeekBar seekW = new SeekBar(this);
            seekW.setMax(200); // من 180 إلى 380 (القيمة = 180 + progress)
            seekW.setProgress(Math.max(0, currentW - 180));
            seekW.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int realW = 180 + progress;
                    txtW.setText("عرض الكبسولة في وضع السكون: " + realW + " px");
                    prefs.edit().putInt("island_width", realW).apply();
                    if (DynamicIslandService.instance != null) {
                        DynamicIslandService.instance.updateDimensions(prefs.getInt("island_y", 12), realW);
                    }
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            positionCard.addView(seekW);

            root.addView(positionCard);

            // زر التجربة الفورية
            Button btnTest = createCleanButton("✨ تجربة إشعار تفاعلي", "#242426");
            btnTest.setTextColor(Color.parseColor("#00E676"));
            btnTest.setOnClickListener(v -> {
                if (DynamicIslandService.instance != null) {
                    DynamicIslandService.instance.showIsland("✨ تجربة ناجحة", "الجزيرة تعمل بكفاءة على Pixel 8 🔥", "🚀", Color.parseColor("#00E676"));
                    Toast.makeText(this, "تم تنشيط الجزيرة!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "يرجى تفعيل الزر الأزرق رقم 1 أولاً", Toast.LENGTH_SHORT).show();
                }
            });
            root.addView(btnTest);

            scrollView.addView(root);
            setContentView(scrollView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LinearLayout createCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(35, 28, 35, 28);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#1C1C1E"));
        bg.setCornerRadius(26);
        card.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 20);
        card.setLayoutParams(p);
        return card;
    }

    private TextView createCardTitle(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setPadding(0, 0, 0, 12);
        return tv;
    }

    private View createSwitchRow(String title, String prefKey, boolean defVal, SharedPreferences prefs) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 8, 0, 8);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextColor(Color.parseColor("#CCCCCC"));
        label.setTextSize(13);
        label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Switch sw = new Switch(this);
        sw.setChecked(prefs.getBoolean(prefKey, defVal));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean(prefKey, isChecked).apply());

        row.addView(label);
        row.addView(sw);
        return row;
    }

    private Button createCleanButton(String text, String colorHex) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(12);
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(colorHex));
        d.setCornerRadius(18);
        b.setBackground(d);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 115);
        p.setMargins(0, 4, 0, 8);
        b.setLayoutParams(p);
        return b;
    }
}
