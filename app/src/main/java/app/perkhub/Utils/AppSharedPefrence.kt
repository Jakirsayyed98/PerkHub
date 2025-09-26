package app.perkhub.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import app.perkhub.Screens.login.models.LoginRes
import com.google.gson.Gson

class AppSharedPefrence {
    companion object{
        lateinit var insatance : AppSharedPefrence
        lateinit var prefrence : SharedPreferences

        fun getInstance(context:Context):AppSharedPefrence{
            if (this::insatance.isInitialized.not()){
                    insatance = AppSharedPefrence()
            }

            if (this::prefrence.isInitialized.not()){
                prefrence = context.getSharedPreferences("PerkHub",MODE_PRIVATE)
            }

            return insatance

        }
    }

    fun saveString(key:String,value:String){
        prefrence.edit().putString(key,value).apply()
    }

    fun getString(key: String):String?{
        return  prefrence.getString(key,null)
    }

    fun saveBool(key: String,value:Boolean){
        prefrence.edit().putBoolean(key, value).apply()
    }
    fun getBoolen(key: String):Boolean?{
        return  prefrence.getBoolean(key,false)
    }

    fun saveUser(USERDATA :String,data: LoginRes){
        var user = Gson().toJson(data)
        prefrence.edit().putString(USERDATA,user).apply()
    }

    fun getUser(USERDATA :String): LoginRes? {
        val response= prefrence.getString(USERDATA,null)
        return Gson().fromJson<LoginRes?>(response,LoginRes::class.java)
    }

}