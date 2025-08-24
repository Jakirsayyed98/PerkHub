package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("status")
    val status: Int,   // ✅ must be Int (since API gives 1 or 0)

    @SerializedName("image")
    val image: String,

    @SerializedName("homepage_visible")
    val homepageVisible: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)