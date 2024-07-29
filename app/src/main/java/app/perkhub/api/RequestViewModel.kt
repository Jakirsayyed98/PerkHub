package app.perkhub.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.login.models.LoginRes
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext


class RequestViewModel : ViewModel() {

    fun SendOTP(number:String,listner : ApiListener<LoginRes>){
        viewModelScope.launch(SetErrorhandler()){
            withContext(coroutineContext){
                MyApi().sendOtp(number).body()?.let {it->
                        println()
                        println(it)
                    println()
                        listner.onResponse(it)
                }
            }
        }
    }

    fun VerifyOTP(number:String,OTP:String,listner : ApiListener<LoginRes>){
        viewModelScope.launch(SetErrorhandler()){
            withContext(coroutineContext){
                MyApi().verifyOTP(number,OTP).body()?.let {it->
                    listner.onResponse(it)
                }
            }
        }
    }


}