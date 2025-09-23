package app.perkhub.room
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.api.MyApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.TimeUnit
class HomeRepository(
    private val api: MyApi,
    private val homeCacheDao: HomeCacheDao
) {
    private val cacheDuration = 24 * 60 * 60 * 1000L // 24 hours

    suspend fun getHomeData(): HomeRes? {
        val now = System.currentTimeMillis()
        val cached = homeCacheDao.getCacheFlow().firstOrNull()
        if (cached != null && now - cached.lastUpdated <= cacheDuration) {
            return cached.homeResponse
        }

        val response = api.getHomePage().body() ?: return null
        homeCacheDao.insertCache(HomeCache(homeResponse = response, lastUpdated = now))
        return response
    }
}
