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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicIslandService extends AccessibilityService {

    private WindowManager windowManager;
    private LinearLayout islandContainer;
    private TextView islandText;
    private WindowManager.LayoutParams params;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isExpanded = false;

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            showNotificationIsland(title, text);
        }
    };

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        registerNotificationReceiver();
        createIsland();
    }

    private void registerNotificationReceiver() {
        IntentFilter filter = new IntentFilter(NotificationService.ACTION_NEW_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(notificationReceiver, filter);
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
            int customY = prefs.getInt("island_y", 12);
            int baseWidth = prefs.getInt("island_width", 260);
            int baseHeight = prefs.getInt("island_height", 90);

            islandContainer = new LinearLayout(this);
            islandContainer.setOrientation(LinearLayout.HORIZONTAL);
            islandContainer.setGravity(Gravity.CENTER);
            islandContainer.setPadding(24, 0, 24, 0);

            // تصميم الكبسولة السوداء المقوسة فائقة النقاء
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(Color.BLACK);
            shape.setCornerRadius(100f);
            shape.setStroke(1, Color.parseColor("#222222"));
            islandContainer.setBackground(shape);

            islandText = new TextView(this);
            islandText.setTextColor(Color.WHITE);
            islandText.setTextSize(12);
            islandText.setSingleLine(true);
            islandText.setVisibility(View.GONE); // مخفي في وضع الكاميرا الصامت
            islandContainer.addView(islandText);

            islandContainer.setOnClickListener(v -> {
                if (!isExpanded) {
                    showNotificationIsland("Pixel 8 Island", "نظام الجزيرة نشط وجاهز للإشعارات!");
                }
            });

            params = new WindowManager.LayoutParams(
                    baseWidth,
                    baseHeight,
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

    private void showNotificationIsland(String title, String text) {
        if (islandContainer == null || windowManager == null) return;

        isExpanded = true;
        islandText.setVisibility(View.VISIBLE);
        islandText.setText(title + (text.isEmpty() ? "" : ": " + text));

        animateIslandSize(params.width, 680, params.height, 120);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("island_prefs", MODE_PRIVATE);
            int baseWidth = prefs.getInt("island_width", 260);
            int baseHeight = prefs.getInt("island_height", 90);
            islandText.setVisibility(View.GONE);
            animateIslandSize(params.width, baseWidth, params.height, baseHeight);
            isExpanded = false;
        }, 4000);
    }

    private void animateIslandSize(int fromW, int toW, int fromH, int toH) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            params.width = (int) (fromW + (toW - fromW) * fraction);
            params.height = (int) (fromH + (toH - fromH) * fraction);
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
            unregisterReceiver(notificationReceiver);
        } catch (Exception ignored) {}
        if (islandContainer != null && windowManager != null) {
            windowManager.removeView(islandContainer);
            islandContainer = null;
        }
    }
}
