package app.perkhub.api

import com.google.gson.annotations.SerializedName

data class ApiErrorRes(
    @SerializedName("error")
    val error: ApiError?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("status")
    val status: Int?
)

data class ApiError(
    @SerializedName("code")
    val code: String?,

    @SerializedName("message")
    val message: String?
)

