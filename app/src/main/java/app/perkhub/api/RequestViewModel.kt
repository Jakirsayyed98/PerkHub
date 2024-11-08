package app.perkhub.api

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.Screens.Home.Model.Games.GamesRes
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SendOtpReq
import app.perkhub.Screens.login.models.VerifyOtpReq
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext


class RequestViewModel : ViewModel() {

    fun SendOTP(sendOTP: SendOtpReq, context: Context, listner: ApiListener<BaseRes>) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).sendOtp(sendOTP).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun VerifyOTP(req: VerifyOtpReq,  context: Context,listner: ApiListener<BaseRes>) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).verifyOTP(req).body()?.let { it ->
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
        context: Context,
        listner: ApiListener<LoginRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).SaveDetail(name, number, email, gender, dob).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getHomeData(
        user_id: String, context: Context, listner: ApiListener<HomeRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).getHomePage(user_id).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getLootProduct(
        user_id: String, page: String, context: Context, listner: ApiListener<LootProductRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).getLootProduct(user_id, page).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getMiniAppByCategory(
        user_id: String, category_id: String, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).getMiniAppByCategory(user_id, category_id).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun searchminiApps(
        user_id: String, miniapp_name: String, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).searchminiApps(user_id, miniapp_name).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun generateUrl(
        user_id: String, miniapp_id: String, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).generateUrl(user_id, miniapp_id).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun updateNotification(
        user_id: String, token: String, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).updateNotification(user_id, token).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getAllGames(user_id: String, context: Context,listner: ApiListener<GamesRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getAllGames(user_id).body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getAllTransaction(user_id: String, context: Context,listner: ApiListener<TransactionDataRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getAllTransaction(user_id).body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getUserDetail( context: Context,listner: ApiListener<LoginRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getUserDetail().body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getWithdrawalTxn(user_id: String, context: Context,listner: ApiListener<withdrawalRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getWithdrawalTxn(user_id).body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }
 fun ReqForWithdrawal(user_id: String,VPA_Id: String,requested_withdrawal_amt: String, context: Context,listner: ApiListener<withdrawalRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).ReqForWithdrawal(user_id,VPA_Id,requested_withdrawal_amt).body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

fun getNotificationList(user_id: String, context: Context,listner: ApiListener<NotificationListRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getNotificationList(user_id).body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }


}

