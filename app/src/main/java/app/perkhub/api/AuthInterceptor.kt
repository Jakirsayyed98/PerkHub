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

    private val mutex = Mutex()
    @Volatile private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = TokenManager.getToken(context)

        val requestWithAuth = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        Log.d("@@@@@@@",requestWithAuth.toString())
        val response = chain.proceed(requestWithAuth)

        if (response.code != 401) {
            return response
        }

        response.close()

        return runBlocking {
            mutex.withLock {

                val latestToken = TokenManager.getToken(context)
                if (latestToken != token && !latestToken.isNullOrEmpty()) {
                    return@runBlocking retryRequest(chain, originalRequest, latestToken)
                }

                if (!isRefreshing) {
                    isRefreshing = true

                    val newToken = refreshAuthToken()

                    isRefreshing = false

                    if (!newToken.isNullOrEmpty()) {
                        TokenManager.saveToken(context, newToken)
                        return@runBlocking retryRequest(chain, originalRequest, newToken)
                    } else {
                        return@runBlocking chain.proceed(requestWithAuth)
                    }
                }

                while (isRefreshing) {
                    delay(100)
                }

                val refreshedToken = TokenManager.getToken(context)

                return@runBlocking if (!refreshedToken.isNullOrEmpty()) {
                    retryRequest(chain, originalRequest, refreshedToken)
                } else {
                    chain.proceed(requestWithAuth)
                }
            }
        }
    }

    private fun retryRequest(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
        newToken: String
    ): Response {
        val newRequest = originalRequest.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newToken")
            .build()

        return chain.proceed(newRequest)
    }

    private suspend fun refreshAuthToken(): String? {
        return try {
            val mobileNumber = TokenManager.getMobileNumberForToken(context)
            if (mobileNumber.isNullOrEmpty()) return null

            val request = GetToken(mobileNumber)
            val apiService = MyApi(context)
            val response = apiService.getAuthorization(
                NativeEndpoints.authorization(),
                request
            )

            if (response.isSuccessful) {
                response.body()?.Data?.Token
            } else null

        } catch (e: Exception) {
            null
        }
    }
}


/*

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    private val mutex = Mutex()
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the current token
        var token = TokenManager.getToken(context)

        // Create request with authorization header
        val requestWithAuth = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Execute the request
        var response = chain.proceed(requestWithAuth)

        // If we get 401, attempt token refresh
        if (response.code == 401) {
            response.close() // Close the original response
            // Use runBlocking to handle token refresh synchronously
            return runBlocking {
                mutex.withLock {
                    // Check if token was already refreshed by another request
                    val currentToken = TokenManager.getToken(context)
                    if (currentToken != token && !isRefreshing) {
                        // Token was already refreshed, retry with new token
                        return@runBlocking retryRequestWithNewToken(chain, originalRequest, currentToken)
                    }

                    // Refresh the token
                    if (!isRefreshing) {
                        isRefreshing = true
                        val newToken = refreshAuthTokenAsync()
                        isRefreshing = false

                        if (newToken != null) {
                            // Save the new token
                            TokenManager.saveToken(context, newToken.Data.Token)

                            // Retry the original request with new token
                            return@runBlocking retryRequestWithNewToken(chain, originalRequest, newToken.Data.Token)
                        } else {
                            // Return the original 401 response if refresh failed
                            return@runBlocking chain.proceed(requestWithAuth)
                        }
                    } else {
                        // Another thread is refreshing, wait and retry
                        delay(100)
                        val refreshedToken = TokenManager.getToken(context)
                        return@runBlocking retryRequestWithNewToken(chain, originalRequest, refreshedToken)
                    }
                }
            }
        }

        return response
    }

    private fun retryRequestWithNewToken(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
        newToken: String?
    ): Response {
        val newRequest = originalRequest.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newToken")
            .build()

        return chain.proceed(newRequest)
    }

    private suspend fun refreshAuthTokenAsync(): BaseRes? {
        return try {
            val mobileNumber = TokenManager.getMobileNumberForToken(context)
            if (mobileNumber.isNullOrEmpty()) {
                return null
            }

            val request = GetToken(mobileNumber)
            val apiService = MyApi(context)
            val response = apiService.getAuthorization(NativeEndpoints.authorization(),request)

            if (response.isSuccessful) {
                val tokenResponse = response.body()
                if (tokenResponse?.Data?.Token != null) {
                    tokenResponse
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
*/
