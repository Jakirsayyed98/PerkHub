package app.perkhub.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.perkhub.R
import app.perkhub.SplashActivity
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "perkHub_channel"
        private const val CHANNEL_NAME = "PerkHub Notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Handle notification payload (title + body)
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "PerkHub", it.body ?: "", remoteMessage.data)
        }

        // Handle data-only payload
        if (remoteMessage.data.isNotEmpty() && remoteMessage.notification == null) {
            val title = remoteMessage.data["title"] ?: "PerkHub"
            val body = remoteMessage.data["message"] ?: ""
            sendNotification(title, body, remoteMessage.data)
        }

        Log.d("FCM", "Message data payload: ${remoteMessage.data}")
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        // TODO: send this token to your backend API for push notifications
    }

    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {
        val intent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("click_action", data["click_action"])
            putExtra("event", data["event"])
            putExtra("game_id", data["game_id"])
            putExtra("url", data["url"])
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(), // unique request code
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_notification) // REQUIRED: small white icon
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // If data contains "icon", load it as large icon
        val iconUrl = data["icon"]
        val bannerUrl = data["banner"]

        CoroutineScope(Dispatchers.IO).launch {
            val iconBitmap = iconUrl?.takeIf { it.isNotEmpty() }?.let { loadBitmapFromUrl(it) }
            val bannerBitmap = bannerUrl?.takeIf { it.isNotEmpty() }?.let { loadBitmapFromUrl(it) }

            withContext(Dispatchers.Main) {
                iconBitmap?.let { notificationBuilder.setLargeIcon(it) }

                bannerBitmap?.let {
                    val style = NotificationCompat.BigPictureStyle()
                        .bigPicture(it)
                        .bigLargeIcon(null as Bitmap?) // Fix ambiguity
                    notificationBuilder.setStyle(style)
                }

                notifyNotification(notificationBuilder)
            }
        }

    }

    private fun notifyNotification(builder: NotificationCompat.Builder) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for PerkHub notifications"
                enableLights(true)
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun loadBitmapFromUrl(url: String): Bitmap? {
        return try {
            Glide.with(this)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: Exception) {
            Log.e("FCM", "Failed to load bitmap: $e")
            null
        }
    }
}
