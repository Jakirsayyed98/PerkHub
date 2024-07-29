package app.perkhub.login.models

data class LoginRes(
    val data: List<Data>,
    val errorCode: String,
    val errorMsg: String
)