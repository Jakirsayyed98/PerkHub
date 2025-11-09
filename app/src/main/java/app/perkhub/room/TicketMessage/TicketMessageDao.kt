package app.perkhub.room.Tickets

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.perkhub.Screens.Profile.model.TicketMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketMessageDao {

    // ✅ Get all messages for a given ticket
    @Query("SELECT * FROM ticket_messages WHERE ticket_id = :ticketId ORDER BY created_at ASC")
    fun getMessagesByTicketId(ticketId: String): Flow<List<TicketMessage>>

    // ✅ Get all messages once (no Flow)
    @Query("SELECT * FROM ticket_messages WHERE ticket_id = :ticketId ORDER BY created_at ASC")
    suspend fun getMessagesByTicketIdOnce(ticketId: String): List<TicketMessage>

    // ✅ Insert or update a list of messages (upsert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<TicketMessage>)

    // ✅ Insert or update a single message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: TicketMessage)

    // ✅ Delete all messages for a ticket
    @Query("DELETE FROM ticket_messages WHERE ticket_id = :ticketId")
    suspend fun deleteMessagesByTicketId(ticketId: String)


    // ✅ Delete all messages (optional)
    @Query("DELETE FROM ticket_messages")
    suspend fun clearAllMessages()

    // ✅ Count messages for a ticket
    @Query("SELECT COUNT(*) FROM ticket_messages WHERE ticket_id = :ticketId")
    suspend fun getMessageCount(ticketId: String): Int
}
