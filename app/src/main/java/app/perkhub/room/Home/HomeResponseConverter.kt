package app.perkhub.room.Home

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import app.perkhub.Screens.Home.Model.Home.HomeRes

class HomeResponseConverter {
    @TypeConverter
    fun fromHomeRes(homeRes: HomeRes): String = Gson().toJson(homeRes)

    @TypeConverter
    fun toHomeRes(data: String): HomeRes =
        Gson().fromJson(data, object : TypeToken<HomeRes>() {}.type)
}
