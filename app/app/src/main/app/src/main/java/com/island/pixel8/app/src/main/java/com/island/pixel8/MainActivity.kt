package com.pixel8.dynamicisland;

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
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_UPDATE_CONFIG = "com.pixel8.dynamicisland.UPDATE_CONFIG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#0F0F0F"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 50, 40, 50);

        // ترويسة أنيقة
        TextView title = new TextView(this);
        title.setText("Pixel 8 Dynamic Island");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(title);

        TextView subTitle = new TextView(this);
        subTitle.setText("إعدادات الشحن، الموسيقى، والإشعارات المتقدمة");
        subTitle.setTextColor(Color.parseColor("#888888"));
        subTitle.setTextSize(12);
        subTitle.setPadding(0, 4, 0, 25);
        root.addView(subTitle);

        // 1. بطاقة الأذونات
        LinearLayout permCard = createCard();
        permCard.addView(createCardTitle("🛡️ أذونات التشغيل"));
        Button btnAccess = createCleanButton("1. تفعيل الجزيرة في الخلفية (Accessibility)", "#1E88E5");
        btnAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        permCard.addView(btnAccess);

        Button btnNotif = createCleanButton("2. إذن الإشعارات والموسيقى (Notifications)", "#2E7D32");
        btnNotif.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        permCard.addView(btnNotif);
        root.addView(permCard);

        // 2. بطاقة إعدادات الشاحن والبطارية
        LinearLayout batteryCard = createCard();
        batteryCard.addView(createCardTitle("⚡ إعدادات الشاحن والبطارية"));
        batteryCard.addView(createSwitchRow("أنيميشن توصيل الشاحن السريع", "enable_charging_anim", true, prefs));
        batteryCard.addView(createSwitchRow("تنبيه اكتمال الشحن (100%)", "enable_battery_full", true, prefs));
        batteryCard.addView(createSwitchRow("تنبيه انخفاض البطارية (أقل من 20%)", "enable_battery_low", true, prefs));
        root.addView(batteryCard);

        // 3. بطاقة إعدادات الموسيقى والوسائط
        LinearLayout musicCard = createCard();
        musicCard.addView(createCardTitle("🎵 إعدادات مشغل الموسيقى"));
        musicCard.addView(createSwitchRow("إظهار شريط الموسيقى عند التشغيل", "enable_music", true, prefs));
        musicCard.addView(createSwitchRow("موجة صوتية متحركة (Waveform)", "enable_music_wave", true, prefs));
        musicCard.addView(createSwitchRow("عرض اسم الفنان والأغنية", "enable_music_info", true, prefs));
        root.addView(musicCard);

        // 4. بطاقة إعدادات الإشعارات والتطبيقات
        LinearLayout notifCard = createCard();
        notifCard.addView(createCardTitle("💬 إعدادات الإشعارات"));
        notifCard.addView(createSwitchRow("توسيع الجزيرة عند وصول رسالة جديدة", "enable_notifications", true, prefs));
        notifCard.addView(createSwitchRow("إخفاء الإشعار تلقائياً بعد 4 ثوانٍ", "auto_hide_notif", true, prefs));
        root.addView(notifCard);

        // 5. بطاقة محاذاة الكاميرا
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
                sendLiveUpdate(progress, prefs.getInt("island_width", 260));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        positionCard.addView(seekY);

        int currentW = prefs.getInt("island_width", 260);
        TextView txtW = new TextView(this);
        txtW.setText("عرض الكبسولة في وضع السكون: " + currentW + " px");
        txtW.setTextColor(Color.parseColor("#AAAAAA"));
        txtW.setTextSize(12);
        txtW.setPadding(0, 15, 0, 5);
        positionCard.addView(txtW);

        SeekBar seekW = new SeekBar(this);
        seekW.setMin(180);
        seekW.setMax(380);
        seekW.setProgress(currentW);
        seekW.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtW.setText("عرض الكبسولة في وضع السكون: " + progress + " px");
                prefs.edit().putInt("island_width", progress).apply();
                sendLiveUpdate(prefs.getInt("island_y", 12), progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        positionCard.addView(seekW);

        root.addView(positionCard);

        scrollView.addView(root);
        setContentView(scrollView);
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
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(prefKey, isChecked).apply();
        });

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

    private void sendLiveUpdate(int y, int width) {
        Intent intent = new Intent(ACTION_UPDATE_CONFIG);
        intent.putExtra("island_y", y);
        intent.putExtra("island_width", width);
        sendBroadcast(intent);
    }
}
