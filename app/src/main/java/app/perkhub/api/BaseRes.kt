package app.perkhub.api

import com.google.gson.annotations.SerializedName

data class BaseRes(
    @SerializedName("message")
    val Message: String,

    @SerializedName("status")
    val Status: String,

    @SerializedName("data")
    val Data: Data
)

data class Data(
    @SerializedName("token")
    val Token: String
)