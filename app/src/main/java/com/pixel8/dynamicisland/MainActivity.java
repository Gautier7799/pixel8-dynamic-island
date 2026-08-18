package com.pixel8.dynamicisland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_UPDATE_CONFIG = "com.pixel8.dynamicisland.UPDATE_CONFIG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#121212"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 60, 50, 60);

        // عنوان التطبيق
        TextView title = new TextView(this);
        title.setText("Pixel 8 Dynamic Island 🏝️");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(title);

        TextView subTitle = new TextView(this);
        subTitle.setText("لوحة التحكم وتخصيص الجزيرة والإشعارات");
        subTitle.setTextColor(Color.parseColor("#888888"));
        subTitle.setTextSize(13);
        subTitle.setPadding(0, 8, 0, 40);
        layout.addView(subTitle);

        // 1. زر تفعيل الجزيرة
        Button btnAccess = createStyledButton("1. تفعيل الجزيرة (Accessibility)", "#1E88E5");
        btnAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(btnAccess);

        // 2. زر تفعيل الإشعارات
        Button btnNotif = createStyledButton("2. تفعيل قراءة الإشعارات", "#43A047");
        btnNotif.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        layout.addView(btnNotif);

        // تعديل موضع Y (الارتفاع)
        int currentY = prefs.getInt("island_y", 12);
        TextView txtY = new TextView(this);
        txtY.setText("الموضع من الأعلى (Y): " + currentY + " px");
        txtY.setTextColor(Color.LTGRAY);
        txtY.setPadding(0, 30, 0, 10);
        layout.addView(txtY);

        SeekBar seekY = new SeekBar(this);
        seekY.setMax(80);
        seekY.setProgress(currentY);
        seekY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtY.setText("الموضع من الأعلى (Y): " + progress + " px");
                prefs.edit().putInt("island_y", progress).apply();
                sendLiveUpdate(progress, prefs.getInt("island_width", 280));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(seekY);

        // تعديل العرض
        int currentW = prefs.getInt("island_width", 280);
        TextView txtW = new TextView(this);
        txtW.setText("عرض الجزيرة في وضع السكون: " + currentW + " px");
        txtW.setTextColor(Color.LTGRAY);
        txtW.setPadding(0, 30, 0, 10);
        layout.addView(txtW);

        SeekBar seekW = new SeekBar(this);
        seekW.setMax(450);
        seekW.setProgress(currentW);
        seekW.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtW.setText("عرض الجزيرة في وضع السكون: " + progress + " px");
                prefs.edit().putInt("island_width", progress).apply();
                sendLiveUpdate(prefs.getInt("island_y", 12), progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(seekW);

        // 3. زر اختبار الإشعار
        Button btnTest = createStyledButton("✨ تجربة إشعار تفاعلي", "#333333");
        btnTest.setTextColor(Color.parseColor("#00E676"));
        btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationService.ACTION_NEW_NOTIFICATION);
            intent.putExtra("title", "واتساب");
            intent.putExtra("text", "مرحباً! إشعارات الجزيرة تعمل بنجاح 🚀");
            sendBroadcast(intent);
            Toast.makeText(this, "تم إرسال إشعار للجزيرة!", Toast.LENGTH_SHORT).show();
        });
        layout.addView(btnTest);

        scrollView.addView(layout);
        setContentView(scrollView);
    }

    private Button createStyledButton(String text, String colorHex) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(colorHex));
        d.setCornerRadius(30);
        b.setBackground(d);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 140);
        p.setMargins(0, 15, 0, 15);
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
