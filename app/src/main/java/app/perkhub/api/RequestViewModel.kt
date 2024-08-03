package app.perkhub.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.login.models.LoginRes
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext


class RequestViewModel : ViewModel() {

    fun SendOTP(number: String, listner: ApiListener<LoginRes>) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi().sendOtp(number).body()?.let { it ->
                    println()
                    println(it)
                    println()
                    listner.onResponse(it)
                }
            }
        }
    }

    fun VerifyOTP(number: String, OTP: String, listner: ApiListener<LoginRes>) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi().verifyOTP(number, OTP).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun SaveDetail(
        name: String,
        number: String,
        email: String,
        dob: String,
        gender: String,
        listner: ApiListener<LoginRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi().SaveDetail(name, number, email, gender, dob).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getHomeData(
        user_id:String,listner: ApiListener<HomeRes>
    ){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext){
                MyApi().getHomePage(user_id).body()?.let {it->
                    listner.onResponse(it)
                }
            }
        }
    }

}