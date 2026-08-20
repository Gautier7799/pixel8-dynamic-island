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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        subtitle.setText("لوحة التحكم والأنشطة التفاعلية الحية 🏝️");
        subtitle.setTextColor(Color.parseColor("#38BDF8"));
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        LinearLayout.LayoutParams subP = new LinearLayout.LayoutParams(-2, -2);
        subP.setMargins(0, dpToPx(4), 0, dpToPx(24));
        subtitle.setLayoutParams(subP);
        root.addView(subtitle);

        // Permissions Section
        root.addView(createSectionTitle("الصلاحيات الأساسية"));
        root.addView(createButton("1. تفعيل الجزيرة فوق التطبيقات ⚡", "#0284C7", v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                } else {
                    startService(new Intent(this, DynamicIslandService.class));
                    Toast.makeText(this, "الجزيرة نشطة بنجاح! 🏝️", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        root.addView(createButton("2. تفعيل قراءة الإشعارات والموسيقى 🔔", "#16A34A", v -> {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }));

        // Live Activities Grid
        root.addView(createSectionTitle("الأنشطة التفاعلية الحية (Live Activities)"));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        addGridButton(grid, "🎵 الموسيقى والصوت", "Starboy - The Weeknd", "music", "🎵");
        addGridButton(grid, "⏱️ مؤقت 05:00 دقيقة", "جاري العد التنازلي...", "timer", "⏱️");
        addGridButton(grid, "💬 إشعار واتساب", "رسالة جديدة: أين أنت؟", "whatsapp", "🟢");
        addGridButton(grid, "📞 مكالمة هاتفية", "محمد يتصل بك...", "call", "📞");
        addGridButton(grid, "⚡ الشحن السريع 30W", "البطارية: 85% متبقي 12 دقيقة", "charging", "⚡");
        addGridButton(grid, "🗺️ الملاحة والخرائط", "انعطف يميناً بعد 200م", "maps", "🗺️");
        addGridButton(grid, "🎧 سماعات Pixel Buds", "متصلة: 95% علبة الشحن", "buds", "🎧");
        addGridButton(grid, "✨ ذكاء Gemini AI", "تم تلخيص المستند بنجاح", "gemini", "✨");

        root.addView(grid);

        setContentView(scrollView);
    }

    private void addGridButton(GridLayout grid, String title, String text, String type, String icon) {
        Button btn = new Button(this);
        btn.setText(title);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackground(createCurvedBg(dpToPx(14), Color.parseColor("#171923"), Color.parseColor("#2D3748")));

        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dpToPx(65);
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        p.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        btn.setLayoutParams(p);

        btn.setOnClickListener(v -> {
            startService(new Intent(this, DynamicIslandService.class));
            Intent intent = new Intent("com.pixel8.dynamicisland.NOTIF");
            intent.putExtra("title", title);
            intent.putExtra("text", text);
            intent.putExtra("type", type);
            intent.putExtra("icon", icon);
            sendBroadcast(intent);
            Toast.makeText(this, "تم إرسال " + title + " إلى الجزيرة! 🚀", Toast.LENGTH_SHORT).show();
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
        p.setMargins(0, dpToPx(16), 0, dpToPx(10));
        tv.setLayoutParams(p);
        return tv;
    }

    private Button createButton(String text, String colorHex, android.view.View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackground(createCurvedBg(dpToPx(14), Color.parseColor(colorHex), Color.TRANSPARENT));
        btn.setOnClickListener(listener);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dpToPx(52));
        p.setMargins(0, 0, 0, dpToPx(10));
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
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
