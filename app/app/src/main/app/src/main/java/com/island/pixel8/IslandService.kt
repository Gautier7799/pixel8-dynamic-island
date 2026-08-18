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

    private WindowManager windowManager;
    private LinearLayout islandContainer;
    private TextView islandText;
    private WindowManager.LayoutParams params;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isExpanded = false;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MainActivity.ACTION_UPDATE_CONFIG.equals(intent.getAction())) {
                int newY = intent.getIntExtra("island_y", 15);
                int newWidth = intent.getIntExtra("island_width", 280);
                updateLiveDimensions(newY, newWidth);
            } else if (NotificationService.ACTION_NEW_NOTIFICATION.equals(intent.getAction())) {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                showNotificationIsland(title, text);
            }
        }
    };

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        registerReceivers();
        createIsland();
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_UPDATE_CONFIG);
        filter.addAction(NotificationService.ACTION_NEW_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(updateReceiver, filter);
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
            int customY = prefs.getInt("island_y", 15);

            islandContainer = new LinearLayout(this);
            islandContainer.setOrientation(LinearLayout.HORIZONTAL);
            islandContainer.setGravity(Gravity.CENTER);
            islandContainer.setPadding(20, 0, 20, 0);

            GradientDrawable shape = new GradientDrawable();
            shape.setColor(Color.BLACK);
            shape.setCornerRadius(100f);
            islandContainer.setBackground(shape);

            islandText = new TextView(this);
            islandText.setTextColor(Color.WHITE);
            islandText.setTextSize(12);
            islandText.setSingleLine(true);
            islandText.setVisibility(View.GONE);
            islandContainer.addView(islandText);

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

    private void updateLiveDimensions(int y, int width) {
        if (islandContainer != null && windowManager != null && !isExpanded) {
            params.y = y;
            params.width = width;
            try {
                windowManager.updateViewLayout(islandContainer, params);
            } catch (Exception ignored) {}
        }
    }

    private void showNotificationIsland(String title, String text) {
        if (islandContainer == null || windowManager == null) return;

        isExpanded = true;
        islandText.setVisibility(View.VISIBLE);
        islandText.setText((title != null ? title : "") + (text != null ? " : " + text : ""));

        animateIsland(params.width, 680, params.height, 120);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);
            int baseWidth = prefs.getInt("island_width", 280);
            islandText.setVisibility(View.GONE);
            animateIsland(params.width, baseWidth, params.height, 90);
            isExpanded = false;
        }, 3500);
    }

    private void animateIsland(int fromW, int toW, int fromH, int toH) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(250);
        anim.addUpdateListener(animation -> {
            float f = animation.getAnimatedFraction();
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
            unregisterReceiver(updateReceiver);
        } catch (Exception ignored) {}
        if (islandContainer != null && windowManager != null) {
            windowManager.removeView(islandContainer);
            islandContainer = null;
        }
    }
}
