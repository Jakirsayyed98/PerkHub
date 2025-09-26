package app.perkhub.Screens.Home.Model.TransactionModel

import com.google.gson.annotations.SerializedName

data class TransactionDataRes(
    @SerializedName("data")
    val data: Datas,
    val message: String,
    val status: Int,
)

data class Datas(
    val data: ArrayList<Data>,
    val pending: String,
    val rejected: String,
    val verified: String,
    val withdrawal: String,
)


