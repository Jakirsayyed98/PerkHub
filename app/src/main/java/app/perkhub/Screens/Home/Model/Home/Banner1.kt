package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName

data class Banner(
    val id: String,
    val name: String,

    @SerializedName("banner_category_id")
    val bannerCategoryId: String,
    val image: String,
    val url: String,

    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    val status: Boolean,
    val createdAt: String,
    val updatedAt: String
)
