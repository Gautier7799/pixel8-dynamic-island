package com.pixel8.dynamicisland;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) return;
        try {
            if (getPackageName().equals(sbn.getPackageName())) return;

            CharSequence titleObj = sbn.getNotification().extras.getCharSequence("android.title");
            CharSequence textObj = sbn.getNotification().extras.getCharSequence("android.text");

            String title = titleObj != null ? titleObj.toString() : "إشعار جديد";
            String text = textObj != null ? textObj.toString() : "";
            String pkg = sbn.getPackageName();

            String icon = "💬";
            if (pkg.contains("whatsapp")) icon = "🟢 واتساب";
            else if (pkg.contains("telegram")) icon = "✈️ تيليجرام";
            else if (pkg.contains("spotify") || pkg.contains("music") || pkg.contains("youtube")) icon = "🎵 موسيقى";
            else if (pkg.contains("dialer") || pkg.contains("call")) icon = "📞 مكالمة";

            if (DynamicIslandService.instance != null) {
                DynamicIslandService.instance.showIsland(title, text, icon);
            }
        } catch (Exception ignored) {}
    }
}
