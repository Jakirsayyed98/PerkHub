package app.perkhub.room.Home

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeCacheDao {
    @Query("SELECT * FROM home_cache LIMIT 1")
    fun getCacheFlow(): Flow<HomeCache?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: HomeCache)
}
