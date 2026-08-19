package com.pixel8.dynamicisland;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAccess = findViewById(R.id.btn_accessibility);
        if (btnAccess != null) {
            btnAccess.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this, "تعذر فتح الإعدادات", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnNotif = findViewById(R.id.btn_notification);
        if (btnNotif != null) {
            btnNotif.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this, "تعذر فتح إعدادات الإشعارات", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnTest = findViewById(R.id.btn_test);
        if (btnTest != null) {
            btnTest.setOnClickListener(v -> {
                if (DynamicIslandService.instance != null) {
                    DynamicIslandService.instance.showIsland("✨ تجربة تفاعلية", "الجزيرة تعمل بنجاح!", "🚀", Color.parseColor("#00E676"));
                } else {
                    Toast.makeText(this, "يرجى تفعيل الخيار رقم 1 أولاً", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
