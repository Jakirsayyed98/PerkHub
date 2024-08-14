package app.perkhub.Screens.Home.Model.MiniAppByCategory

import app.perkhub.Screens.Home.Model.Home.MiniApp

data class MiniAppbyCategory(
    val `data`: List<MiniApp>,
    val errorCode: String,
    val errorMsg: String
)