
package app.perkhub.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.perkhub.R
import app.perkhub.SplashActivity
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"

        private const val CHANNEL_ID = "perkHub_channel"
        private const val CHANNEL_NAME = "PerkHub Notifications"
        private const val DEFAULT_TITLE = "PerkHub"

        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_BODY = "body"
        private const val KEY_ICON = "icon"
        private const val KEY_BANNER = "banner"

        private val notificationId = AtomicInteger(1000)
    }

    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Received message. Data keys: ${remoteMessage.data.keys}")

        val data = remoteMessage.data

        // Prefer notification payload values when present.
        val notification = remoteMessage.notification

        val title = notification?.title
            ?: data[KEY_TITLE]
            ?: DEFAULT_TITLE

        val body = notification?.body
            ?: data[KEY_BODY]
            ?: data[KEY_MESSAGE]
            ?: ""

        // Do not display empty notifications.
        if (title.isBlank() && body.isBlank()) {
            Log.w(TAG, "Ignoring empty notification")
            return
        }

        showNotification(title, body, data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.i(TAG, "FCM token refreshed")

        // TODO: Upload the refreshed token to your backend API.
        // Handle the refreshed token.
        FcmTokenManager.handleNewToken(applicationContext, token)
    }
    private fun showNotification(
        title: String,
        messageBody: String,
        data: Map<String, String>
    ) {
        val intent = createNotificationIntent(data)

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId.incrementAndGet(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title.ifBlank { DEFAULT_TITLE })
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val iconUrl = data[KEY_ICON]
        val bannerUrl = data[KEY_BANNER]

        if (iconUrl.isNullOrBlank() && bannerUrl.isNullOrBlank()) {
            postNotification(builder)
            return
        }

        serviceScope.launch {
            try {
                val iconBitmap = iconUrl
                    ?.takeIf { it.isNotBlank() }
                    ?.let { loadBitmapFromUrl(it) }

                val bannerBitmap = bannerUrl
                    ?.takeIf { it.isNotBlank() }
                    ?.let { loadBitmapFromUrl(it) }

                withContext(Dispatchers.Main.immediate) {
                    iconBitmap?.let {
                        builder.setLargeIcon(it)
                    }

                    bannerBitmap?.let {
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(it)
                                .bigLargeIcon(null as Bitmap?)
                        )
                    }

                    postNotification(builder)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to prepare notification images", e)

                withContext(Dispatchers.Main.immediate) {
                    postNotification(builder)
                }
            }
        }
    }

    private fun createNotificationIntent(
        data: Map<String, String>
    ): Intent {
        return Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra("click_action", data["click_action"])
            putExtra("event", data["event"])
            putExtra("game_id", data["game_id"])
            putExtra("url", data["url"])

            // Preserve all FCM data for future routing.
            data.forEach { (key, value) ->
                if (key !in setOf("click_action", "event", "game_id", "url")) {
                    putExtra(key, value)
                }
            }
        }
    }

    private fun postNotification(
        builder: NotificationCompat.Builder
    ) {
        val manager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Android 13+ requires notification permission to be granted.
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification permission not granted")
            return
        }

        manager.notify(
            notificationId.incrementAndGet(),
            builder.build()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for PerkHub notifications"
            enableLights(true)
            enableVibration(true)
        }

        val manager = getSystemService(
            NotificationManager::class.java
        )

        manager.createNotificationChannel(channel)
    }

    private suspend fun loadBitmapFromUrl(
        url: String
    ): Bitmap? {
        return try {
            withContext(Dispatchers.IO) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(Uri.parse(url))
                    .submit()
                    .get()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Unable to load notification image", e)
            null
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
