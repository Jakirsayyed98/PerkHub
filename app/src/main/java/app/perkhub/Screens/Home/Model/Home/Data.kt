package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("banner1")
    val banner1: List<Banner>?,

    @SerializedName("banner2")
    val banner2: List<Banner>?,

    @SerializedName("banner3")
    val banner3: List<Banner>?,

    @SerializedName("categories")
    val categories: List<Category>,

    @SerializedName("popular")
    val popular: List<MiniApp>,

    @SerializedName("top_cashback")
    val topCashback: List<MiniApp>,

    @SerializedName("trending")
    val trending: List<MiniApp>
)
