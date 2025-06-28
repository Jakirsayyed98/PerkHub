package app.perkhub.api

import com.google.gson.annotations.SerializedName

data class BaseRes(
    @SerializedName("errorMsg")
    val Message: String,

    @SerializedName("errorCode")
    val ErrorCode: String,

    @SerializedName("status")
    val Status: String,

    @SerializedName("data")
    val Data: String
)

data class Data(
    @SerializedName("token")
    val Token: String
)