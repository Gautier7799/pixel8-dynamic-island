package com.pixel8.dynamicisland;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStartIsland);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOverlayPermissionAndStart();
            }
        });
    }

    private void checkOverlayPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "يرجى منح إذن الظهور فوق التطبيقات", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                startDynamicIslandService();
            }
        } else {
            startDynamicIslandService();
        }
    }

    private void startDynamicIslandService() {
        Intent serviceIntent = new Intent(this, DynamicIslandService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "تم تشغيل الجزيرة بنجاح!", Toast.LENGTH_SHORT).show();
    }
}
