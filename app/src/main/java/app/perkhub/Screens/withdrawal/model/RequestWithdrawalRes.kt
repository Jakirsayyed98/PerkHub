package app.perkhub.Screens.withdrawal.model

import app.perkhub.Screens.Home.Model.Notification.Data

data class RequestWithdrawalRes(
    val status: Int,
    val data: String,
    val message: String
)

data class RequestWithdrawalReq(
    var request_amt:String,
    var payment_method:String
)