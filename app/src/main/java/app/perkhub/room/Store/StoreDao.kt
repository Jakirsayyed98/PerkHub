package app.perkhub.room.Store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import app.perkhub.Screens.Home.Model.Home.MiniApp
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Query("SELECT * FROM stores")
    fun getAllStores(): Flow<List<MiniApp>>

    @Query("SELECT * FROM stores")
    suspend fun getAllStoresOnce(): List<MiniApp>

    @Query("SELECT * FROM stores WHERE miniappCategoryId = :categoryId")
    suspend fun getAllStoresByCategoryId(categoryId: String): List<MiniApp>

    @Query("""
        SELECT *
        FROM stores
        WHERE popular = 1
        ORDER BY RANDOM()
    """)
    suspend fun getPopularStoresOnce(): List<MiniApp>

    @Query("""
        SELECT *
        FROM stores
        WHERE popular = 1
        ORDER BY RANDOM()
        LIMIT 20
    """)
    fun getPopularStores(): Flow<List<MiniApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(
        stores: List<MiniApp>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(
        store: MiniApp
    )

    @Query("DELETE FROM stores WHERE id = :storeId")
    suspend fun deleteStore(
        storeId: String
    )

    @Query("DELETE FROM stores")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun getCount(): Int

    @Query("SELECT * FROM stores LIMIT 1")
    fun getCacheFlow(): Flow<MiniApp?>

    /**
     * Replace entire dataset.
     */
    @Transaction
    suspend fun replaceStores(
        stores: List<MiniApp>
    ) {
        clearAll()
        insertStores(stores)
    }
}