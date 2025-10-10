package app.perkhub.room.Tickets

import android.content.Context
import app.perkhub.Screens.Profile.model.TicketMessage
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow

class TicketMessageRepository(context: Context) {

    private val messageDao = HomeDatabase.getInstance(context).ticketMessageDao()

    /** 🔹 Get all messages (real-time) for a specific ticket */
    fun getMessages(ticketId: String): Flow<List<TicketMessage>> {
        return messageDao.getMessagesByTicketId(ticketId)
    }

    /** 🔹 Get all messages once (no Flow) */
    suspend fun getMessagesOnce(ticketId: String): List<TicketMessage> {
        return messageDao.getMessagesByTicketIdOnce(ticketId)
    }

    /** 🔹 Save or update list of messages (upsert) */
    suspend fun saveMessages(messages: List<TicketMessage>) {
        messageDao.insertMessages(messages)
    }

    /** 🔹 Save a single message (for newly sent or incoming ones) */
    suspend fun saveMessage(message: TicketMessage) {
        messageDao.insertMessage(message)
    }

    /** 🔹 Delete all messages for a given ticket */
    suspend fun clearMessagesByTicket(ticketId: String) {
        messageDao.deleteMessagesByTicketId(ticketId)
    }

    /** 🔹 Delete all messages (global) */
    suspend fun clearAllMessages() {
        messageDao.clearAllMessages()
    }

    /** 🔹 Count how many messages exist for a ticket */
    suspend fun getMessageCount(ticketId: String): Int {
        return messageDao.getMessageCount(ticketId)
    }
}
