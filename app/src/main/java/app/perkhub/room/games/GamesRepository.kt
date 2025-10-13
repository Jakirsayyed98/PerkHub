package app.perkhub.room.games

import android.content.Context
import app.perkhub.Screens.Games.model.Data
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow

class GamesRepository(context: Context) {
    private val gamesDao = HomeDatabase.getInstance(context).gamesDao()

    // ✅ Get all games as Flow for live observation
    fun getGames(): Flow<List<Data>> = gamesDao.getAllGames()

    // ✅ Get all games once (not Flow)
    suspend fun getAllGames(): List<Data> = gamesDao.getAllGamesOnce()

    // ✅ Get only popular games (once)
    suspend fun getAllPopularGames(): List<Data> = gamesDao.getPopularGamesOnce()

    // ✅ Get only popular games as Flow (real-time updates)
    fun getAllPopularGamesFlow(): Flow<List<Data>> = gamesDao.getPopularGames()

    // ✅ Save games list
    suspend fun saveGames(games: List<Data>) = gamesDao.insertGames(games)

    // ✅ Save single game
    suspend fun saveGame(game: Data) = gamesDao.insertGame(game)

    // ✅ Clear all games
    suspend fun clearGames() = gamesDao.clearAll()

    // ✅ Check if DB has any games
    suspend fun hasGames(): Boolean = gamesDao.getCount() > 0

    // ✅ Delete specific game by ID
    suspend fun deleteGames(gameId: String) = gamesDao.deleteGames(gameId)
}
