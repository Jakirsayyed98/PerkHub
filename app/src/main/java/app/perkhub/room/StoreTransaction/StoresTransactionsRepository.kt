package app.perkhub.room.StoreTransaction

import android.content.Context
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow

class StoresTransactionsRepository(context: Context) {
    private val notificationDao = HomeDatabase.getInstance(context).storeTransactionDao()

    // Get store transactions as Flow for real-time updates
    fun getStoresTransactionsFlow(): Flow<List<Data>> {
        return notificationDao.getAllStoreTransactions()
    }

    // Get StoresTransactions once
    suspend fun getStoresTransactions(): List<Data> {
        return notificationDao.getAllStoreTransactionsOnce()
    }

    // Save StoresTransactions to database
    suspend fun saveStoresTransactions(storeTransactions: List<Data>) {
        notificationDao.insertStoreTransactions(storeTransactions)
    }

    // Save single StoresTransactions
    suspend fun saveStoresTransactions(storeTransaction: Data) {
        notificationDao.insertStoreTransaction(storeTransaction)
    }

    // Clear all StoresTransactions
    suspend fun clearStoresTransactions() {
        notificationDao.clearAll()
    }

    // Check if database has data
    suspend fun hasStoresTransactions(): Boolean {
        return notificationDao.getCount() > 0
    }

    // Delete single notification
    suspend fun deleteStoresTransactions(transactionID: String) {
        notificationDao.deleteStoreTransactions(transactionID)
    }
}