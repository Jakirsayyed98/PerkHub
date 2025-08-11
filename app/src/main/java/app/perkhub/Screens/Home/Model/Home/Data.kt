package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName


data class Data(
//    val banner1: ArrayList<Banner>,
//    val banner2: Any?,
//    val banner3: Any?,
//    val banner4: Any?,
//    val banner5: Any?,
//    val banner6: Any?,
//    val banner7: Any?,
//    val banner8: Any?,
//    val banner9: Any?,
//    val banner10: Any?,
    @SerializedName("store_categories")
    val categories: List<Category>,
    @SerializedName("popular_stores")
    val popular: List<MiniApp>,
    @SerializedName("top_cashback_providers")
    val topCashback: List<MiniApp>,
    @SerializedName("trending_stores")
    val trending: List<MiniApp>
)
