package app.perkhub.Screens.login.models

import com.google.gson.annotations.SerializedName

data class LoginRes(
    val data: Data,
    val errorCode: String,
    val errorMsg: String
)

data class SendOtpReq(
    @SerializedName("number")
    var Number: String
)


data class VerifyOtpReq(
    @SerializedName("number")
    var Number: String,
    @SerializedName("otp")
    var Otp: String
)