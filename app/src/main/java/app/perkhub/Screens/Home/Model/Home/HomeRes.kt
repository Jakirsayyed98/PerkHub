package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName

data class HomeRes(
    @SerializedName("data")
    val data: Data,

    @SerializedName("errorMsg")
    val errorMsg: String,

    @SerializedName("errorCode")
    val errorCode: String,

    @SerializedName("status")
    val status: Int
)