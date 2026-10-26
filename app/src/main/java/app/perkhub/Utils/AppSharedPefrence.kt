
package app.perkhub.Utils

import android.content.Context
import android.content.SharedPreferences
import app.perkhub.Screens.login.models.LoginRes
import com.google.gson.Gson

class AppSharedPefrence private constructor(context: Context) {

    private val preference: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "PerkHub"

        @Volatile
        private var instance: AppSharedPefrence? = null

        fun getInstance(context: Context): AppSharedPefrence {
            return instance ?: synchronized(this) {
                instance ?: AppSharedPefrence(context).also {
                    instance = it
                }
            }
        }
    }

    // Save string
    fun saveString(key: String, value: String) {
        preference.edit()
            .putString(key, value)
            .apply()
    }

    // Get string
    fun getString(key: String): String? {
        return preference.getString(key, null)
    }

    // Save boolean
    fun saveBool(key: String, value: Boolean) {
        preference.edit()
            .putBoolean(key, value)
            .apply()
    }

    // Get boolean
    fun getBoolen(key: String): Boolean {
        return preference.getBoolean(key, false)
    }

    // Save user data
    fun saveUser(USERDATA: String, data: LoginRes) {
        preference.edit()
            .putString(USERDATA, gson.toJson(data))
            .apply()
    }

    // Get user data
    fun getUser(USERDATA: String): LoginRes? {
        val response = preference.getString(USERDATA, null)
            ?: return null

        return try {
            gson.fromJson(response, LoginRes::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Remove a specific key
    fun remove(key: String) {
        preference.edit()
            .remove(key)
            .apply()
    }

    // Clear all SharedPreferences data
    fun clearAll() {
        preference.edit()
            .clear()
            .apply()
    }
}
