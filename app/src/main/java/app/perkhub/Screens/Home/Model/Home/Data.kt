package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName


data class Data(
    val banner1: ArrayList<Banner>,
    val banner2: Any?,
    val banner3: Any?,
    val banner4: Any?,
    val banner5: Any?,
    val banner6: Any?,
    val banner7: Any?,
    val banner8: Any?,
    val banner9: Any?,
    val banner10: Any?,
    val categories: ArrayList<Category>,
    val popular: ArrayList<MiniApp>,
    @SerializedName("top_cashback")
    val topCashback: ArrayList<MiniApp>,
    val trending: ArrayList<MiniApp>,
    @SerializedName("category_list")
    val categoryList: ArrayList<CategoryX>
)
