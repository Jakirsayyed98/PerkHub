package app.perkhub.Screens.Profile.model

data class RequestTicketMessage(
    val ticketId:String,
    val msg:String
)

data class TicketMessageResponse(
val status:Int,
val message:String,
val data:String
)