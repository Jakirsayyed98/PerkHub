package app.perkhub.api

import android.content.Context
import app.perkhub.Screens.Home.Model.Games.GamesRes
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubId
import app.perkhub.Screens.Home.Model.GenerateUrl.GenerateSubIdRes
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppByCategoryReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppSearchReq
import app.perkhub.Screens.Home.Model.MiniAppByCategory.MiniAppbyCategory
import app.perkhub.Screens.Home.Model.Notification.NotificationListRes
import app.perkhub.Screens.Home.Model.TransactionModel.TransactionDataRes
import app.perkhub.Screens.Home.Model.Withdrawal.WithdrawalReq
import app.perkhub.Screens.Home.Model.Withdrawal.withdrawalRes
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

interface MyApi {

    //Login APIs


    @POST("sendOTP")
    suspend fun sendOtp(
        @Body body: SendOtpReq
    ):Response<BaseRes>

    @POST("verifyOTP")
    suspend fun verifyOTP(
        @Body body: VerifyOtpReq
    ):Response<BaseRes>


    @GET("getUserDetail")
    suspend fun getUserDetail(
    ):Response<LoginRes>


    @POST("savedetail")
    suspend fun SaveDetail(
        @Body body: SaveUserDetail
    ):Response<LoginRes>

    //Home
    @GET("getHomePage")
    suspend fun getHomePage(
    ):Response<HomeRes>

    //Loot Products
    @POST("getAllLootoffers")
    suspend fun getLootProduct(
        @Query("page") page:String
    ):Response<LootProductRes>

   //MiniAppByCategory
    @POST("getMiniAppByCategory")
    suspend fun getMiniAppByCategory(
       @Body body:MiniAppByCategoryReq
    ):Response<MiniAppbyCategory>
//searchMiniApps
    @POST("searchMiniApps")
    suspend fun searchminiApps(
    @Body body: MiniAppSearchReq
    ):Response<MiniAppbyCategory>

    @POST("updateNotificationToken")
    suspend fun updateNotification(
        @Field("token") token:String
    ):Response<MiniAppbyCategory>

    @POST("genrateSubID")
    suspend fun generateUrl(
        @Body body: GenerateSubId
    ):Response<GenerateSubIdRes>


    @GET("getAllGames")
    suspend fun getAllGames(
    ):Response<GamesRes>

    @GET("getTxnList")
    suspend fun getAllTransaction(
    ):Response<TransactionDataRes>

    @POST("withdrawal/request")
    suspend fun ReqForWithdrawal(
        @Body body: WithdrawalReq
    ):Response<withdrawalRes>


    @GET("withdrawal/txnList")
    suspend fun getWithdrawalTxn(
    ):Response<withdrawalRes>



    @FormUrlEncoded
    @POST("getNotificationList")
    suspend fun getNotificationList(
        @Field("user_id") user_id:String
    ):Response<NotificationListRes>



    companion object{
      operator  fun invoke(context : Context):MyApi{
          val BaseUrl = "https://blessed-pretty-mammal.ngrok-free.app"
            var BaseURl = "$BaseUrl/api/"
            val httploggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .addInterceptor(httploggingInterceptor).build()

            val retrofit = Retrofit.Builder().baseUrl(BaseURl).addConverterFactory(GsonConverterFactory.create()).client(client).build()

            return retrofit.create(MyApi::class.java)
        }
    }
}