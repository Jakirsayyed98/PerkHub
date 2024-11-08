package app.perkhub.api.tokens

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREF_NAME = "app_preferences"
    private const val TOKEN_KEY = "auth_token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save the token
    fun saveToken(context: Context, token: String) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    // Retrieve the token
    fun getToken(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return  "Bearer " + sharedPreferences.getString(TOKEN_KEY, null)
    }

    // Remove token (e.g., for logout)
    fun removeToken(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }
}
