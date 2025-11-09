package app.perkhub.room.games

import android.content.Context
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GameCategoryData
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow

class GamesCategoryRepository(context: Context) {
    private val gamesCategoryDao = HomeDatabase.getInstance(context).gamesCategoryDao()

    // Get all games as Flow for live observation
    fun getGamesCategory(): Flow<List<GameCategoryData>> = gamesCategoryDao.getAllGamesCategory()

    // Get all games once (not Flow)
    suspend fun getAllGamesCategory(): List<GameCategoryData> = gamesCategoryDao.getAllGamesCategoryOnce()
    
    // Save games list
    suspend fun saveGamesCategory(games: List<GameCategoryData>) = gamesCategoryDao.insertGamesCategory(games)

    // Save single game
    suspend fun saveGameCategory(game: GameCategoryData) = gamesCategoryDao.insertGameCategory(game)

    // Clear all games
    suspend fun clearGamesCategory() = gamesCategoryDao.clearAllGameCategory()

    // Check if DB has any games
    suspend fun hasGamesCategory(): Boolean = gamesCategoryDao.getGameCategoryCount() > 0

    // Delete specific game by ID
    suspend fun deleteGamesCategory(gameId: String) = gamesCategoryDao.deleteGamesCategory(gameId)
}
