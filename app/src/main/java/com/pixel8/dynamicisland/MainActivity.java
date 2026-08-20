package com.pixel8.dynamicisland;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import com.pixel8.dynamicisland.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOverlay = findViewById(R.id.btnOverlayPermission);
        Button btnNotif = findViewById(R.id.btnNotificationPermission);
        Button btnTest = findViewById(R.id.btnTestIsland);

        // 1. Overlay Permission
        if (btnOverlay != null) {
            btnOverlay.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    } else {
                        startService(new Intent(this, DynamicIslandService.class));
                        Toast.makeText(this, "الجزيرة مفعلة بنجاح! 🏝️", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // 2. Notification Listener Permission
        if (btnNotif != null) {
            btnNotif.setOnClickListener(v -> {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            });
        }

        // 3. Test Island Live Broadcast
        if (btnTest != null) {
            btnTest.setOnClickListener(v -> {
                startService(new Intent(this, DynamicIslandService.class));
                Intent testIntent = new Intent("com.pixel8.dynamicisland.NOTIF");
                testIntent.putExtra("title", "🎵 الآن يعمل على Pixel 8");
                testIntent.putExtra("text", "الجزيرة التفاعلية الإصدار 4.0");
                testIntent.putExtra("type", "music");
                sendBroadcast(testIntent);
                Toast.makeText(this, "تم إرسال نشاط تجريبي للجزيرة! 🚀", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
