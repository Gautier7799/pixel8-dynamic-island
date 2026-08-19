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
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicIslandService extends AccessibilityService {

    public static DynamicIslandService instance = null;

    private WindowManager windowManager;
    private LinearLayout islandContainer;
    private TextView leftIcon;
    private TextView mainText;
    private TextView rightText;
    private WindowManager.LayoutParams params;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isExpanded = false;
    private boolean lastChargingState = false;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                 status == BatteryManager.BATTERY_STATUS_FULL;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            if (isCharging != lastChargingState) {
                lastChargingState = isCharging;
                if (isCharging) {
                    showIsland("⚡ جاري الشحن السريع", level + "%", "🔋", Color.parseColor("#00E676"));
                } else {
                    showIsland("تم فصل الشاحن", "", "🔋", Color.LTGRAY);
                }
            }
        }
    };

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(batteryReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(batteryReceiver, filter);
        }

        createIsland();
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
            islandContainer.setPadding(25, 0, 25, 0);

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.BLACK);
            bg.setCornerRadius(100f);
            bg.setStroke(1, Color.parseColor("#222222"));
            islandContainer.setBackground(bg);

            leftIcon = new TextView(this);
            leftIcon.setTextSize(14f);
            leftIcon.setVisibility(View.GONE);
            islandContainer.addView(leftIcon);

            mainText = new TextView(this);
            mainText.setTextColor(Color.WHITE);
            mainText.setTextSize(12f);
            mainText.setSingleLine(true);
            mainText.setPadding(12, 0, 12, 0);
            mainText.setVisibility(View.GONE);
            islandContainer.addView(mainText);

            rightText = new TextView(this);
            rightText.setTextSize(13f);
            rightText.setVisibility(View.GONE);
            islandContainer.addView(rightText);

            int flag = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                       WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                       WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

            params = new WindowManager.LayoutParams(
                    baseWidth,
                    90,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    flag,
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

    public void showIsland(String title, String subtitle, String symbol, int color) {
        handler.post(() -> {
            isExpanded = true;
            leftIcon.setVisibility(symbol.isEmpty() ? View.GONE : View.VISIBLE);
            leftIcon.setText(symbol);

            mainText.setVisibility(View.VISIBLE);
            mainText.setText(subtitle.isEmpty() ? title : title + ": " + subtitle);

            rightText.setVisibility(symbol.isEmpty() ? View.GONE : View.VISIBLE);
            rightText.setText(symbol);
            rightText.setTextColor(color);

            int w = params != null ? params.width : 280;
            int h = params != null ? params.height : 90;
            animateIsland(w, 680, h, 120);

            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(this::collapseIsland, 3800);
        });
    }

    private void collapseIsland() {
        SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);
        int baseWidth = prefs.getInt("island_width", 280);
        leftIcon.setVisibility(View.GONE);
        mainText.setVisibility(View.GONE);
        rightText.setVisibility(View.GONE);
        int w = params != null ? params.width : 680;
        int h = params != null ? params.height : 120;
        animateIsland(w, baseWidth, h, 90);
        isExpanded = false;
    }

    public void updateDimensions(int y, int width) {
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
        anim.setDuration(240);
        anim.addUpdateListener(animation -> {
            float f = animation.getAnimatedFraction();
            if (params != null) {
                params.width = (int) (fromW + (toW - fromW) * f);
                params.height = (int) (fromH + (toH - fromH) * f);
                try {
                    windowManager.updateViewLayout(islandContainer, params);
                } catch (Exception ignored) {}
            }
        });
        anim.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        try { unregisterReceiver(batteryReceiver); } catch (Exception ignored) {}
        if (islandContainer != null && windowManager != null) {
            windowManager.removeView(islandContainer);
            islandContainer = null;
        }
    }
}
