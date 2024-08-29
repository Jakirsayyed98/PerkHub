package app.perkhub.Screens.Home.Model.TransactionModel

data class TransactionDataRes(
    val `data`: List<Data>,
    val errorCode: String,
    val errorMsg: String
)