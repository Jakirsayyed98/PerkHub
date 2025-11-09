package app.perkhub.room.games

import androidx.room.*
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GameCategoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesCategoryDao {
    @Query("SELECT * FROM games_category")
    fun getAllGamesCategory(): Flow<List<GameCategoryData>>

    @Query("SELECT * FROM games_category")
    suspend fun getAllGamesCategoryOnce(): List<GameCategoryData>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGamesCategory(games: List<GameCategoryData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameCategory(game: GameCategoryData)

    @Query("DELETE FROM games_category WHERE id = :gameId")
    suspend fun deleteGamesCategory(gameId: String)

    @Query("DELETE FROM games_category")
    suspend fun clearAllGameCategory()

    @Query("SELECT COUNT(*) FROM games_category")
    suspend fun getGameCategoryCount(): Int

}
