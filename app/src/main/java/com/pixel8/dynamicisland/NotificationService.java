package com.pixel8.dynamicisland;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {

    public static final String ACTION_NEW_NOTIFICATION = "com.pixel8.dynamicisland.NEW_NOTIFICATION";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getPackageName().equals(getPackageName())) return;

        Notification notification = sbn.getNotification();
        if (notification == null || notification.extras == null) return;

        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        CharSequence textChar = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = textChar != null ? textChar.toString() : "";

        if (!title.isEmpty() || !text.isEmpty()) {
            Intent intent = new Intent(ACTION_NEW_NOTIFICATION);
            intent.putExtra("title", title);
            intent.putExtra("text", text);
            intent.putExtra("package", sbn.getPackageName());
            sendBroadcast(intent);
        }
    }
}
