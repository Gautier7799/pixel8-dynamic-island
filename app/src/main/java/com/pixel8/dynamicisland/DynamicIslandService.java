package com.pixel8.dynamicisland;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DynamicIslandService extends AccessibilityService {

    private WindowManager windowManager;
    private View islandContainer;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        showIsland();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    private void showIsland() {
        if (islandContainer != null) return;

        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);

            // شكل كبسولة سوداء مقوسة لـ Pixel 8
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(Color.BLACK);
            shape.setCornerRadius(80f);
            shape.setStroke(2, Color.parseColor("#333333"));
            layout.setBackground(shape);

            TextView tv = new TextView(this);
            tv.setText("🏝️ Pixel 8 Island");
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(13);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(30, 10, 30, 10);
            layout.addView(tv);

            islandContainer = layout;

            islandContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DynamicIslandService.this, "الجزيرة نشطة وتتفاعل معك! ✨", Toast.LENGTH_SHORT).show();
                }
            });

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    460,
                    120,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.y = 10;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }

            windowManager.addView(islandContainer, params);
            Toast.makeText(this, "تم إطلاق الجزيرة التفاعلية بنجاح! 🚀", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (islandContainer != null && windowManager != null) {
            windowManager.removeView(islandContainer);
            islandContainer = null;
        }
    }
}
