package app.perkhub.Screens.Home.Model.Withdrawal

data class Data(
    val id: String,
    val requested_amt: String,
    val user_id: String,
    val reason: String,
    val VPA_Id: String,
    val status: String,
    val txn_id: String,
    val txn_time: String,
    val created_at: String,
    val updated_at: String,
)