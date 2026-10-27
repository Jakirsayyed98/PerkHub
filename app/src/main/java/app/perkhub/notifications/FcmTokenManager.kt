
package app.perkhub.notifications

import android.content.Context
import android.util.Log
import app.perkhub.api.MyApi
import app.perkhub.api.NativeEndpoints
import app.perkhub.Screens.Home.Model.Notification.TokenReq
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object FcmTokenManager {

    private const val TAG = "FcmTokenManager"

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    fun fetchCurrentToken(context: Context) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (token.isBlank()) {
                    Log.w(TAG, "FCM token is empty")
                    return@addOnSuccessListener
                }

                syncToken(context.applicationContext, token)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve FCM token", exception)
            }
    }

    fun handleNewToken(context: Context, token: String) {
        if (token.isBlank()) {
            Log.w(TAG, "Received empty FCM token")
            return
        }

        syncToken(context.applicationContext, token)
    }

    private fun syncToken(context: Context, token: String) {
        scope.launch {
            try {
                val api = MyApi(context)

                val response = api.updateNotification(
                    NativeEndpoints.updateNotificationToken(),
                    TokenReq(token)
                )

                if (response.isSuccessful) {
                    Log.i(TAG, "FCM token uploaded successfully")
                } else {
                    Log.e(
                        TAG,
                        "FCM token upload failed: HTTP ${response.code()}"
                    )
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Error uploading FCM token", exception)
            }
        }
    }
}
