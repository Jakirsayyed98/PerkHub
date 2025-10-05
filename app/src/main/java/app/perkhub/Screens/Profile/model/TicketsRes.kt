package app.perkhub.Screens.Profile.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class TicketsRes(
    val status:Int,
    val message:String,
    val data : List<Data>
)

@Entity(tableName = "tickets")
data class Data(
    @PrimaryKey
    val id : String,
    val subject : String,
    val status : String,
    val priority : String,
    val category : String,
    val created_at : String,
    val last_message : String,
    val last_message_time : String,
)