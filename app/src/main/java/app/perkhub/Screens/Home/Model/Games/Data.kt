package app.perkhub.Screens.Home.Model.Games

data class Data(
    val assets: Assets,
    val category_id: String,
    val code: String,
    val colorMuted: String,
    val colorVibrant: String,
//    val created_at: String,
    val description: String,
    val gamePlays: String,
    val gamePreviews: String,
    val hasIntegratedAds:  Int,
    val height: String,
    val id: Int,
    val isPortrait: Int,
    val name: String,
    val numberOfRatings: String,
    val popular: Int,
    val privateAllowed:  Int,
    val rating: String,
    val status:  Int,
    val trending:  Int,
    val updated_at: String,
    val url: String,
    val width: String
)