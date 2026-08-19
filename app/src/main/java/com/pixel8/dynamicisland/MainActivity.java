package com.pixel8.dynamicisland;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ScrollView scrollView = new ScrollView(this);
            scrollView.setBackgroundColor(Color.parseColor("#121212"));

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 80, 50, 80);

            TextView title = new TextView(this);
            title.setText("Pixel 8 Dynamic Island");
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(title);

            TextView subtitle = new TextView(this);
            subtitle.setText("لوحة التحكم والإعدادات التفاعلية");
            subtitle.setTextColor(Color.parseColor("#999999"));
            subtitle.setTextSize(13);
            subtitle.setPadding(0, 8, 0, 40);
            layout.addView(subtitle);

            // 1. زر تفعيل الجزيرة
            Button btnAccess = makeButton("1. تفعيل الجزيرة (ACCESSIBILITY)", "#1976D2");
            btnAccess.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this, "تعذر فتح الإعدادات", Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(btnAccess);

            // 2. زر إذن الإشعارات والموسيقى
            Button btnNotif = makeButton("2. إذن الإشعارات والموسيقى (NOTIFICATIONS)", "#2E7D32");
            btnNotif.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this, "تعذر فتح الإشعارات", Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(btnNotif);

            // 3. زر التجربة الفورية
            Button btnTest = makeButton("✨ تجربة تفاعلية للجزيرة", "#333333");
            btnTest.setTextColor(Color.parseColor("#00E676"));
            btnTest.setOnClickListener(v -> {
                if (DynamicIslandService.instance != null) {
                    DynamicIslandService.instance.showIsland("✨ تجربة ناجحة", "الجزيرة تعمل بكفاءة على Pixel 8!", "🚀", Color.parseColor("#00E676"));
                } else {
                    Toast.makeText(this, "يرجى تفعيل الخيار رقم 1 أولاً", Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(btnTest);

            scrollView.addView(layout);
            setContentView(scrollView);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Button makeButton(String text, String colorHex) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(colorHex));
        bg.setCornerRadius(24);
        b.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 130);
        p.setMargins(0, 10, 0, 20);
        b.setLayoutParams(p);
        return b;
    }
}
