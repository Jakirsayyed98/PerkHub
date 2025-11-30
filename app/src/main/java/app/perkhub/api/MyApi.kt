package app.perkhub.api

import android.content.Context
import app.perkhub.Screens.Games.model.GamesCategoryRes
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
import app.perkhub.Screens.Offers.model.Offerlistres
import app.perkhub.Screens.Offers.model.SearchOfferReq
import app.perkhub.Screens.Profile.model.CreateNewTicketRequest
import app.perkhub.Screens.Profile.model.CreateNewTicketResponse
import app.perkhub.Screens.Profile.model.RequestTicketMessage
import app.perkhub.Screens.Profile.model.TicketMessageRes
import app.perkhub.Screens.Profile.model.TicketMessageResponse
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface MyApi {

        // ----------------- LOGIN APIs -----------------
        @POST
        suspend fun sendOtp(
            @Url url: String,
            @Body body: SendOtpReq
        ): Response<BaseRes>

        @POST
        suspend fun verifyOtp(
            @Url url: String,
            @Body body: VerifyOtpReq
        ): Response<VerifyOTP>

        @POST
        suspend fun getAuthorization(
            @Url url: String,
            @Body body: GetToken
        ): Response<BaseRes>

        @GET
        suspend fun getUserDetail(
            @Url url: String = NativeEndpoints.getUserDetail()
        ): Response<LoginRes>

        @POST
        suspend fun saveDetail(
            @Url url: String,
            @Body body: SaveUserDetail
        ): Response<BaseRes>

        // ----------------- HOME -----------------
        @GET
        suspend fun getHomePage(
            @Url url: String = NativeEndpoints.getHomePage()
        ): Response<HomeRes>

        // ----------------- MINI APPS -----------------
        @POST
        suspend fun getMiniAppByCategory(
            @Url url: String,
            @Body body: MiniAppByCategoryReq
        ): Response<MiniAppbyCategory>

        @POST
        suspend fun generateUrl(
            @Url url: String,
            @Body body: GenerateSubId
        ): Response<GenerateSubIdRes>

        @POST
        suspend fun searchMiniApps(
            @Url url: String,
            @Body body: MiniAppSearchReq
        ): Response<MiniAppbyCategory>

        @POST
        suspend fun updateNotification(
            @Url url: String,
            @Body body: TokenReq
        ): Response<BaseRes>

        // ----------------- LOOT PRODUCTS -----------------
        @POST
        suspend fun getLootProduct(
            @Url url: String,
            @Query("page") page: String
        ): Response<LootProductRes>

        // ----------------- GAMES -----------------
        @GET
        suspend fun getAllGames(
            @Url url: String = NativeEndpoints.getAllGames()
        ): Response<app.perkhub.Screens.Games.model.GamesRes>

        @GET
        suspend fun getAllGamesCategory(
            @Url url: String = NativeEndpoints.getAllGamesCategory()
        ): Response<GamesCategoryRes>

        @GET
        suspend fun getPopularGames(
            @Url url: String = NativeEndpoints.getPopularGames()
        ): Response<app.perkhub.Screens.Games.model.GamesRes>

        // ----------------- TRANSACTIONS -----------------
        @GET
        suspend fun getAllTransactions(
            @Url url: String,
        ): Response<TransactionDataRes>

        // ----------------- WITHDRAWALS -----------------
        @POST
        suspend fun requestWithdrawal(
            @Url url: String,
            @Body body: WithdrawalReq
        ): Response<RequestWithdrawalRes>

        @GET
        suspend fun getWithdrawalTxn(
            @Url url: String = NativeEndpoints.withdrawalTransactions()
        ): Response<withdrawalRes>

        @GET
        suspend fun getPaymentMethods(
            @Url url: String = NativeEndpoints.withdrawalGetPaymentMethod()
        ): Response<getPaymentMethodsRes>

        @POST
        suspend fun addPaymentMethod(
            @Url url: String,
            @Body body: addPaymentMethodReq
        ): Response<addPaymentMethodRes>

        @POST
        suspend fun requestWithdrawalPayment(
            @Url url: String = NativeEndpoints.withdrawalRequest(),
            @Body body: RequestWithdrawalReq
        ): Response<RequestWithdrawalRes>

        // ----------------- NOTIFICATIONS -----------------
        @GET
        suspend fun getNotificationList(
            @Url url: String = NativeEndpoints.getNotificationHistory()
        ): Response<NotificationListRes>

        // ----------------- SUPPORT / TICKETS -----------------
        @GET
        suspend fun getAllTickets(
            @Url url: String = NativeEndpoints.ticketsByUser()
        ): Response<TicketsRes>

        @POST
        suspend fun createTicket(
            @Url url: String,
            @Body body: CreateNewTicketRequest
        ): Response<CreateNewTicketResponse>

        @GET
        suspend fun getTicketMessages(
            @Url url: String
        ): Response<TicketMessageRes>

        @POST
        suspend fun sendTicketMessage(
            @Url url: String,
            @Body body: RequestTicketMessage
        ): Response<TicketMessageResponse>

        // ----------------- OFFERS -----------------
        @POST
        suspend fun searchOfferByStoreName(
            @Url url: String,
            @Body body: SearchOfferReq
        ): Response<Offerlistres>

        @GET
        suspend fun getOffersHomePage(
            @Url url: String = NativeEndpoints.offersHomepage()
        ): Response<Offerlistres>



    companion object{
      external fun BaseURL(): String?
      operator  fun invoke(context : Context):MyApi{
          System.loadLibrary("perkhub")
//          val BaseUrl = "https://api.perkhub.in/api/"
//          val BaseUrl = "https://blessed-pretty-mammal.ngrok-free.app/api/"
            var BaseURl =BaseURL()// "$BaseUrl"
            val httploggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(context))
                .addInterceptor(httploggingInterceptor).build()

            val retrofit = Retrofit.Builder().baseUrl(BaseURl).addConverterFactory(GsonConverterFactory.create()).client(client).build()

            return retrofit.create(MyApi::class.java)
        }
    }
}