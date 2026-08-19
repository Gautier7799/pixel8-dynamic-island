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

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.parseColor("#121212"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 60);

        TextView title = new TextView(this);
        title.setText("Pixel 8 Dynamic Island");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(title);

        TextView sub = new TextView(this);
        sub.setText("لوحة التحكم والإعدادات");
        sub.setTextColor(Color.GRAY);
        sub.setTextSize(13);
        sub.setPadding(0, 5, 0, 30);
        layout.addView(sub);

        // زر تفعيل الجزيرة
        Button btn1 = createBtn("1. تفعيل إذن الجزيرة (ACCESSIBILITY)", "#1976D2");
        btn1.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception ignored) {}
        });
        layout.addView(btn1);

        // زر إذن الإشعارات
        Button btn2 = createBtn("2. تفعيل إذن الإشعارات (NOTIFICATIONS)", "#388E3C");
        btn2.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            } catch (Exception ignored) {}
        });
        layout.addView(btn2);

        // زر التجربة
        Button btnTest = createBtn("✨ تجربة ظهور الجزيرة الآن", "#424242");
        btnTest.setOnClickListener(v -> {
            if (DynamicIslandService.instance != null) {
                DynamicIslandService.instance.showIsland("✨ تجربة تفاعلية", "الجزيرة تعمل بنجاح!", "🚀", Color.parseColor("#00E676"));
            } else {
                Toast.makeText(this, "يرجى تفعيل الزر الأزرق رقم 1 أولاً", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(btnTest);

        scroll.addView(layout);
        setContentView(scroll);
    }

    private Button createBtn(String text, String colorHex) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(colorHex));
        bg.setCornerRadius(20);
        b.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 120);
        p.setMargins(0, 10, 0, 15);
        b.setLayoutParams(p);
        return b;
    }
}
