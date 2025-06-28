package app.perkhub.Screens.Home.Model.Home

data class HomeRes(
    val data: ArrayList<Data>,

    val errorMsg: String,
    val errorCode: String,
    val status: Int
)