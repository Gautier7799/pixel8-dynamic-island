package com.pixel8.dynamicisland;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#090A0F"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(20), dpToPx(36), dpToPx(20), dpToPx(36));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(root);

        // Header
        TextView title = new TextView(this);
        title.setText("Pixel 8 Dynamic Island");
        title.setTextColor(Color.WHITE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("إعدادات وتخصيص حجم وموضع الجزيرة 🏝️");
        subtitle.setTextColor(Color.parseColor("#38BDF8"));
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        LinearLayout.LayoutParams subP = new LinearLayout.LayoutParams(-2, -2);
        subP.setMargins(0, dpToPx(4), 0, dpToPx(20));
        subtitle.setLayoutParams(subP);
        root.addView(subtitle);

        // Section: Main Switch
        root.addView(createSectionTitle("الحالة والتشغيل"));
        root.addView(createButton("⚡ إعادة تشغيل / تفعيل الجزيرة", "#0284C7", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName())));
                    } else {
                        startService(new Intent(MainActivity.this, DynamicIslandService.class));
                        Toast.makeText(MainActivity.this, "تم تفعيل الجزيرة بالحجم المخصص!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }));

        // Section: Calibration Sliders (تخصيص الحجم والموضع)
        root.addView(createSectionTitle("تخصيص موضع وأبعاد الكاميرا (Pixel 8)"));

        // 1. عرض الجزيرة (Width)
        final TextView tvW = new TextView(this);
        int curW = prefs.getInt("compact_width", 125);
        tvW.setText("عرض الجزيرة: " + curW + " dp");
        tvW.setTextColor(Color.LTGRAY);
        root.addView(tvW);

        SeekBar sbW = createSeekBar(80, 260, curW, new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvW.setText("عرض الجزيرة: " + progress + " dp");
                prefs.edit().putInt("compact_width", progress).apply();
                sendBroadcast(new Intent("com.pixel8.dynamicisland.UPDATE_SIZE"));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        root.addView(sbW);

        // 2. المسافة من الأعلى (Y Position)
        final TextView tvY = new TextView(this);
        int curY = prefs.getInt("notch_y", 8);
        tvY.setText("المسافة من أعلى الشاشة: " + curY + " dp");
        tvY.setTextColor(Color.LTGRAY);
        root.addView(tvY);

        SeekBar sbY = createSeekBar(0, 40, curY, new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvY.setText("المسافة من أعلى الشاشة: " + progress + " dp");
                prefs.edit().putInt("notch_y", progress).apply();
                sendBroadcast(new Intent("com.pixel8.dynamicisland.UPDATE_SIZE"));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        root.addView(sbY);

        // Section: Live Activities Grid
        root.addView(createSectionTitle("اختبار الأنشطة التفاعلية (Live Activities)"));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        addGridButton(grid, "🎵 الموسيقى والصوت", "Starboy - The Weeknd", "music", "🎵");
        addGridButton(grid, "⏱️ مؤقت 05:00 دقيقة", "متبقي 04:59 ثانية", "timer", "⏱️");
        addGridButton(grid, "💬 إشعار واتساب", "رسالة جديدة: مرحباً بك!", "whatsapp", "🟢");
        addGridButton(grid, "⚡ الشحن السريع 30W", "البطارية 88%", "charging", "⚡");
        addGridButton(grid, "📞 مكالمة هاتفية", "مكالمة واردة...", "call", "📞");
        addGridButton(grid, "🎧 سماعات Buds", "متصلة 100%", "buds", "🎧");

        root.addView(grid);

        setContentView(scrollView);
    }

    private SeekBar createSeekBar(int min, int max, int current, SeekBar.OnSeekBarChangeListener listener) {
        SeekBar sb = new SeekBar(this);
        sb.setMax(max - min);
        sb.setProgress(current - min);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dpToPx(4), 0, dpToPx(14));
        sb.setLayoutParams(p);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onProgressChanged(seekBar, progress + min, fromUser);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        return sb;
    }

    private void addGridButton(GridLayout grid, final String title, final String text, final String type, final String icon) {
        Button btn = new Button(this);
        btn.setText(title);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackground(createCurvedBg(dpToPx(12), Color.parseColor("#171923"), Color.parseColor("#2D3748")));

        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dpToPx(60);
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        p.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        btn.setLayoutParams(p);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, DynamicIslandService.class));
                Intent intent = new Intent("com.pixel8.dynamicisland.NOTIF");
                intent.putExtra("title", title);
                intent.putExtra("text", text);
                intent.putExtra("type", type);
                intent.putExtra("icon", icon);
                sendBroadcast(intent);
            }
        });

        grid.addView(btn);
    }

    private TextView createSectionTitle(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#94A3B8"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dpToPx(14), 0, dpToPx(8));
        tv.setLayoutParams(p);
        return tv;
    }

    private Button createButton(String text, String colorHex, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackground(createCurvedBg(dpToPx(12), Color.parseColor(colorHex), Color.TRANSPARENT));
        btn.setOnClickListener(listener);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dpToPx(50));
        p.setMargins(0, 0, 0, dpToPx(12));
        btn.setLayoutParams(p);
        return btn;
    }

    private GradientDrawable createCurvedBg(int radiusPx, int bgColor, int strokeColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radiusPx);
        shape.setColor(bgColor);
        if (strokeColor != Color.TRANSPARENT) shape.setStroke(dpToPx(1), strokeColor);
        return shape;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
