package app.perkhub.Screens.Offers.model

import com.google.gson.annotations.SerializedName

data class Offerlistres(
    @SerializedName("data")
    val data: List<OffersData>,
    val message: String,
    val status: Int
)

data class OffersData(
    val coupon_code: String,
    val created_at: String,
    val description: String,
    val end_date: String,
    val id: String,
    val image: String,
    val offer_id: String,
    val start_date: String,
    val status: Boolean,
    val store_id: String,
    val store_name: String,
    val terms_and_condition: String,
    val title: String,
    val type: String,
    val updated_at: String,
    val url: String
)


data class SearchOfferReq(
    val brand_name:String
)