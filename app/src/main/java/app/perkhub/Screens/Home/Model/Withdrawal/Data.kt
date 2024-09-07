package app.perkhub.Screens.Home.Model.Withdrawal

data class Data(
    val VPA_Id: String,
    val created_at: String,
    val id: Int,
    val message: String,
    val requested_withdrawal_amt: String,
    val status: String,
    val txn_id: String,
    val txn_time: String,
    val type: String,
    val updated_at: String,
    val user_id: String
)