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
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the current token
        var token = TokenManager.getToken(context)
        Log.d("AuthInterceptor", "Current token: $token")

        // Create request with authorization header
        val requestWithAuth = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Execute the request
        var response = chain.proceed(requestWithAuth)

        // If we get 401, attempt token refresh
        if (response.code == 401) {
            Log.d("AuthInterceptor", "Received 401, attempting token refresh")

            response.close() // Close the original response

            // Use runBlocking to handle token refresh synchronously
            return runBlocking {
                mutex.withLock {
                    // Check if token was already refreshed by another request
                    val currentToken = TokenManager.getToken(context)
                    if (currentToken != token && !isRefreshing) {
                        // Token was already refreshed, retry with new token
                        Log.d("AuthInterceptor", "Token already refreshed, retrying request")
                        return@runBlocking retryRequestWithNewToken(chain, originalRequest, currentToken)
                    }

                    // Refresh the token
                    if (!isRefreshing) {
                        isRefreshing = true
                        val newToken = refreshAuthTokenAsync()
                        isRefreshing = false

                        if (newToken != null) {
                            Log.d("AuthInterceptor", "Token refresh successful")
                            // Save the new token
                            TokenManager.saveToken(context, newToken.Data.Token)

                            // Retry the original request with new token
                            return@runBlocking retryRequestWithNewToken(chain, originalRequest, newToken.Data.Token)
                        } else {
                            Log.e("AuthInterceptor", "Token refresh failed")
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
                Log.e("AuthInterceptor", "Mobile number not found for token refresh")
                return null
            }

            val request = GetToken(mobileNumber)
            val apiService = MyApi(context)
            val response = apiService.getAuthorization(request)

            if (response.isSuccessful) {
                val tokenResponse = response.body()
                if (tokenResponse?.Data?.Token != null) {
                    Log.d("AuthInterceptor", "Token refreshed successfully")
                    tokenResponse
                } else {
                    Log.e("AuthInterceptor", "Token response body is null or invalid")
                    null
                }
            } else {
                Log.e("AuthInterceptor", "Token refresh failed with code: ${response.code()}, message: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Error refreshing token: ${e.message}", e)
            null
        }
    }
}

// Remove the custom response models since we're using BaseRes
// Make sure your BaseRes has the proper structure for token access

//package app.perkhub.api
//
//import android.content.Context
//import android.util.Log
//import app.perkhub.Screens.login.models.GetToken
//import app.perkhub.api.tokens.TokenManager
//import okhttp3.Interceptor
//import okhttp3.Response
//import kotlinx.coroutines.*
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//
//class AuthInterceptor(
//    private val context: Context
//) : Interceptor {
//
//    private val mutex = Mutex() // Mutex to ensure we handle token refresh synchronously across requests
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        var request = chain.request()
//
//        // Get the token from TokenManager (SharedPreferences, etc.)
//        var token = TokenManager.getToken(context)
//        print("\n\n"+token+"\n\n")
//        // Add token to the request headers
//        request = request.newBuilder()
//            .addHeader("Authorization", "Bearer $token") // Ensure 'Bearer' prefix is correct
//            .build()
//
//        // Proceed with the request
//        var response = chain.proceed(request)
//
//        // If the token is expired (401 Unauthorized), attempt to refresh it
//        if (response.code == 401) {
//            // Synchronize token refresh to ensure only one token refresh happens at a time
//            runBlocking {
//                // Acquire the mutex lock to perform a token refresh in a coroutine
//                mutex.withLock {
//                    val newToken = refreshAuthTokenAsync()
//
//                    // If token refresh is successful, retry the request with the new token
//                    if (newToken != null) {
//                        // Save the new token (e.g., in TokenManager)
//                        TokenManager.saveToken(context, newToken.Data.Token)
//
//                        // Retry the original request with the new token
//                        request = request.newBuilder()
//                            .removeHeader("Authorization")
//                            .addHeader("Authorization", "Bearer ${newToken.Data}")
//                            .build()
//
//                        response = chain.proceed(request)
//                    }
//                }
//            }
//        }
//
//        return response
//    }
//
//    // This is a suspend function that we call asynchronously to refresh the token
//    private suspend fun refreshAuthTokenAsync(): BaseRes? {
//        return try {
//            val request = GetToken(TokenManager.getMobileNumberForToken(context).toString())
//            val response = MyApi(context).getAuthorization(request)
//            if (response.isSuccessful) {
//                response.body()
//
//            } else {
//                Log.e("AuthInterceptor", "Token refresh failed: ${response.message()}")
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("AuthInterceptor", "Error refreshing token: ${e.message}")
//            null
//        }
//    }
//}
