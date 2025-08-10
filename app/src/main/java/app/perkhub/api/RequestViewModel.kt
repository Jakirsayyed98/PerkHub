package app.perkhub.api

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.Screens.Home.Model.Auth.VerifyOTP
import app.perkhub.Screens.Home.Model.Games.GamesRes
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubId
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubIdRes
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppByCategoryReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppSearchReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Screens.Home.Model.Notification.TokenReq
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.WithdrawalReq
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Screens.login.models.GetToken
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SaveUserDetail
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

    fun GetAuthorizationToken(getToken: GetToken, context: Context, listner: ApiListener<BaseRes>) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).getAuthorization(getToken).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun VerifyOTP(req: VerifyOtpReq,  context: Context,listner: ApiListener<VerifyOTP>) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).verifyOTP(req).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun SaveDetail(
        request : SaveUserDetail,
        context: Context,
        listner: ApiListener<LoginRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).SaveDetail(request).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getHomeData(
         context: Context, listner: ApiListener<HomeRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).getHomePage().body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getLootProduct(
        page: String, context: Context, listner: ApiListener<LootProductRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(coroutineContext) {
                MyApi(context).getLootProduct( page).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getMiniAppByCategory(
        req: MiniAppByCategoryReq, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).getMiniAppByCategory(req).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun searchminiApps(
        req: MiniAppSearchReq, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).searchminiApps(req).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun generateUrl(
        request : GenerateSubId, context: Context, listner: ApiListener<GenerateSubIdRes>
    ) {
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).generateUrl(request).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }

    fun updateNotification(
        token: String, context: Context, listner: ApiListener<MiniAppbyCategory>
    ) {
        val token = TokenReq(token)
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext) {
                MyApi(context).updateNotification( token).body()?.let { it ->
                    listner.onResponse(it)
                }
            }
        }
    }


    fun getAllGames(context: Context,listner: ApiListener<GamesRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getAllGames().body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getPopularGames(context: Context,listner: ApiListener<GamesRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getPopularGames().body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

    fun getAllTransaction(context: Context,listner: ApiListener<TransactionDataRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getAllTransaction().body()?.let {
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

    fun getWithdrawalTxn(context: Context,listner: ApiListener<withdrawalRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getWithdrawalTxn().body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }
 fun ReqForWithdrawal(req: WithdrawalReq, context: Context,listner: ApiListener<withdrawalRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).ReqForWithdrawal(req).body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }

fun getNotificationList( context: Context,listner: ApiListener<NotificationListRes>){
        viewModelScope.launch(SetErrorhandler()) {
            withContext(kotlin.coroutines.coroutineContext){
                MyApi(context).getNotificationList().body()?.let {
                    listner.onResponse(it)
                }
            }
        }
    }


}

