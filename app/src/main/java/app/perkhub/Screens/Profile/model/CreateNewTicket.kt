package app.perkhub.Screens.Profile.model

data class CreateNewTicketRequest(
    val subject:String,
    val body:String,
    val priority:String,
    val category:String
)

data class CreateNewTicketResponse(
val status:Int,
val message:String,
val data:String
)