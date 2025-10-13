package app.perkhub.Screens.Games.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GamesTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromAssets(assets: Assets?): String? {
        return gson.toJson(assets)
    }

    @TypeConverter
    fun toAssets(json: String?): Assets? {
        return gson.fromJson(json, Assets::class.java)
    }

    @TypeConverter
    fun fromScreensList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toScreensList(json: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}
