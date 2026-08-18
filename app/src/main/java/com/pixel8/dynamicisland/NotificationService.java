package com.pixel8.dynamicisland;

import android.app.Notification;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.util.List;

public class NotificationService extends NotificationListenerService {

    public static final String ACTION_NEW_NOTIFICATION = "com.pixel8.dynamicisland.NEW_NOTIFICATION";
    public static final String ACTION_MEDIA_STATE = "com.pixel8.dynamicisland.MEDIA_STATE";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getPackageName().equals(getPackageName())) return;

        Notification notification = sbn.getNotification();
        if (notification == null || notification.extras == null) return;

        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        CharSequence textChar = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = textChar != null ? textChar.toString() : "";

        // فحص إذا كان إشعار وسائط/موسيقى
        if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
            Intent mediaIntent = new Intent(ACTION_MEDIA_STATE);
            mediaIntent.putExtra("track", title);
            mediaIntent.putExtra("artist", text);
            mediaIntent.putExtra("isPlaying", true);
            sendBroadcast(mediaIntent);
            return;
        }

        if (!title.isEmpty() || !text.isEmpty()) {
            Intent intent = new Intent(ACTION_NEW_NOTIFICATION);
            intent.putExtra("title", title);
            intent.putExtra("text", text);
            intent.putExtra("package", sbn.getPackageName());
            sendBroadcast(intent);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn != null && sbn.getNotification() != null && sbn.getNotification().extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
            Intent mediaIntent = new Intent(ACTION_MEDIA_STATE);
            mediaIntent.putExtra("isPlaying", false);
            sendBroadcast(mediaIntent);
        }
    }
}
