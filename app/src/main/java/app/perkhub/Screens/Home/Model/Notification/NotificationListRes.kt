package app.perkhub.Screens.Home.Model.Notification

data class NotificationListRes(
    val `data`: List<Data>,
    val errorCode: String,
    val errorMsg: String
)