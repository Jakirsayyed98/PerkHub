package app.perkhub.api

import com.google.gson.annotations.SerializedName

data class BaseRes(
    @SerializedName("message")
    val Message: String,

    @SerializedName("errorCode")
    val ErrorCode: String,

    @SerializedName("status")
    val Status: Int,

    @SerializedName("data")
    val Data: Data
)

data class Data(
    @SerializedName("token")
    val Token: String,
    @SerializedName("is_new_user")
    val IsNewUser: String,
)