package app.perkhub.room.Tickets

import android.content.Context
import app.perkhub.Screens.Profile.model.Data
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow

class TicketsRepository(context: Context)  {
    private val ticketsDao = HomeDatabase.getInstance(context).ticketsDao()

    // Get notifications as Flow for real-time updates
    fun getTickets(): Flow<List<Data>> {
        return ticketsDao.getAllTickets()
    }

    // Get notifications once
    suspend fun getAllTickets(): List<Data> {
        return ticketsDao.getAllTicketsOnce()
    }

    // Save notifications to database
    suspend fun saveTickets(tickets: List<Data>) {
        ticketsDao.insertTickets(tickets)
    }

    // Save single notification (for push notifications)
    suspend fun saveTicket(ticket: Data) {
        ticketsDao.insertTicket(ticket)
    }

    // Clear all notifications
    suspend fun clearTickets() {
        ticketsDao.clearAll()
    }

    // Check if database has data
    suspend fun hasTickets(): Boolean {
        return ticketsDao.getCount() > 0
    }

    // Delete single notification
    suspend fun deleteTickets(ticketId: String) {
        ticketsDao.deleteTickets(ticketId)
    }
}