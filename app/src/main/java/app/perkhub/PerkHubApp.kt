package app.perkhub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import app.perkhub.notifications.FcmTokenManager

class PerkHubApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Retrieve the current FCM token when the app starts.
        FcmTokenManager.fetchCurrentToken(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "perkHub_channel",
                "PerkHub Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
