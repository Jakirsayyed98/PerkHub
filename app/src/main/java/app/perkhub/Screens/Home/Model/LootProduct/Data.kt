package app.perkhub.Screens.Home.Model.LootProduct

data class Data(
    val MiniApps: List<MiniApp>,
    val coupon_code: Any,
    val coupon_status: String,
    val created_at: String,
    val description: String,
    val dis_price: String,
    val end_date: String,
    val id: Int,
    val image: String,
    val miniAppID: String,
    val price: String,
    val status: String,
    val title: String,
    val updated_at: String,
    val url: String
)