package app.perkhub.Screens.Home.Model.Withdrawal

data class withdrawalRes(
    val `data`: List<Data>,
    val errorCode: String,
    val errorMsg: String
)