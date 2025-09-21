package app.perkhub.api

import android.content.Context
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
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MyApi {

    //Login APIs Start
    @POST("sendOTP")// used
    suspend fun sendOtp(
        @Body body: SendOtpReq
    ):Response<BaseRes>

    @POST("verifyOTP") //used
    suspend fun verifyOTP(
        @Body body: VerifyOtpReq
    ):Response<VerifyOTP>

    @POST("authorization")
    suspend fun getAuthorization(
        @Body body: GetToken
    ):Response<BaseRes>

    @GET("getUserDetail")
    suspend fun getUserDetail(
    ):Response<LoginRes>


    @POST("savedetail")//used
    suspend fun SaveDetail(
        @Body body: SaveUserDetail
    ):Response<BaseRes>
    //Login APIs End



    //Home
    @GET("getHomePage")
    suspend fun getHomePage(
    ):Response<HomeRes>

    //MiniAppByCategory
    @POST("getStoresByCategory")
    suspend fun getMiniAppByCategory(
        @Body body:MiniAppByCategoryReq
    ):Response<MiniAppbyCategory>

    @POST("genrateSubID")
    suspend fun generateUrl(
        @Body body: GenerateSubId
    ):Response<GenerateSubIdRes>

    //Loot Products
    @POST("getAllLootoffers")
    suspend fun getLootProduct(
        @Query("page") page:String
    ):Response<LootProductRes>


//searchMiniApps
    @POST("searchMiniApps")
    suspend fun searchminiApps(
    @Body body: MiniAppSearchReq
    ):Response<MiniAppbyCategory>

    @POST("updateNotificationToken")
    suspend fun updateNotification(
        @Body body: TokenReq
    ):Response<MiniAppbyCategory>




    @GET("getAllGames")
    suspend fun getAllGames(
    ):Response<GamesRes>


    @GET("get-popular-games")
    suspend fun getPopularGames(
    ):Response<GamesRes>

    @GET("miniapp/transactions")
    suspend fun getAllTransaction(
    ):Response<TransactionDataRes>

    @POST("withdrawals/request")
    suspend fun ReqForWithdrawal(
        @Body body: WithdrawalReq
    ):Response<withdrawalRes>


    @GET("withdrawals/transactions")
    suspend fun getWithdrawalTxn(
    ):Response<withdrawalRes>


    @GET("getNotificationList")
    suspend fun getNotificationList(
    ):Response<NotificationListRes>



    companion object{
      operator  fun invoke(context : Context):MyApi{
//          val BaseUrl = "https://api.perkhub.in/api/"
          val BaseUrl = "https://blessed-pretty-mammal.ngrok-free.app/api/"
            var BaseURl = "$BaseUrl"
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