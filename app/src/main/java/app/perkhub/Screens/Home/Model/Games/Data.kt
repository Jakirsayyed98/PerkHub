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
    val hasIntegratedAds:  Boolean,
    val height: String,
    val id: String,
    val isPortrait: Boolean,
    val name: String,
    val numberOfRatings: String,
    val popular: Boolean,
    val privateAllowed:  Boolean,
    val rating: String,
    val status:  Boolean,
    val trending:  Boolean,
    val updated_at: String,
    val url: String,
    val width: String
)