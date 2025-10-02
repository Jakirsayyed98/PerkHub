package app.perkhub.Screens.withdrawal.model

import app.perkhub.Screens.Home.Model.Notification.Data

data class addPaymentMethodRes(
    val status: Int,
    val data: String,
    val message: String
)

data class addPaymentMethodReq(
    var upi_id: String,
    var bank_name:String,
    var bank_account_number:String,
    var ifsc_code:String
)