package app.perkhub.room.games

import android.content.Context
import app.perkhub.Screens.Games.model.Data
import app.perkhub.room.HomeDatabase
import kotlinx.coroutines.flow.Flow

class GamesRepository(context: Context) {
    private val gamesDao = HomeDatabase.getInstance(context).gamesDao()

    // Get all games as Flow for live observation
    fun getGames(): Flow<List<Data>> = gamesDao.getAllGames()

    // Get all games once (not Flow)
    suspend fun getAllGames(): List<Data> = gamesDao.getAllGamesOnce()

    // Get only popular games (once)
    suspend fun getAllPopularGames(): List<Data> = gamesDao.getPopularGamesOnce()

    // Get only popular games as Flow (real-time updates)
    fun getAllPopularGamesFlow(): Flow<List<Data>> = gamesDao.getPopularGames()

    // Save games list
    suspend fun saveGames(games: List<Data>) = gamesDao.insertGames(games)

    // Save single game
    suspend fun saveGame(game: Data) = gamesDao.insertGame(game)

    // Clear all games
    suspend fun clearGames() = gamesDao.clearAll()

    // Check if DB has any games
    suspend fun hasGames(): Boolean = gamesDao.getCount() > 0

    // Delete specific game by ID
    suspend fun deleteGames(gameId: String) = gamesDao.deleteGames(gameId)



    private val PREF_NAME = "games_cache_pref"
    private val KEY_POPULAR_LAST_SYNC = "popular_last_sync"

    private fun savePopularLastSync(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_POPULAR_LAST_SYNC, System.currentTimeMillis()).apply()
    }

    private fun getPopularLastSync(context: Context): Long {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_POPULAR_LAST_SYNC, 0L)
    }

    fun isPopularCacheValid(context: Context): Boolean {
        val lastSync = getPopularLastSync(context)
        val now = System.currentTimeMillis()
        val hours24 = 24 * 60 * 60 * 1000L
        return (now - lastSync) < hours24
    }

    suspend fun savePopularGames(games: List<app.perkhub.Screens.Games.model.Data>, context: Context) {
        saveGames(games) // your existing DB save function
        savePopularLastSync(context)
    }

}
