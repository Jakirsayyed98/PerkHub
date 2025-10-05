package app.perkhub.room.Tickets

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.perkhub.Screens.Profile.model.Data
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketsDao {
    @Query("SELECT * FROM tickets ORDER BY created_at DESC")
    fun getAllTickets(): Flow<List<Data>>

    @Query("SELECT * FROM tickets ORDER BY created_at DESC")
    suspend fun getAllTicketsOnce(): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<Data>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(tickets: Data)

    @Query("DELETE FROM tickets")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM tickets")
    suspend fun getCount(): Int

    @Query("DELETE FROM tickets WHERE id = :ticketsId")
    suspend fun deleteTickets(ticketsId: String)
}