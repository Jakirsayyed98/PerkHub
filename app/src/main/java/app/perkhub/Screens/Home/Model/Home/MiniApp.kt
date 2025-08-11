package app.perkhub.Screens.Home.Model.Home

import com.google.gson.annotations.SerializedName

data class MiniApp(
    @SerializedName( "id") val id: Int,
    @SerializedName( "store_category_id") val miniappCategoryId: String,
    @SerializedName( "name") val name: String,
    @SerializedName( "icon") val icon: String?,
    @SerializedName( "description") val description: String?,
    @SerializedName( "terms_and_conditions") val cashbackTerms: String?,
    @SerializedName( "cashback_rates") val cashbackRates: String?,
    @SerializedName( "status") val status: Int,
    @SerializedName( "url_type") val urlType: String,
    @SerializedName( "active") val active: Int,
    @SerializedName( "cd_active") val cbActive: Int,
    @SerializedName( "cashback") val cbPercentage: String,
//    @SerializedName( "cb_percentage") val cbPercentage: String,
    @SerializedName( "url") val url: String,
    @SerializedName( "label") val label: String?,
    @SerializedName( "banner") val banner: String?,
    @SerializedName( "logo") val logo: String?,
    @SerializedName( "macro_publisher") val macroPublisher: String,
    @SerializedName( "popular") val popular: Int,
    @SerializedName( "trending") val trending: Int,
    @SerializedName( "top_cashback") val topCashback: Int,
    @SerializedName( "about_store") val about: String?,
    @SerializedName( "how_its_work") val howitsWork: String?,
    @SerializedName( "created_at") val createdAt: String,
    @SerializedName( "updated_at") val updatedAt: String
)

