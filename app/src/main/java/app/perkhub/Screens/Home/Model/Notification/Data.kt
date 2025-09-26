package app.perkhub.Screens.Home.Model.Notification

data class Data(
    val id: String,
    val title: String,
    val message: String,
    val image: String,
    val click_action: String,
    val status:Boolean,
    val created_at: String,
    val updated_at: String,
    val userId: String
)