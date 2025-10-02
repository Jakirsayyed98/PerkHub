package app.perkhub.Screens.withdrawal.model

data class getPaymentMethodsRes(
    val status: Int,
    val data: ArrayList<Data>,
    val message: String
)

data class Data(
    val id: String,
    val user_id: String,
    val type: String,
    val identifier: String?,
    val bank_name: String?,
    val ifsc_code: String?,
    val is_default:Boolean,
)