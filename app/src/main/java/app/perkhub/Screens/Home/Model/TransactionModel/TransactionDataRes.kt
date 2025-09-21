package app.perkhub.Screens.Home.Model.TransactionModel

data class TransactionDataRes(
    val data: ArrayList<Data>,
    val message: String,
    val status: Int,
    
    val pending: String,
    val rejected: String,
    val verified: String,
    val withdrawal: String,
)