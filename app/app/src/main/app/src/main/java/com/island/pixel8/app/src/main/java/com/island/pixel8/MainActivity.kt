package com.pixel8.dynamicisland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
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
        layout.setPadding(50, 50, 50, 50);

        TextView title = new TextView(this);
        title.setText("Pixel 8 Dynamic Island 🏝️");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(title);

        TextView subTitle = new TextView(this);
        subTitle.setText("تحكم كامل: إشعارات • بطارية • موسيقى");
        subTitle.setTextColor(Color.parseColor("#888888"));
        subTitle.setTextSize(13);
        subTitle.setPadding(0, 5, 0, 30);
        layout.addView(subTitle);

        Button btnAccess = createStyledButton("1. تفعيل الجزيرة (Accessibility)", "#1E88E5");
        btnAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(btnAccess);

        Button btnNotif = createStyledButton("2. تفعيل قراءة الإشعارات والموسيقى", "#43A047");
        btnNotif.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        layout.addView(btnNotif);

        // تعديل موضع Y
        int currentY = prefs.getInt("island_y", 12);
        TextView txtY = new TextView(this);
        txtY.setText("الموضع من الأعلى (Y): " + currentY + " px");
        txtY.setTextColor(Color.LTGRAY);
        txtY.setPadding(0, 20, 0, 10);
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
        txtW.setPadding(0, 20, 0, 10);
        layout.addView(txtW);

        SeekBar seekW = new SeekBar(this);
        seekW.setMax(450);
        seekW.setProgress(currentW);
        seekW.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtWidth.setText("عرض الجزيرة في وضع السكون: " + progress + " px");
                prefs.edit().putInt("island_width", progress).apply();
                sendLiveUpdate(prefs.getInt("island_y", 12), progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(seekW);

        // زر تجربة البطارية
        Button btnTestBat = createStyledButton("⚡ تجربة أنيميشن الشحن (Battery)", "#FF9800");
        btnTestBat.setOnClickListener(v -> {
            sendBroadcast(new Intent(Intent.ACTION_POWER_CONNECTED));
        });
        layout.addView(btnTestBat);

        // زر تجربة الموسيقى
        Button btnTestMusic = createStyledButton("🎵 تجربة مشغل الموسيقى (Music)", "#9C27B0");
        btnTestMusic.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationService.ACTION_MEDIA_STATE);
            intent.putExtra("isPlaying", true);
            intent.putExtra("track", "Starboy");
            intent.putExtra("artist", "The Weeknd");
            sendBroadcast(intent);
        });
        layout.addView(btnTestMusic);

        scrollView.addView(layout);
        setContentView(scrollView);
    }

    private Button createStyledButton(String text, String colorHex) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(colorHex));
        d.setCornerRadius(25);
        b.setBackground(d);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 130);
        p.setMargins(0, 10, 0, 10);
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
