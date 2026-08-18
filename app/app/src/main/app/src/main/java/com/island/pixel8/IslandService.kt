package com.pixel8.dynamicisland;

import android.accessibilityservice.AccessibilityService;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DynamicIslandService extends AccessibilityService {

    private WindowManager windowManager;
    private LinearLayout islandContainer;
    private TextView leftIcon, rightText, mainText;
    private WindowManager.LayoutParams params;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isExpanded = false;
    private boolean isPlayingMusic = false;

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            if (MainActivity.ACTION_UPDATE_CONFIG.equals(action)) {
                int newY = intent.getIntExtra("island_y", 12);
                int newWidth = intent.getIntExtra("island_width", 280);
                updateLiveDimensions(newY, newWidth);
            } else if (NotificationService.ACTION_NEW_NOTIFICATION.equals(action)) {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                showNotificationIsland("💬 " + title, text);
            } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                showBatteryIsland(true);
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                showBatteryIsland(false);
            } else if (NotificationService.ACTION_MEDIA_STATE.equals(action)) {
                boolean playing = intent.getBooleanExtra("isPlaying", false);
                String track = intent.getStringExtra("track");
                String artist = intent.getStringExtra("artist");
                handleMusicState(playing, track, artist);
            }
        }
    };

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        registerEvents();
        createIsland();
    }

    private void registerEvents() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_UPDATE_CONFIG);
        filter.addAction(NotificationService.ACTION_NEW_NOTIFICATION);
        filter.addAction(NotificationService.ACTION_MEDIA_STATE);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(eventReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(eventReceiver, filter);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    private void createIsland() {
        if (islandContainer != null) return;

        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);
            int baseWidth = prefs.getInt("island_width", 280);
            int customY = prefs.getInt("island_y", 12);

            islandContainer = new LinearLayout(this);
            islandContainer.setOrientation(LinearLayout.HORIZONTAL);
            islandContainer.setGravity(Gravity.CENTER);
            islandContainer.setPadding(20, 0, 20, 0);

            GradientDrawable shape = new GradientDrawable();
            shape.setColor(Color.BLACK);
            shape.setCornerRadius(100f);
            shape.setStroke(1, Color.parseColor("#333333"));
            islandContainer.setBackground(shape);

            leftIcon = new TextView(this);
            leftIcon.setTextSize(14);
            leftIcon.setVisibility(View.GONE);
            islandContainer.addView(leftIcon);

            mainText = new TextView(this);
            mainText.setTextColor(Color.WHITE);
            mainText.setTextSize(12);
            mainText.setSingleLine(true);
            mainText.setPadding(12, 0, 12, 0);
            mainText.setVisibility(View.GONE);
            islandContainer.addView(mainText);

            rightText = new TextView(this);
            rightText.setTextColor(Color.parseColor("#00E676"));
            rightText.setTextSize(12);
            rightText.setVisibility(View.GONE);
            islandContainer.addView(rightText);

            params = new WindowManager.LayoutParams(
                    baseWidth,
                    90,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.y = customY;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }

            windowManager.addView(islandContainer, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // عرض أنيميشن البطارية عند توصيل الشاحن
    private void showBatteryIsland(boolean connected) {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        isExpanded = true;
        leftIcon.setVisibility(View.VISIBLE);
        leftIcon.setText(connected ? "⚡" : "🔋");

        mainText.setVisibility(View.VISIBLE);
        mainText.setText(connected ? "جاري الشحن السريع" : "تم فصل الشاحن");

        rightText.setVisibility(View.VISIBLE);
        rightText.setText(percentage + "%");
        rightText.setTextColor(connected ? Color.parseColor("#00E676") : Color.LTGRAY);

        animateIsland(params.width, 620, params.height, 120);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> collapseIsland(), 3500);
    }

    // عرض وتفاعل الموسيقى
    private void handleMusicState(boolean playing, String track, String artist) {
        isPlayingMusic = playing;
        if (playing && track != null) {
            isExpanded = true;
            leftIcon.setVisibility(View.VISIBLE);
            leftIcon.setText("🎵");

            mainText.setVisibility(View.VISIBLE);
            mainText.setText(track + (artist != null ? " • " + artist : ""));

            rightText.setVisibility(View.VISIBLE);
            rightText.setText("ılılı");
            rightText.setTextColor(Color.parseColor("#FF4081"));

            animateIsland(params.width, 680, params.height, 115);

            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> {
                // وضع الكبسولة المدمجة أثناء تشغيل الأغنية
                mainText.setVisibility(View.GONE);
                animateIsland(params.width, 360, params.height, 90);
            }, 4000);
        } else {
            collapseIsland();
        }
    }

    private void showNotificationIsland(String title, String text) {
        isExpanded = true;
        leftIcon.setVisibility(View.GONE);
        rightText.setVisibility(View.GONE);
        mainText.setVisibility(View.VISIBLE);
        mainText.setText(title + (text.isEmpty() ? "" : ": " + text));

        animateIsland(params.width, 700, params.height, 125);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> collapseIsland(), 4000);
    }

    private void collapseIsland() {
        SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);
        int baseWidth = prefs.getInt("island_width", 280);
        leftIcon.setVisibility(View.GONE);
        mainText.setVisibility(View.GONE);
        rightText.setVisibility(View.GONE);
        animateIsland(params.width, baseWidth, params.height, 90);
        isExpanded = false;
    }

    private void updateLiveDimensions(int y, int width) {
        if (islandContainer != null && windowManager != null && !isExpanded) {
            params.y = y;
            params.width = width;
            try {
                windowManager.updateViewLayout(islandContainer, params);
            } catch (Exception ignored) {}
        }
    }

    private void animateIsland(int fromW, int toW, int fromH, int toH) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(280);
        anim.addUpdateListener(a -> {
            float f = a.getAnimatedFraction();
            params.width = (int) (fromW + (toW - fromW) * f);
            params.height = (int) (fromH + (toH - fromH) * f);
            try {
                windowManager.updateViewLayout(islandContainer, params);
            } catch (Exception ignored) {}
        });
        anim.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(eventReceiver);
        } catch (Exception ignored) {}
        if (islandContainer != null && windowManager != null) {
            windowManager.removeView(islandContainer);
            islandContainer = null;
        }
    }
}
