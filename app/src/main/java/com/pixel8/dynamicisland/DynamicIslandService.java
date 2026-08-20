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
import android.provider.Settings;
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

    public static DynamicIslandService instance = null;

    private WindowManager windowManager;
    private FrameLayout islandRoot;
    private LinearLayout compactLayout;
    private LinearLayout expandedLayout;
    private WindowManager.LayoutParams params;
    private boolean isAttached = false;

    private boolean isExpanded = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable shrinkRunnable;

    // أبعاد وموضع الجزيرة الدقيق حول ثقب كاميرا Pixel 8
    private final int NOTCH_Y = 10;
    private final int COMPACT_WIDTH = 190;
    private final int COMPACT_HEIGHT = 38;
    private final int EXPANDED_WIDTH = 340;
    private final int EXPANDED_HEIGHT = 120;

    private TextView tvCompactLeft, tvCompactRight;
    private TextView tvExpTitle, tvExpText;

    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            try {
                String action = intent.getAction();
                if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                         status == BatteryManager.BATTERY_STATUS_FULL;
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    if (isCharging) {
                        showIsland("⚡ جاري الشحن السريع", "نسبة البطارية: " + level + "%", "🔋");
                    }
                } else if ("com.pixel8.dynamicisland.NOTIF".equals(action)) {
                    String title = intent.getStringExtra("title");
                    String text = intent.getStringExtra("text");
                    String icon = intent.getStringExtra("icon");
                    showIsland(title != null ? title : "إشعار جديد", text != null ? text : "", icon != null ? icon : "💬");
                }
            } catch (Exception ignored) {}
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // التحقق من صلاحية الظهور فوق التطبيقات لتفادي الـ Crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        try {
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

            if (!isAttached && windowManager != null) {
                windowManager.addView(islandRoot, params);
                isAttached = true;
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction("com.pixel8.dynamicisland.NOTIF");
            registerReceiver(eventReceiver, filter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildIslandViews() {
        islandRoot = new FrameLayout(this);
        islandRoot.setBackground(createCurvedBackground(dpToPx(19), Color.BLACK, Color.parseColor("#38BDF8")));
        islandRoot.setClipToOutline(true);

        // واجهة الكبسولة المضغوطة (Compact)
        compactLayout = new LinearLayout(this);
        compactLayout.setOrientation(LinearLayout.HORIZONTAL);
        compactLayout.setGravity(Gravity.CENTER_VERTICAL);
        compactLayout.setPadding(dpToPx(12), 0, dpToPx(12), 0);

        tvCompactLeft = new TextView(this);
        tvCompactLeft.setText("الجزيرة التفاعلية 🏝️");
        tvCompactLeft.setTextColor(Color.WHITE);
        tvCompactLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvCompactLeft.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        compactLayout.addView(tvCompactLeft, leftParams);

        tvCompactRight = new TextView(this);
        tvCompactRight.setText("✨");
        tvCompactRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        compactLayout.addView(tvCompactRight);

        islandRoot.addView(compactLayout, new FrameLayout.LayoutParams(-1, -1));

        // واجهة البطاقة المنبثقة المتمددة (Expanded Card)
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
        tvExpText.setText("انقر لفتح لوحة التحكم والتفاعل مع الأنشطة الحية");
        tvExpText.setTextColor(Color.LTGRAY);
        tvExpText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        expandedLayout.addView(tvExpText);

        LinearLayout buttonsRow = new LinearLayout(this);
        buttonsRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonsRow.setGravity(Gravity.END);
        buttonsRow.setPadding(0, dpToPx(6), 0, 0);

        Button btnAction = new Button(this);
        btnAction.setText("فتح لوحة التحكم ↗");
        btnAction.setTextColor(Color.WHITE);
        btnAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btnAction.setBackground(createCurvedBackground(dpToPx(8), Color.parseColor("#0284C7"), Color.TRANSPARENT));
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntent);
                    }
                } catch (Exception ignored) {}
                shrinkIsland();
            }
        });
        buttonsRow.addView(btnAction);
        expandedLayout.addView(buttonsRow);

        islandRoot.addView(expandedLayout, new FrameLayout.LayoutParams(-1, -1));

        // استجابة الجزيرة للمس
        islandRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    expandIsland();
                } else {
                    shrinkIsland();
                }
            }
        });
    }

    public void showIsland(String title, String text) {
        showIsland(title, text, "💬", 5000);
    }

    public void showIsland(String title, String text, String iconOrType) {
        showIsland(title, text, iconOrType, 5000);
    }

    public void showIsland(final String title, final String text, final String iconOrType, final int durationMs) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (tvCompactLeft != null) tvCompactLeft.setText(title);
                    if (tvCompactRight != null) tvCompactRight.setText(iconOrType != null ? iconOrType : "🔔");
                    if (tvExpTitle != null) tvExpTitle.setText(title);
                    if (tvExpText != null) tvExpText.setText(text);

                    expandIsland();

                    if (shrinkRunnable != null) handler.removeCallbacks(shrinkRunnable);
                    shrinkRunnable = new Runnable() {
                        @Override
                        public void run() {
                            shrinkIsland();
                        }
                    };
                    handler.postDelayed(shrinkRunnable, durationMs);
                } catch (Exception ignored) {}
            }
        });
    }

    private void expandIsland() {
        if (isExpanded || !isAttached) return;
        isExpanded = true;
        compactLayout.setVisibility(View.GONE);
        expandedLayout.setVisibility(View.VISIBLE);
        animateIslandSize(COMPACT_WIDTH, EXPANDED_WIDTH, COMPACT_HEIGHT, EXPANDED_HEIGHT, 24);
    }

    private void shrinkIsland() {
        if (!isExpanded || !isAttached) return;
        isExpanded = false;
        expandedLayout.setVisibility(View.GONE);
        compactLayout.setVisibility(View.VISIBLE);
        animateIslandSize(EXPANDED_WIDTH, COMPACT_WIDTH, EXPANDED_HEIGHT, COMPACT_HEIGHT, 19);
    }

    private void animateIslandSize(int fromW, int toW, int fromH, int toH, int cornerRadius) {
        try {
            ValueAnimator animW = ValueAnimator.ofInt(dpToPx(fromW), dpToPx(toW));
            animW.setDuration(280);
            animW.setInterpolator(new OvershootInterpolator(1.1f));
            animW.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        if (isAttached && windowManager != null) {
                            params.width = (Integer) animation.getAnimatedValue();
                            windowManager.updateViewLayout(islandRoot, params);
                        }
                    } catch (Exception ignored) {}
                }
            });

            ValueAnimator animH = ValueAnimator.ofInt(dpToPx(fromH), dpToPx(toH));
            animH.setDuration(280);
            animH.setInterpolator(new OvershootInterpolator(1.1f));
            animH.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        if (isAttached && windowManager != null) {
                            params.height = (Integer) animation.getAnimatedValue();
                            islandRoot.setBackground(createCurvedBackground(dpToPx(cornerRadius), Color.BLACK, Color.parseColor("#38BDF8")));
                            windowManager.updateViewLayout(islandRoot, params);
                        }
                    } catch (Exception ignored) {}
                }
            });

            animW.start();
            animH.start();
        } catch (Exception ignored) {}
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
        instance = null;
        try {
            unregisterReceiver(eventReceiver);
            if (isAttached && windowManager != null && islandRoot != null) {
                windowManager.removeView(islandRoot);
                isAttached = false;
            }
        } catch (Exception ignored) {}
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
