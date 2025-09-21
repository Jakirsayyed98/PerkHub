package app.perkhub.Screens.Home.Model.Notification

import com.google.gson.annotations.SerializedName

data class TokenReq(
    @SerializedName("fcm_token")
    val fcmToken :String?
)
