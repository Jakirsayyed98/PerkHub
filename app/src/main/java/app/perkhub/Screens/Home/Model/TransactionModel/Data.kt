package app.perkhub.Screens.Home.Model.TransactionModel

import app.perkhub.Screens.Home.Model.Home.MiniApp

data class Data(
    val affiliate_commission: String,
    val campaign_id: String,
    val campaign_name: String,
    val created_at: String,
    val id: Int,
    val macro_publisher: String,
    val miniApp: ArrayList<MiniApp>,
    val miniapp_id: String,
    val reference_id: String,
    val sale_amount: String,
    val status: String,
    val subId: String,
    val transaction_date: String,
    val transaction_id: String,
    val update_status: String,
    val updated_at: String,
    val user_commission: String,
    val user_commission_percentage: String,
    val user_id: String
)