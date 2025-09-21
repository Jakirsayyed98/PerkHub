package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("status")
    val status: Boolean,   // ✅ must be Int (since API gives 1 or 0)

    @SerializedName("image")
    val image: String,

    @SerializedName("homepage_visible")
    val homepageVisible: Boolean,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)