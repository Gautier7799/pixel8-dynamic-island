package com.pixel8.dynamicisland

import android.app.Notification
import android.graphics.Color
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null || sbn.packageName == packageName) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        val island = DynamicIslandService.instance ?: return

        // للموسيقى
        if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION) || sbn.packageName.contains("spotify") || sbn.packageName.contains("youtube")) {
            if (title.isNotEmpty()) {
                island.showIsland("🎵 $title", text, "ılı", Color.parseColor("#FF4081"))
            }
            return
        }

        // للإشعارات العادية
        if (title.isNotEmpty() || text.isNotEmpty()) {
            island.showIsland("💬 $title", text, "●", Color.WHITE)
        }
    }
}
