package app.perkhub.room.games

import androidx.room.*
import app.perkhub.Screens.Games.model.Data
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {


    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<Data>>

    @Query("SELECT * FROM games")
    suspend fun getAllGamesOnce(): List<Data>

    @Query("SELECT * FROM games WHERE popular = 1 ORDER BY RANDOM() LIMIT 20")
    suspend fun getPopularGamesOnce(): List<Data>

    @Query("SELECT * FROM games WHERE popular = 1 ORDER BY RANDOM() LIMIT 20")
    fun getPopularGames(): Flow<List<Data>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Data>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Data)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGames(gameId: String)

    @Query("DELETE FROM games")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getCount(): Int

    @Query("SELECT * FROM games LIMIT 1")
    fun getCacheFlow(): Flow<Data?>

}
