package app.perkhub.Screens.login.models

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class LoginRes(
    val data: ArrayList<Data>,
    val errorCode: String,
    val errorMsg: String
)

data class SendOtpReq(
    @SerializedName("number")
    var Number: String
)

data class GetToken(
    @SerializedName("number")
    var Number: String
)


data class VerifyOtpReq(
    @SerializedName("number")
    var Number: String,
    @SerializedName("otp")
    var Otp: String
)

data class SaveUserDetail(
    @SerializedName("name") var name:String,
    @SerializedName("email") var email:String,
    @SerializedName("gender") var gender:String,
    @SerializedName("dob") var dob:String
)