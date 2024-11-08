package app.perkhub.api

import android.content.Context
import app.perkhub.api.tokens.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Get the token from a token provider (like SharedPreferences, etc.)
        val token =TokenManager.getToken(context)

        // Add token to the request headers
        request = request.newBuilder()
            .addHeader("Authorization", token.toString())
            .build()

        // Proceed with the request
        var response = chain.proceed(request)

        // If token expired, attempt to refresh it
        if (response.code == 401) {
            // You may need to implement the logic for refreshing the token
            val newToken = refreshToken()

            // Retry the original request with the new token
            request = request.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", newToken)
                .build()
            response = chain.proceed(request)
        }

        return response
    }

    // Example method to refresh the token
    private fun refreshToken(): String {
        // Implement your logic to refresh the token
        return "new-refreshed-token"
    }
}
