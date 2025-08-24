package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName

data class Banner(
    val id: Int,
    val name: String,

    @SerializedName("banner_category_id")
    val bannerCategoryId: String,
    val image: String,
    val url: String,
    val status: Int,
    val createdAt: String,
    val updatedAt: String
)
