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
import android.widget.CompoundButton;
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
        scrollView.setBackgroundColor(Color.parseColor("#0F0F0F")); // خلفية سوداء ناعمة فاخرة

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 50, 40, 50);

        // ترويسة أنيقة وبسيطة
        TextView title = new TextView(this);
        title.setText("Pixel 8 Island");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(title);

        TextView subTitle = new TextView(this);
        subTitle.setText("تخصيص نقي ومباشر بدون تعقيد");
        subTitle.setTextColor(Color.parseColor("#777777"));
        subTitle.setTextSize(13);
        subTitle.setPadding(0, 4, 0, 30);
        root.addView(subTitle);

        // 1. بطاقة الأذونات الأساسية
        LinearLayout permCard = createCard();
        permCard.addView(createCardTitle("الأذونات الأساسية"));

        Button btnAccess = createCleanButton("إذن الظهور فوق الشاشة (Accessibility)", "#1E88E5");
        btnAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        permCard.addView(btnAccess);

        Button btnNotif = createCleanButton("إذن رصد الإشعارات (Notifications)", "#2E7D32");
        btnNotif.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        permCard.addView(btnNotif);

        root.addView(permCard);

        // 2. بطاقة الميزات وتفعيل ما تريده فقط
        LinearLayout featuresCard = createCard();
        featuresCard.addView(createCardTitle("الميزات النشطة"));

        featuresCard.addView(createSwitchRow("⚡ تنبيهات الشحن والبطارية", "enable_battery", true, prefs));
        featuresCard.addView(createSwitchRow("🎵 شريط الموسيقى الحي", "enable_music", true, prefs));
        featuresCard.addView(createSwitchRow("💬 إشعارات التطبيقات", "enable_notifications", true, prefs));

        root.addView(featuresCard);

        // 3. بطاقة ضبط موضع الكاميرا بدقة
        LinearLayout positionCard = createCard();
        positionCard.addView(createCardTitle("محاذاة ثقب الكاميرا"));

        int currentY = prefs.getInt("island_y", 12);
        TextView txtY = new TextView(this);
        txtY.setText("المسافة من الأعلى: " + currentY + " px");
        txtY.setTextColor(Color.parseColor("#AAAAAA"));
        txtY.setTextSize(12);
        txtY.setPadding(0, 10, 0, 5);
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
        txtW.setText("عرض الكبسولة الصامتة: " + currentW + " px");
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
                txtW.setText("عرض الكبسولة الصامتة: " + progress + " px");
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
        card.setPadding(35, 30, 35, 30);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#1C1C1E")); // رمادي داكن فخم بتصميم البطاقات
        bg.setCornerRadius(28);
        card.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 24);
        card.setLayoutParams(p);
        return card;
    }

    private TextView createCardTitle(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setPadding(0, 0, 0, 15);
        return tv;
    }

    private View createSwitchRow(String title, String prefKey, boolean defVal, SharedPreferences prefs) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 12, 0, 12);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextColor(Color.parseColor("#DDDDDD"));
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
        d.setCornerRadius(20);
        b.setBackground(d);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 115);
        p.setMargins(0, 6, 0, 10);
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
