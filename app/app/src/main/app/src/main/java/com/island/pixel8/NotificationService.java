package com.pixel8.dynamicisland;

import android.app.Notification;
import android.graphics.Color;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || getPackageName().equals(sbn.getPackageName())) return;

        Notification notification = sbn.getNotification();
        if (notification == null) return;
        Bundle extras = notification.extras;
        if (extras == null) return;

        String title = extras.getString(Notification.EXTRA_TITLE, "");
        CharSequence charSeq = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = charSeq != null ? charSeq.toString() : "";

        DynamicIslandService island = DynamicIslandService.instance;
        if (island == null) return;

        // التقاط الموسيقى
        if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION) ||
            sbn.getPackageName().contains("spotify") ||
            sbn.getPackageName().contains("youtube")) {
            if (!title.isEmpty()) {
                island.showIsland("🎵 " + title, text, "ılı", Color.parseColor("#FF4081"));
            }
            return;
        }

        // التقاط الإشعارات العامة
        if (!title.isEmpty() || !text.isEmpty()) {
            island.showIsland("💬 " + title, text, "●", Color.WHITE);
        }
    }
}
