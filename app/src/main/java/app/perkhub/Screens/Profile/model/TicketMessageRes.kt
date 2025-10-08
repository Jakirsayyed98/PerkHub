package app.perkhub.Screens.Profile.model

import java.text.SimpleDateFormat
import java.util.*

data class TicketMessageRes(
    val status: Int,
    val message: String,
    val data: DataTicket
)

data class DataTicket(
    val id: String,
    val user_id: String,
    val subject: String,
    val status: String,
    val created_at: String,
    val ticket_messages: List<TicketMessage>
)

data class TicketMessage(
    val id: String,
    val ticket_id: String,
    val author_type: String, // "user" or "admin"
    val body: String,
    val created_at: String
) {
    fun getFormattedTime(): String {
        return try {
            // Example: "2025-10-04T22:54:55.473569+05:30"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(created_at)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}
