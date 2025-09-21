package app.perkhub.Screens.Home.Model.GenerateUrl

import app.perkhub.Screens.Home.Model.Games.Data

data class GenerateSubIdRes(
    val data: String,
    val errorCode: String,
    val errorMsg: String,
    val status: Int
)