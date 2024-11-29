package app.perkhub.Screens.Home.Model.Withdrawal

data class withdrawalRes(
    val `data`: List<Data>,
    val message: String,
    val status: Int
)


data class WithdrawalReq(
    val upi_id: String,
    val request_amt:String
)