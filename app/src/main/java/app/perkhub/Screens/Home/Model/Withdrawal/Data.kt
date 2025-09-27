package app.perkhub.Screens.Home.Model.Withdrawal

data class Data(
    val id: String,
    val user_id: String,
    val requested_amt: Double,
    val processed_amt: Double,
    val status: String,
    val reason: String,
    val txn_id: String,
    val txn_time: String,
    val created_at: String,
    val updated_at: String,
)