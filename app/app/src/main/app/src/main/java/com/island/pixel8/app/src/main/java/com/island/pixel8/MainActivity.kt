package com.pixel8.dynamicisland;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAccessibility = findViewById(R.id.btnAccessibility);
        Button btnNotification = findViewById(R.id.btnNotification);
        Button btnTest = findViewById(R.id.btnTest);
        SeekBar seekY = findViewById(R.id.seekY);
        SeekBar seekWidth = findViewById(R.id.seekWidth);
        TextView txtY = findViewById(R.id.txtY);
        TextView txtWidth = findViewById(R.id.txtWidth);

        SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);

        // ضبط إزاحة الكاميرا وموقع الجزيرة
        seekY.setProgress(prefs.getInt("island_y", 12));
        txtY.setText("الموضع من الأعلى (Y): " + seekY.getProgress() + "px");

        seekY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtY.setText("الموضع من الأعلى (Y): " + progress + "px");
                prefs.edit().putInt("island_y", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // ضبط عرض الجزيرة
        seekWidth.setProgress(prefs.getInt("island_width", 260));
        txtWidth.setText("عرض الجزيرة في وضع السكون: " + seekWidth.getProgress() + "px");

        seekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtWidth.setText("عرض الجزيرة في وضع السكون: " + progress + "px");
                prefs.edit().putInt("island_width", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // أزرار الصلاحيات
        btnAccessibility.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "فعّل Pixel 8 Island من القائمة", Toast.LENGTH_SHORT).show();
        });

        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            Toast.makeText(this, "امنح إذن الإشعارات للتطبيق", Toast.LENGTH_SHORT).show();
        });

        btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationService.ACTION_NEW_NOTIFICATION);
            intent.putExtra("title", "💬 رسالة جديدة");
            intent.putExtra("text", "مرحباً! إشعارات الجزيرة تعمل بامتياز على Pixel 8 🔥");
            sendBroadcast(intent);
        });
    }
}
