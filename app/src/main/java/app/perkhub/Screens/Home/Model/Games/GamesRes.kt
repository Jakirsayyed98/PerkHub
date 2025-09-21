package app.perkhub.Screens.Home.Model.Games

data class GamesRes(
    val data: ArrayList<Data>,
    val errorMsg: String,
    val errorCode: String,
    val status: Int
)