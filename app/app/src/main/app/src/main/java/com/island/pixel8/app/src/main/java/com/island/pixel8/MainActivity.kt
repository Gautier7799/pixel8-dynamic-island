package com.pixel8.dynamicisland;

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

    public static final String ACTION_UPDATE_CONFIG = "com.pixel8.dynamicisland.UPDATE_CONFIG";

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

        int currentY = prefs.getInt("island_y", 15);
        int currentWidth = prefs.getInt("island_width", 280);

        seekY.setProgress(currentY);
        txtY.setText("الموضع من الأعلى (Y): " + currentY + " px");

        seekWidth.setProgress(currentWidth);
        txtWidth.setText("عرض الجزيرة في وضع السكون: " + currentWidth + " px");

        // تغيير موقع Y مباشرة
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

        // تغيير العرض مباشرة
        seekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtWidth.setText("عرض الجزيرة في وضع السكون: " + progress + " px");
                prefs.edit().putInt("island_width", progress).apply();
                sendLiveUpdate(prefs.getInt("island_y", 15), progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnAccessibility.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });

        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        });

        btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationService.ACTION_NEW_NOTIFICATION);
            intent.putExtra("title", "واتساب");
            intent.putExtra("text", "أهلاً بك! إشعارات الجزيرة تعمل بامتياز 🚀");
            sendBroadcast(intent);
            Toast.makeText(this, "تم إرسال إشعار تجريبي!", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendLiveUpdate(int y, int width) {
        Intent intent = new Intent(ACTION_UPDATE_CONFIG);
        intent.putExtra("island_y", y);
        intent.putExtra("island_width", width);
        sendBroadcast(intent);
    }
}
