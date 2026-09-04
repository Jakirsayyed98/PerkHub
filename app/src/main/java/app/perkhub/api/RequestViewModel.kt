package app.perkhub.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.Screens.Games.model.GamesCategoryRes
import app.perkhub.Screens.Games.model.GamesRes
import app.perkhub.Screens.Home.Model.Auth.VerifyOTP
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
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
import app.perkhub.Screens.Offers.model.Offerlistres
import app.perkhub.Screens.Offers.model.SearchOfferReq
import app.perkhub.Screens.Profile.model.CreateNewTicketRequest
import app.perkhub.Screens.Profile.model.CreateNewTicketResponse
import app.perkhub.Screens.Profile.model.DataTicket
import app.perkhub.Screens.Profile.model.RequestTicketMessage
import app.perkhub.Screens.Profile.model.TicketMessageRes
import app.perkhub.Screens.Profile.model.TicketMessageResponse
import app.perkhub.Screens.Profile.model.TicketsRes
import app.perkhub.Screens.Stores.model.StoresRes
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
import app.perkhub.Utils.getStoreCacheExpiry
import app.perkhub.room.Home.HomeRepository
import app.perkhub.room.Store.StoreRepository
import app.perkhub.room.notification.NotificationRepository
import app.perkhub.room.StoreTransaction.StoresTransactionsRepository
import app.perkhub.room.Tickets.TicketMessageRepository
import app.perkhub.room.Tickets.TicketsRepository
import app.perkhub.room.games.GamesCategoryRepository
import app.perkhub.room.games.GamesRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

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
        block: suspend () -> Response<T>,
        listener: ApiListener<T>
    ) {
        viewModelScope.launchSafe(onError = {
            listener.onError(it.message ?: "Something went wrong")
        }) {
            val response = block()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    listener.onSuccess(body, "success")
                } else {
                    listener.onError("Empty response")
                }
            } else {
                val errorMsg = try {
                    val json = response.errorBody()?.string()
                    val parsed = Gson().fromJson(json, ApiErrorRes::class.java)
                    parsed?.error?.message
                        ?: parsed?.message
                        ?: "Something went wrong"
                } catch (e: Exception) {
                    "Something went wrong"
                }

                listener.onError(errorMsg)
            }
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
        launchApi({ apiProvider(context).sendOtp(NativeEndpoints.sendOTP(),req) }, listener)
    }

    fun getAuthorizationToken(req: GetToken, context: Context, listener: ApiListener<BaseRes>) {
        launchApi({ apiProvider(context).getAuthorization(NativeEndpoints.authorization(),req) }, listener)
    }

    fun verifyOTP(req: VerifyOtpReq, context: Context, listener: ApiListener<VerifyOTP>) {
        launchApi({ apiProvider(context).verifyOtp(NativeEndpoints.verifyOTP(),req) }, listener)
    }

    fun saveDetail(req: SaveUserDetail, context: Context, listener: ApiListener<BaseRes>) {
        launchApi({ apiProvider(context).saveDetail(NativeEndpoints.saveDetail(),req) }, listener)
    }

    fun getLootProduct(page: String, context: Context, listener: ApiListener<LootProductRes>) {
        launchApi({ apiProvider(context).getLootProduct(NativeEndpoints.getAllLootoffers(),page) }, listener)
    }

    fun getMiniAppByCategory(req: MiniAppByCategoryReq, context: Context, listener: ApiListener<MiniAppbyCategory>) {
        val repository = StoreRepository(context)
        viewModelScope.launch {
            try {
                // Load cached popular games
                val cachedData = repository.getAllStoresByCategoryId(req.category_id)
                if (cachedData.isNotEmpty()) {

                    val cachedResponse = MiniAppbyCategory(
                            data = ArrayList(cachedData),
                            message = "Loaded from cache",
                            status = 200
                        )

                    listener.onSuccess(
                        cachedResponse,
                        "Loaded from cache"
                    )

                    return@launch
                }

                launchApi({ apiProvider(context).getMiniAppByCategory(NativeEndpoints.getStoresByCategory(),req) },
                    object : ApiListener<MiniAppbyCategory> {
                        override fun onSuccess(t: MiniAppbyCategory?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    // Return API response to listener
                                    listener.onSuccess(t, mess)
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            // If no cached data exists, propagate the error
                            if (cachedData.isEmpty()) {
                                listener.onError(mess)
                            }
                            // Otherwise, we already returned cached data, so ignore
                        }
                    }
                )

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }
//        launchApi({ apiProvider(context).getMiniAppByCategory(NativeEndpoints.getStoresByCategory(),req) }, listener)
    }

    fun searchMiniApps(
        req: MiniAppSearchReq,
        context: Context,
        listener: ApiListener<MiniAppbyCategory>
    ) {
        launchApi({ apiProvider(context).searchMiniApps( NativeEndpoints.searchMiniApps(),req) }, listener)
    }

    fun generateUrl(req: GenerateSubId, context: Context, listener: ApiListener<GenerateSubIdRes>) {
        launchApi({ apiProvider(context).generateUrl(NativeEndpoints.genrateSubID(),req) }, listener)
    }

    fun updateNotification(token: String, context: Context, listener: ApiListener<BaseRes>) {
        launchApi({ apiProvider(context).updateNotification(NativeEndpoints.updateNotificationToken(),TokenReq(token)) }, listener)
    }

    fun getPopularGames( context: Context,
                         forceRefresh: Boolean = false,
                         listener: ApiListener<GamesRes>
    ) {
        val repository = GamesRepository(context)

        viewModelScope.launch {
            try {
                // Load cached popular games
                val cachedData = repository.getAllPopularGames()
                val isCacheValid = repository.isPopularCacheValid(context)


                if (cachedData.isNotEmpty() && isCacheValid && !forceRefresh) {
                        val cachedResponse = GamesRes(
                            data = cachedData,
                            message = "Loaded from cache",
                            status = 200
                        )
                        listener.onSuccess(cachedResponse, "Loaded from cache")
                        return@launch
                }

                launchApi(
                    { apiProvider(context).getAllGames() },
                    object : ApiListener<GamesRes> {
                        override fun onSuccess(t: GamesRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    // Save fresh data into DB
                                    repository.saveGames(it)
                                    // Return API response to listener
                                    listener.onSuccess(t, mess)
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            // If no cached data exists, propagate the error
                            if (cachedData.isEmpty()) {
                                listener.onError(mess)
                            }
                            // Otherwise, we already returned cached data, so ignore
                        }
                    }
                )

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }

    }

//    fun getAllTransaction(context: Context, listener: ApiListener<TransactionDataRes>) {
//        launchApi({ apiProvider(context).getAllTransaction() }, listener)
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
                launchApi({ apiProvider(context).getAllTransactions(NativeEndpoints.getTxnList()) },
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
        launchApi({ apiProvider(context).getUserDetail() }, listener)
    }

    fun getWithdrawalTxn(context: Context, listener: ApiListener<withdrawalRes>) {
        launchApi({ apiProvider(context).getWithdrawalTxn() }, listener)
    }

//    fun reqForWithdrawal(req: WithdrawalReq, context: Context, listener: ApiListener<withdrawalRes>) {
//        launchApi({ apiProvider(context).requestWithdrawal(NativeEndpoints.withdrawalsRequest(),req) }, listener)
//    }

    fun AddPaymentMethod(req: addPaymentMethodReq, context: Context, listener: ApiListener<addPaymentMethodRes>) {
        launchApi({ apiProvider(context).addPaymentMethod(NativeEndpoints.withdrawalAddPaymentMethod(),req) }, listener)
    }

    fun GetPaymentMethod(context: Context, listener: ApiListener<getPaymentMethodsRes>) {
        launchApi({ apiProvider(context).getPaymentMethods() }, listener)
    }
  fun RequestWithdrawalPayment(
      req: RequestWithdrawalReq,
      context: Context, listener: ApiListener<RequestWithdrawalRes>
  ) {
        launchApi({ apiProvider(context).requestWithdrawalPayment(NativeEndpoints.withdrawalsRequest(),req) }, listener)
    }


    fun getAllTicketByMessage(
        ticketId: String,
        context: Context,
        forceRefresh: Boolean = false,
        listener: ApiListener<TicketMessageRes>
    )
    {
        val repository = TicketMessageRepository(context)

        viewModelScope.launch {
            try {
                // First, load from database
                val cachedData = repository.getMessagesOnce(ticketId)

                if (cachedData.isNotEmpty() && !forceRefresh) {
                    // Return cached data immediately
                    val message = DataTicket(ticket_messages = cachedData, id = ticketId, user_id = "", status = "", created_at = "", subject = "")
                    val cachedResponse = TicketMessageRes(
                        status = 200,
                        message = "success",
                        data = message,

                    )
                    listener.onSuccess(cachedResponse, "Loaded from cache")
                }

                // Then fetch from server in background
                val endPoint=NativeEndpoints.ticketsById()
                val url = "${endPoint}/$ticketId"
                launchApi({ apiProvider(context).getTicketMessages(url) },
                    object : ApiListener<TicketMessageRes> {
                        override fun onSuccess(t: TicketMessageRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    // Save to database
                                    repository.saveMessages(it.ticket_messages)

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


    fun createTicket(
        ticketMessage: CreateNewTicketRequest,
        context: Context, listener: ApiListener<CreateNewTicketResponse>
    ) {
        launchApi({ apiProvider(context).createTicket( NativeEndpoints.ticketsCreate(),ticketMessage) }, listener)
    }


    fun sendTicketMessage(
        ticketMessage: RequestTicketMessage,
        context: Context, listener: ApiListener<TicketMessageResponse>
    ) {
        launchApi({ apiProvider(context).sendTicketMessage(NativeEndpoints.ticketsSendMsg(),ticketMessage) }, listener)
    }

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
                launchApi({ apiProvider(context).getNotificationList() },
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
                launchApi({ apiProvider(context).getAllTickets() },
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


    fun getOffersListHomePage(
        context: Context, listener: ApiListener<Offerlistres>
    ) {
        launchApi({ apiProvider(context).getOffersHomePage() }, listener)
    }
    fun searchOffersByStore(
        request: SearchOfferReq,
        context: Context, listener: ApiListener<Offerlistres>
        ) {
            launchApi({ apiProvider(context).searchOfferByStoreName(NativeEndpoints.offersSearchByStore(),request) }, listener)
        }


    fun getGamesList(
        context: Context,
        forceRefresh: Boolean = false,
        listener: ApiListener<GamesRes>
    ) {
        val repository = GamesRepository(context)

        viewModelScope.launch {
            try {
                // Load cached data
                val cachedData = repository.getAllGames()

                if (cachedData.isNotEmpty() && !forceRefresh) {

                    val cachedResponse = GamesRes(
                        data = cachedData,
                        message = "Loaded from cache",
                        status = 200
                    )
                    listener.onSuccess(cachedResponse, "Loaded from cache")
                    return@launch
                }

                launchApi(
                    { apiProvider(context).getAllGames() },
                    object : ApiListener<GamesRes> {
                        override fun onSuccess(t: GamesRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    repository.saveGames(it) // Save fresh data
                                    listener.onSuccess(t, mess) // Return API data
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            listener.onError(mess)
                        }
                    }
                )

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }
    }

    fun getAllGamesCategory(
        context: Context,
        forceRefresh: Boolean = false,
        listener: ApiListener<GamesCategoryRes>
    ) {
        val repository = GamesCategoryRepository(context)

        viewModelScope.launch {
            try {
                // Load cached data
                val cachedData = repository.getAllGamesCategory()

                if (cachedData.isNotEmpty() && !forceRefresh) {

                    val cachedResponse = GamesCategoryRes(
                        data = cachedData,
                        message = "Loaded from cache",
                        status = 200
                    )
                    listener.onSuccess(cachedResponse, "Loaded from cache")
                    return@launch
                }

                launchApi(
                    { apiProvider(context).getAllGamesCategory() },
                    object : ApiListener<GamesCategoryRes> {
                        override fun onSuccess(t: GamesCategoryRes?, mess: String) {
                            viewModelScope.launch {
                                t?.data?.let {
                                    repository.saveGamesCategory(it) // Save fresh data
                                    listener.onSuccess(t, mess) // Return API data
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            listener.onError(mess)
                        }
                    }
                )

            } catch (e: Exception) {
                listener.onError(e.message ?: "Unknown error")
            }
        }
    }

    fun getAllStores(
        context: Context,
        listener: ApiListener<StoresRes>
    ) {

        val repository = StoreRepository(context)

        viewModelScope.launch {
            try {
                val cachedData = repository.getAllStores()

                val shouldFetch = repository.shouldFetchFromApi()
                if (cachedData.isNotEmpty() && !shouldFetch) {

                    val cachedResponse =
                        StoresRes(
                            data = cachedData,
                            message = "Loaded from cache",
                            status = 200
                        )

                    listener.onSuccess(
                        cachedResponse,
                        "Loaded from cache"
                    )

                    return@launch
                }

                // ==========================================
                // API REQUIRED
                // ==========================================

                launchApi({
                    apiProvider(context).getAllStores()
                    },
                    object : ApiListener<StoresRes> {
                        override fun onSuccess(t: StoresRes?, mess:String) {
                            viewModelScope.launch {
                                try {
                                    val freshData = t?.data ?: emptyList()
                                    if (freshData.isNotEmpty()) {
                                        repository.saveStores(freshData)
                                    }
                                    listener.onSuccess(t, mess)
                                } catch (e: Exception) {
                                    listener.onError(e.message ?: "Failed to save stores")
                                }
                            }
                        }

                        override fun onError(mess: String) {
                            if (cachedData.isNotEmpty()) {
                                listener.onSuccess(
                                    StoresRes(
                                        data = cachedData,
                                        message = "Loaded stale cache",
                                        status = 200
                                    ),
                                    "Loaded stale cache"
                                )

                            } else {
                                listener.onError(mess)
                            }
                        }
                    }
                )

            } catch (e: Exception) {
                listener.onError(
                    e.message ?: "Unknown error"
                )
            }
        }
    }
}
