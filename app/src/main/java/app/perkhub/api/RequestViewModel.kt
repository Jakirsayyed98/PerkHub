package app.perkhub.api

import android.content.Context
import android.util.Log
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
import app.perkhub.Screens.Home.Model.TransactionModel.Datas
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.WithdrawalReq
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Screens.Profile.model.TicketsRes
import app.perkhub.Screens.login.models.GetToken
import app.perkhub.Screens.login.models.LoginRes
import app.perkhub.Screens.login.models.SaveUserDetail
import app.perkhub.Screens.login.models.SendOtpReq
import app.perkhub.Screens.login.models.VerifyOtpReq
import app.perkhub.Screens.withdrawal.model.RequestWithdrawalReq
import app.perkhub.Screens.withdrawal.model.RequestWithdrawalRes
import app.perkhub.Screens.withdrawal.model.addPaymentMethodReq
import app.perkhub.Screens.withdrawal.model.addPaymentMethodRes
import app.perkhub.Screens.withdrawal.model.getPaymentMethodsRes
import app.perkhub.room.HomeRepository
import app.perkhub.room.NotificationRepository
import app.perkhub.room.StoresTransactionsRepository
import app.perkhub.room.Tickets.TicketsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun CoroutineScope.launchSafe(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    onError: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) {
    val handler = CoroutineExceptionHandler { _, exception ->
        onError(exception)
    }
    this.launch(context = dispatcher + handler) {
        block()
    }
}

class RequestViewModel(
    private val repository: HomeRepository,
    private val apiProvider: (Context) -> MyApi
) : ViewModel() {

    private fun <T> launchApi(
        block: suspend () -> T?,
        listener: ApiListener<T>
    ) {
        viewModelScope.launchSafe(onError = {
            Log.e("RequestViewModel", "API call failed", it)
            listener.onError(it.message ?: "Unknown error")
        }) {
            val response = block()
            if (response != null) listener.onResponse(response)
            else listener.onError("Empty response")
        }
    }

    // --- Home API with caching ---
    fun getHomeData(listener: ApiListener<HomeRes>) {
        viewModelScope.launchSafe(onError = {
            listener.onError(it.message ?: "Unknown error")
        }) {
            val data = repository.getHomeData() // Repository handles cache + API
            listener.onResponse(data!!)
        }
    }


    // --- Other APIs ---
    fun sendOTP(req: SendOtpReq, context: Context, listener: ApiListener<BaseRes>) {
        launchApi({ apiProvider(context).sendOtp(req).body() }, listener)
    }

    fun getAuthorizationToken(req: GetToken, context: Context, listener: ApiListener<BaseRes>) {
        launchApi({ apiProvider(context).getAuthorization(req).body() }, listener)
    }

    fun verifyOTP(req: VerifyOtpReq, context: Context, listener: ApiListener<VerifyOTP>) {
        launchApi({ apiProvider(context).verifyOTP(req).body() }, listener)
    }

    fun saveDetail(req: SaveUserDetail, context: Context, listener: ApiListener<BaseRes>) {
        launchApi({ apiProvider(context).SaveDetail(req).body() }, listener)
    }

    fun getLootProduct(page: String, context: Context, listener: ApiListener<LootProductRes>) {
        launchApi({ apiProvider(context).getLootProduct(page).body() }, listener)
    }

    fun getMiniAppByCategory(
        req: MiniAppByCategoryReq,
        context: Context,
        listener: ApiListener<MiniAppbyCategory>
    ) {
        launchApi({ apiProvider(context).getMiniAppByCategory(req).body() }, listener)
    }

    fun searchMiniApps(
        req: MiniAppSearchReq,
        context: Context,
        listener: ApiListener<MiniAppbyCategory>
    ) {
        launchApi({ apiProvider(context).searchminiApps(req).body() }, listener)
    }

    fun generateUrl(req: GenerateSubId, context: Context, listener: ApiListener<GenerateSubIdRes>) {
        launchApi({ apiProvider(context).generateUrl(req).body() }, listener)
    }

    fun updateNotification(token: String, context: Context, listener: ApiListener<MiniAppbyCategory>) {
        launchApi({ apiProvider(context).updateNotification(TokenReq(token)).body() }, listener)
    }

    fun getAllGames(context: Context, listener: ApiListener<GamesRes>) {
        launchApi({ apiProvider(context).getAllGames().body() }, listener)
    }

    fun getPopularGames(context: Context, listener: ApiListener<GamesRes>) {
        launchApi({ apiProvider(context).getPopularGames().body() }, listener)
    }

//    fun getAllTransaction(context: Context, listener: ApiListener<TransactionDataRes>) {
//        launchApi({ apiProvider(context).getAllTransaction().body() }, listener)
//    }

    fun getAllTransaction(
        context: Context,
        forceRefresh: Boolean = false,
        listener: ApiListener<TransactionDataRes>
    ) {
        val repository = StoresTransactionsRepository(context)

        viewModelScope.launch {
            try {
                // First, load from database
                val cachedData = repository.getStoresTransactions()

                if (cachedData.isNotEmpty() && !forceRefresh) {
                    // Return cached data immediately
                    val cachedResponse = TransactionDataRes(
                        data = Datas(
                            data = ArrayList(cachedData), // Convert List to ArrayList
                            pending = "0",
                            rejected = "0",
                            verified = "0",
                            withdrawal = "0"
                        ),
                        message = "Loaded from cache",
                        status = 1
                    )
                    listener.onSuccess(cachedResponse, "Loaded from cache")
                }

                // Then fetch from server in background
                launchApi({ apiProvider(context).getAllTransaction().body() },
                    object : ApiListener<TransactionDataRes> {
                        override fun onSuccess(t: TransactionDataRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.data.let {
                                    // Save to database
                                    if (it != null) {
                                        repository.saveStoresTransactions(it)
                                    }
                                    // Notify listener with fresh data
                                    listener.onSuccess(t, mess)
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            // If no cached data, propagate error
                            if (cachedData.isEmpty()) {
                                listener.onError(mess)
                            }
                            // Otherwise, we already returned cached data
                        }
                    })

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }
    }

    fun getUserDetail(context: Context, listener: ApiListener<LoginRes>) {
        launchApi({ apiProvider(context).getUserDetail().body() }, listener)
    }

    fun getWithdrawalTxn(context: Context, listener: ApiListener<withdrawalRes>) {
        launchApi({ apiProvider(context).getWithdrawalTxn().body() }, listener)
    }

    fun reqForWithdrawal(req: WithdrawalReq, context: Context, listener: ApiListener<withdrawalRes>) {
        launchApi({ apiProvider(context).ReqForWithdrawal(req).body() }, listener)
    }

    fun AddPaymentMethod(req: addPaymentMethodReq, context: Context, listener: ApiListener<addPaymentMethodRes>) {
        launchApi({ apiProvider(context).addPaymentMethod(req).body() }, listener)
    }

    fun GetPaymentMethod(context: Context, listener: ApiListener<getPaymentMethodsRes>) {
        launchApi({ apiProvider(context).getPaymentMethods().body() }, listener)
    }
  fun RequestWithdrawalPayment(
      req: RequestWithdrawalReq,
      context: Context, listener: ApiListener<RequestWithdrawalRes>
  ) {
        launchApi({ apiProvider(context).RequestWithdrawalPayment(req).body() }, listener)
    }

//    fun getNotificationList(context: Context, listener: ApiListener<NotificationListRes>) {
//        launchApi({ apiProvider(context).getNotificationList().body() }, listener)
//    }

    fun getNotificationList(
        context: Context,
        forceRefresh: Boolean = false,
        listener: ApiListener<NotificationListRes>
    ) {
        val repository = NotificationRepository(context)

        viewModelScope.launch {
            try {
                // First, load from database
                val cachedData = repository.getNotifications()

                if (cachedData.isNotEmpty() && !forceRefresh) {
                    // Return cached data immediately
                    val cachedResponse = NotificationListRes(
                        data = cachedData,
                        errorCode = "0",
                        errorMsg = ""
                    )
                    listener.onSuccess(cachedResponse, "Loaded from cache")
                }

                // Then fetch from server in background
                launchApi({ apiProvider(context).getNotificationList().body() },
                    object : ApiListener<NotificationListRes> {
                        override fun onSuccess(t: NotificationListRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    // Save to database
                                    repository.saveNotifications(it)

                                    // Notify listener with fresh data
                                    listener.onSuccess(t, mess)
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            // If no cached data, propagate error
                            if (cachedData.isEmpty()) {
                                listener.onError(mess)
                            }
                            // Otherwise, we already returned cached data
                        }
                    })

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }
    }

    fun getAllTickets(
        context: Context,
        forceRefresh: Boolean = false,
        listener: ApiListener<TicketsRes>
    ) {
        val repository = TicketsRepository(context)

        viewModelScope.launch {
            try {
                // First, load from database
                val cachedData = repository.getAllTickets()

                if (cachedData.isNotEmpty() && !forceRefresh) {
                    // Return cached data immediately
                    val cachedResponse = TicketsRes(
                        data = cachedData,
                        status = 200,
                        message = "success"
                    )
                    listener.onSuccess(cachedResponse, "Loaded from cache")
                }

                // Then fetch from server in background
                launchApi({ apiProvider(context).getAllTickets().body() },
                    object : ApiListener<TicketsRes> {
                        override fun onSuccess(t: TicketsRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    // Save to database
                                    repository.saveTickets(it)

                                    // Notify listener with fresh data
                                    listener.onSuccess(t, mess)
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            // If no cached data, propagate error
                            if (cachedData.isEmpty()) {
                                listener.onError(mess)
                            }
                            // Otherwise, we already returned cached data
                        }
                    })

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }
    }

}
