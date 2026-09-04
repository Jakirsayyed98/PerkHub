package app.perkhub.room.Home

import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.api.MyApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HomeRepository(
    private val api: MyApi,
    private val homeCacheDao: HomeCacheDao
) {
    private val cacheDuration = TimeUnit.HOURS.toMillis(24) // 24 hours

    suspend fun getHomeData(): HomeRes? = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cached = homeCacheDao.getCacheFlow().firstOrNull()
        if (cached != null && now - cached.lastUpdated <= cacheDuration) {
            return@withContext cached.homeResponse
        }

        try {
            val response = api.getHomePage().body()

            if (response != null && response.status == 200) {
                homeCacheDao.insertCache(
                    HomeCache(
                        homeResponse = response,
                        lastUpdated = now
                    )
                )
                return@withContext response
            }
            cached?.homeResponse
        } catch (e: Exception) {
            cached?.homeResponse
        }
    }
}
