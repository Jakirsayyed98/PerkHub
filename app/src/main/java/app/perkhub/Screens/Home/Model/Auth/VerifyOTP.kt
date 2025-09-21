package app.perkhub.Screens.Home.Model.Auth

import com.google.gson.annotations.SerializedName

data class VerifyOTP(
    @SerializedName("errorCode") val errorCode: String,
    @SerializedName("errorMsg") val errorMsg: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: AuthData?
)

data class AuthData(
    @SerializedName("token") val token: String?,
    @SerializedName("is_new_user") val isNewUser: Boolean = false
)
