package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("banner1")
    val banner1: List<Banner>?,

    @SerializedName("banner2")
    val banner2: List<Banner>?,

    @SerializedName("banner3")
    val banner3: List<Banner>?,

    @SerializedName("store_categories")
    val categories: List<Category>,

    @SerializedName("popular_stores")
    val popular: List<MiniApp>,

    @SerializedName("top_cashback_providers")
    val topCashback: List<MiniApp>,

    @SerializedName("trending_stores")
    val trending: List<MiniApp>
)
