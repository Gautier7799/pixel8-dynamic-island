package com.pixel8.dynamicisland;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;

public class DynamicIslandService extends Service {

    private WindowManager windowManager;
    private View islandView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());
        showIsland();
    }

    private void showIsland() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // إنشاء شكل الجزيرة الديناميكية الأسود المقوس المخصص لثقب كاميرا Pixel 8
        islandView = new TextView(this);
        ((TextView) islandView).setText("● Pixel 8 Island");
        ((TextView) islandView).setTextColor(Color.WHITE);
        ((TextView) islandView).setTextSize(12);
        ((TextView) islandView).setGravity(Gravity.CENTER);

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.BLACK);
        shape.setCornerRadius(60); // زوايا دائرية كاملة
        islandView.setBackground(shape);

        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        // إحداثيات ومقاسات الجزيرة حول الكاميرا
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                320, // العرض
                100, // الارتفاع
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = 20; // المسافة من أعلى الشاشة لتناسب ثقب الكاميرا

        windowManager.addView(islandView, params);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "island_channel",
                    "Dynamic Island Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, "island_channel")
                .setContentTitle("Dynamic Island نشطة")
                .setContentText("الجزيرة التفاعلية تعمل الآن حول كاميرا Pixel 8")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (islandView != null && windowManager != null) {
            windowManager.removeView(islandView);
        }
    }
}
