package com.pixel8.dynamicisland;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // إنشاء خلفية داكنة رئيسية فخمة
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#0A0A0C"));
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        int padding = dpToPx(24);
        rootLayout.setPadding(padding, dpToPx(48), padding, padding);

        // عنوان التطبيق
        TextView title = new TextView(this);
        title.setText("Pixel 8 Dynamic Island");
        title.setTextColor(Color.WHITE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        rootLayout.addView(title);

        // وصف الإصدار
        TextView subtitle = new TextView(this);
        subtitle.setText("الجزيرة التفاعلية المتطورة v4.0");
        subtitle.setTextColor(Color.parseColor("#38BDF8"));
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.setMargins(0, dpToPx(8), 0, dpToPx(36));
        subtitle.setLayoutParams(subParams);
        rootLayout.addView(subtitle);

        // زر 1: تفعيل الظهور فوق التطبيقات
        Button btnOverlay = createStyledButton("1. تفعيل الجزيرة فوق التطبيقات ⚡", "#0284C7");
        btnOverlay.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } else {
                    startService(new Intent(this, DynamicIslandService.class));
                    Toast.makeText(this, "الجزيرة مفعلة بنجاح حول الكاميرا! 🏝️", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rootLayout.addView(btnOverlay);

        // زر 2: إذن الإشعارات والموسيقى
        Button btnNotif = createStyledButton("2. إذن الإشعارات والموسيقى 🎵", "#16A34A");
        btnNotif.setOnClickListener(v -> {
            try {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "يرجى تفعيل صلاحية الإشعارات من الإعدادات", Toast.LENGTH_SHORT).show();
            }
        });
        rootLayout.addView(btnNotif);

        // زر 3: تجربة الأنشطة الحية فوراً
        Button btnTest = createStyledButton("3. اختبار الأنشطة الحية فوراً ✨", "#D97706");
        btnTest.setOnClickListener(v -> {
            startService(new Intent(this, DynamicIslandService.class));
            Intent testIntent = new Intent("com.pixel8.dynamicisland.NOTIF");
            testIntent.putExtra("title", "🎵 شغال على Pixel 8 بنجاح");
            testIntent.putExtra("text", "تجربة الجزيرة التفاعلية الإصدار 4.0");
            testIntent.putExtra("type", "music");
            sendBroadcast(testIntent);
            Toast.makeText(this, "تم إرسال نشاط تجريبي للجزيرة! 🚀", Toast.LENGTH_SHORT).show();
        });
        rootLayout.addView(btnTest);

        setContentView(rootLayout);
    }

    private Button createStyledButton(String text, String colorHex) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        btn.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dpToPx(16));
        shape.setColor(Color.parseColor(colorHex));
        btn.setBackground(shape);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(58));
        params.setMargins(0, 0, 0, dpToPx(16));
        btn.setLayoutParams(params);
        return btn;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
