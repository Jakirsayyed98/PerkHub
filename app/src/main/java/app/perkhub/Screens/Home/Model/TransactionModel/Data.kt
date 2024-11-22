package app.perkhub.Screens.Home.Model.TransactionModel

import app.perkhub.Screens.Home.Model.Home.MiniApp

data class Data(
    val id: String,
    val campaign_id: String,
    val commission: String,
    val user_commission: String,
    val user_id: String,
    val order_id: String,
    val reference_id: String,
    val sale_amount: String,
    val status: String,
    val subid: String,
    val subid1: String,
    val subid2: String,
    val miniapp_id: String,
    val user_commission_percentage: String,
    val transaction_date: String,
    val transaction_id: String,
    val created_at: String,
    val updated_at: String,
    val miniapp_name: String,
    val miniApp: ArrayList<MiniApp>?,
)