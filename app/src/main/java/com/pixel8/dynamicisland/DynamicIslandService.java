package com.pixel8.dynamicisland;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicIslandService extends Service {

    private WindowManager windowManager;
    private FrameLayout islandRoot;
    private LinearLayout compactLayout;
    private LinearLayout expandedLayout;
    private WindowManager.LayoutParams params;

    private boolean isExpanded = false;
    private Handler autoShrinkHandler = new Handler(Looper.getMainLooper());
    private Runnable autoShrinkRunnable;

    // مقاسات هاتف Pixel 8 الدقيقة حول الكاميرا
    private final int NOTCH_Y = 12;
    private final int COMPACT_WIDTH = 195;
    private final int COMPACT_HEIGHT = 40;
    private final int EXPANDED_WIDTH = 340;
    private final int EXPANDED_HEIGHT = 120;

    private TextView tvCompactLeft, tvCompactRight;
    private TextView tvExpTitle, tvExpText;

    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                     status == BatteryManager.BATTERY_STATUS_FULL;
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                if (isCharging) {
                    popOutIsland("⚡ الشحن السريع: " + level + "%", "🔋", "🔋 جاري الشحن السريع (30W)", "نسبة البطارية الحالية " + level + "%", 4500);
                }
            } else if ("com.pixel8.dynamicisland.NOTIF".equals(action)) {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                String icon = intent.getStringExtra("icon");
                if (title == null) title = "إشعار جديد";
                if (text == null) text = "نشاط حي نشط";
                if (icon == null) icon = "💬";
                popOutIsland(icon + " " + title, "✨", title, text, 5000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
                dpToPx(COMPACT_WIDTH),
                dpToPx(COMPACT_HEIGHT),
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = dpToPx(NOTCH_Y);

        buildIslandViews();

        try {
            windowManager.addView(islandRoot, params);
        } catch (Exception ignored) {}

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction("com.pixel8.dynamicisland.NOTIF");

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(eventReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(eventReceiver, filter);
        }
    }

    private void buildIslandViews() {
        islandRoot = new FrameLayout(this);
        islandRoot.setBackground(createCurvedBackground(dpToPx(20), Color.BLACK, Color.parseColor("#38BDF8")));
        islandRoot.setClipToOutline(true);

        compactLayout = new LinearLayout(this);
        compactLayout.setOrientation(LinearLayout.HORIZONTAL);
        compactLayout.setGravity(Gravity.CENTER_VERTICAL);
        compactLayout.setPadding(dpToPx(14), 0, dpToPx(14), 0);

        tvCompactLeft = new TextView(this);
        tvCompactLeft.setText("⚡ جاري الشحن: 41%");
        tvCompactLeft.setTextColor(Color.WHITE);
        tvCompactLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvCompactLeft.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        compactLayout.addView(tvCompactLeft, leftParams);

        tvCompactRight = new TextView(this);
        tvCompactRight.setText("🔋");
        tvCompactRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        compactLayout.addView(tvCompactRight);

        islandRoot.addView(compactLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        expandedLayout = new LinearLayout(this);
        expandedLayout.setOrientation(LinearLayout.VERTICAL);
        expandedLayout.setGravity(Gravity.CENTER_VERTICAL);
        expandedLayout.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        expandedLayout.setVisibility(View.GONE);

        tvExpTitle = new TextView(this);
        tvExpTitle.setText("الجزيرة التفاعلية");
        tvExpTitle.setTextColor(Color.parseColor("#38BDF8"));
        tvExpTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvExpTitle.setTypeface(Typeface.DEFAULT_BOLD);
        expandedLayout.addView(tvExpTitle);

        tvExpText = new TextView(this);
        tvExpText.setText("انقر لفتح لوحة التحكم والتفاعل مع النشاط الحي");
        tvExpText.setTextColor(Color.LTGRAY);
        tvExpText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        expandedLayout.addView(tvExpText);

        LinearLayout buttonsRow = new LinearLayout(this);
        buttonsRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonsRow.setGravity(Gravity.END);
        buttonsRow.setPadding(0, dpToPx(8), 0, 0);

        Button btnAction = new Button(this);
        btnAction.setText("فتح التطبيق ↗");
        btnAction.setTextColor(Color.WHITE);
        btnAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btnAction.setBackground(createCurvedBackground(dpToPx(10), Color.parseColor("#0284C7"), Color.TRANSPARENT));
        btnAction.setOnClickListener(v -> {
            try {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchIntent);
                }
            } catch (Exception ignored) {}
            shrinkIsland();
        });
        buttonsRow.addView(btnAction);

        expandedLayout.addView(buttonsRow);

        islandRoot.addView(expandedLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // النقر على الجزيرة لتمديدها أو تقليصها
        islandRoot.setOnClickListener(v -> {
            if (!isExpanded) {
                expandIsland();
            } else {
                shrinkIsland();
            }
        });
    }

    public void popOutIsland(String compactText, String compactIcon, String expTitle, String expText, int durationMs) {
        tvCompactLeft.setText(compactText);
        tvCompactRight.setText(compactIcon);
        tvExpTitle.setText(expTitle);
        tvExpText.setText(expText);

        expandIsland();

        if (autoShrinkRunnable != null) autoShrinkHandler.removeCallbacks(autoShrinkRunnable);
        autoShrinkRunnable = this::shrinkIsland;
        autoShrinkHandler.postDelayed(autoShrinkRunnable, durationMs);
    }

    private void expandIsland() {
        if (isExpanded) return;
        isExpanded = true;
        compactLayout.setVisibility(View.GONE);
        expandedLayout.setVisibility(View.VISIBLE);

        animateIslandSize(COMPACT_WIDTH, EXPANDED_WIDTH, COMPACT_HEIGHT, EXPANDED_HEIGHT, 26);
    }

    private void shrinkIsland() {
        if (!isExpanded) return;
        isExpanded = false;
        expandedLayout.setVisibility(View.GONE);
        compactLayout.setVisibility(View.VISIBLE);

        animateIslandSize(EXPANDED_WIDTH, COMPACT_WIDTH, EXPANDED_HEIGHT, COMPACT_HEIGHT, 20);
    }

    private void animateIslandSize(int fromW, int toW, int fromH, int toH, int cornerRadius) {
        ValueAnimator animW = ValueAnimator.ofInt(dpToPx(fromW), dpToPx(toW));
        animW.setDuration(320);
        animW.setInterpolator(new OvershootInterpolator(1.1f));
        animW.addUpdateListener(animation -> {
            params.width = (int) animation.getAnimatedValue();
            windowManager.updateViewLayout(islandRoot, params);
        });

        ValueAnimator animH = ValueAnimator.ofInt(dpToPx(fromH), dpToPx(toH));
        animH.setDuration(320);
        animH.setInterpolator(new OvershootInterpolator(1.1f));
        animH.addUpdateListener(animation -> {
            params.height = (int) animation.getAnimatedValue();
            islandRoot.setBackground(createCurvedBackground(dpToPx(cornerRadius), Color.BLACK, Color.parseColor("#38BDF8")));
            windowManager.updateViewLayout(islandRoot, params);
        });

        animW.start();
        animH.start();
    }

    private GradientDrawable createCurvedBackground(int radiusPx, int bgColor, int strokeColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radiusPx);
        shape.setColor(bgColor);
        if (strokeColor != Color.TRANSPARENT) shape.setStroke(dpToPx(1), strokeColor);
        return shape;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(eventReceiver);
            if (islandRoot != null) windowManager.removeView(islandRoot);
        } catch (Exception ignored) {}
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
