package app.perkhub.Screens.Home.Model.Home

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stores")
data class MiniApp(
    @PrimaryKey
    @SerializedName( "id") val id: String,
    @SerializedName( "miniapp_category_id") val miniappCategoryId: String?,
    @SerializedName( "name") val name: String,
    @SerializedName( "icon") val icon: String?,
    @SerializedName( "banner") val banner: String?,
    @SerializedName( "logo") val logo: String?,
    @SerializedName( "description") val description: String?,
    @SerializedName( "about") val about: String?,
    @SerializedName( "cashback_terms") val cashback_terms: String?,
    @SerializedName( "is_cb_active") val cbActive: Boolean,
    @SerializedName( "cb_percentage") val cbPercentage: String?,
    @SerializedName( "url") val url: String?,
    @SerializedName( "url_type") val urlType: String?,
    @SerializedName( "macro_publisher") val macroPublisher: String?,
    @SerializedName( "is_active") val active: Boolean,
    @SerializedName( "is_popular") val popular: Boolean,
    @SerializedName( "is_trending") val trending: Boolean,
    @SerializedName( "is_top_cashback") val topCashback: Boolean,
    @SerializedName( "created_at") val createdAt: String?,
    @SerializedName( "updated_at") val updatedAt: String?
)

