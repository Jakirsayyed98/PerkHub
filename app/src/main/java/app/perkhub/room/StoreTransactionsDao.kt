package app.perkhub.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreTransactionsDao {
    @Query("SELECT * FROM store_transactions ORDER BY created_at DESC")
    fun getAllStoreTransactions(): Flow<List<Data>>

    @Query("SELECT * FROM store_transactions ORDER BY created_at DESC")
    suspend fun getAllStoreTransactionsOnce(): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreTransactions(storeTransactions: List<Data>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreTransaction(storeTransaction: Data)

    @Query("DELETE FROM store_transactions")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM store_transactions")
    suspend fun getCount(): Int

    @Query("DELETE FROM store_transactions WHERE id = :storeTransactionId")
    suspend fun deleteStoreTransactions(storeTransactionId: String)
}