package app.perkhub.room.Store

import android.content.Context
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow
class StoreRepository(context: Context) {

    private val appContext = context.applicationContext

    private val storeDao =
        HomeDatabase.getInstance(appContext).storeDao()

    private val prefs =
        appContext.getSharedPreferences(
            "stores_cache_pref",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val KEY_LAST_SYNC = "stores_last_sync"
        private const val CACHE_DURATION = 24 * 60 * 60 * 1000L
    }

    fun getStoresFlow(): Flow<List<MiniApp>> =
        storeDao.getAllStores()

    suspend fun getAllStores(): List<MiniApp> = storeDao.getAllStoresOnce()

    suspend fun getAllStoresByCategoryId(categoryId: String): List<MiniApp> = storeDao.getAllStoresByCategoryId(categoryId)

    suspend fun getAllPopularStores(): List<MiniApp> =
        storeDao.getPopularStoresOnce()

    fun getAllPopularStoresFlow(): Flow<List<MiniApp>> =
        storeDao.getPopularStores()

    /**
     * Replace old Room data with fresh API data.
     *
     * IMPORTANT:
     * We update the sync timestamp only after the data
     * has been successfully inserted.
     */
    suspend fun saveStores(stores: List<MiniApp>) {
        storeDao.replaceStores(stores)

        prefs.edit()
            .putLong(
                KEY_LAST_SYNC,
                System.currentTimeMillis()
            )
            .apply()
    }

    suspend fun saveStore(store: MiniApp) = storeDao.insertStore(store)

    suspend fun clearStores() = storeDao.clearAll()

    suspend fun hasStores(): Boolean = storeDao.getCount() > 0

    suspend fun deleteStore(storeId: String) = storeDao.deleteStore(storeId)

    /**
     * Returns true when today's data is already available.
     */
    suspend fun isCacheValid(): Boolean {

        val lastSync =
            prefs.getLong(
                KEY_LAST_SYNC,
                0L
            )

        if (lastSync == 0L) {
            return false
        }

        val now =
            System.currentTimeMillis()

        return (now - lastSync) < CACHE_DURATION
    }

    suspend fun shouldFetchFromApi(): Boolean {
        val hasData = storeDao.getCount() > 0
        if (!hasData) {
            return true
        }

        return !isCacheValid()
    }

    fun getLastSyncTime(): Long =
        prefs.getLong(
            KEY_LAST_SYNC,
            0L
        )
}