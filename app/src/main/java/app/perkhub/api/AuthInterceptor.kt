package app.perkhub.api

import android.content.Context
import android.util.Log
import app.perkhub.Screens.login.models.GetToken
import app.perkhub.api.tokens.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    private val mutex = Mutex() // Mutex to ensure we handle token refresh synchronously across requests

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Get the token from TokenManager (SharedPreferences, etc.)
        var token = TokenManager.getToken(context)

        // Add token to the request headers
        request = request.newBuilder()
            .addHeader("Authorization", "Bearer $token") // Ensure 'Bearer' prefix is correct
            .build()

        // Proceed with the request
        var response = chain.proceed(request)

        // If the token is expired (401 Unauthorized), attempt to refresh it
        if (response.code == 401) {
            // Synchronize token refresh to ensure only one token refresh happens at a time
            runBlocking {
                // Acquire the mutex lock to perform a token refresh in a coroutine
                mutex.withLock {
                    val newToken = refreshAuthTokenAsync()

                    // If token refresh is successful, retry the request with the new token
                    if (newToken != null) {
                        // Save the new token (e.g., in TokenManager)
                        TokenManager.saveToken(context, newToken.Data.Token)

                        // Retry the original request with the new token
                        request = request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer ${newToken.Data.Token}")
                            .build()

                        response = chain.proceed(request)
                    }
                }
            }
        }

        return response
    }

    // This is a suspend function that we call asynchronously to refresh the token
    private suspend fun refreshAuthTokenAsync(): BaseRes? {
        return try {
            val request = GetToken(TokenManager.getMobileNumberForToken(context).toString())
            val response = MyApi(context).getAuthorization(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("AuthInterceptor", "Token refresh failed: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Error refreshing token: ${e.message}")
            null
        }
    }
}
