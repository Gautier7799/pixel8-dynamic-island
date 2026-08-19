package com.pixel8.dynamicisland;

import android.app.Notification;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.util.List;

public class NotificationService extends NotificationListenerService {

    private String lastTrack = "";

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        try {
            setupMediaListener();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void setupMediaListener() {
        try {
            MediaSessionManager mm = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            if (mm == null) return;

            mm.addOnActiveSessionsChangedListener(controllers -> {
                try {
                    updateMedia(controllers);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }, null);

            List<MediaController> active = mm.getActiveSessions(null);
            updateMedia(active);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void updateMedia(List<MediaController> controllers) {
        if (controllers == null || controllers.isEmpty()) return;

        for (MediaController controller : controllers) {
            try {
                PlaybackState state = controller.getPlaybackState();
                if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                    MediaMetadata meta = controller.getMetadata();
                    if (meta != null) {
                        String title = meta.getString(MediaMetadata.METADATA_KEY_TITLE);
                        String artist = meta.getString(MediaMetadata.METADATA_KEY_ARTIST);
                        if (title != null && !title.equals(lastTrack)) {
                            lastTrack = title;
                            if (DynamicIslandService.instance != null) {
                                DynamicIslandService.instance.showIsland(
                                        title,
                                        artist != null ? artist : "جاري التشغيل",
                                        "🎵",
                                        Color.parseColor("#E91E63")
                                );
                            }
                        }
                    }
                    break;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if (sbn == null) return;
            String pkg = sbn.getPackageName();
            if (pkg == null || pkg.equals(getPackageName()) || pkg.contains("systemui")) return;

            Notification n = sbn.getNotification();
            if (n == null) return;

            if ((n.flags & Notification.FLAG_ONGOING_EVENT) != 0) return;

            Bundle extras = n.extras;
            if (extras == null) return;

            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

            if (title != null && text != null && DynamicIslandService.instance != null) {
                DynamicIslandService.instance.showIsland(
                        title.toString(),
                        text.toString(),
                        "💬",
                        Color.parseColor("#2196F3")
                );
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}
