package app.perkhub.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
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
        user_id: String, listner: ApiListener<HomeRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi().getHomePage(user_id).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getLootProduct(
        user_id: String, page: String, listner: ApiListener<LootProductRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi().getLootProduct(user_id, page).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getMiniAppByCategory(
        user_id: String, category_id: String, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi().getMiniAppByCategory(user_id, category_id).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun searchminiApps(
        user_id: String, miniapp_name: String, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi().searchminiApps(user_id, miniapp_name).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun generateUrl(
        user_id: String, miniapp_id: String, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi().generateUrl(user_id, miniapp_id).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun updateNotification(
        user_id: String, token: String, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi().updateNotification(user_id, token).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


}

