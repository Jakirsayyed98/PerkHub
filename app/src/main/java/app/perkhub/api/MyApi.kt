package app.perkhub.api

import android.content.Context
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

  @FormUrlEncoded
    @POST("savedetail")
    suspend fun SaveDetail(
        @Field("name")  name:String,
        @Field("number")  number:String,
        @Field("email")  email:String,
        @Field("gender")  gender:String,
        @Field("dob")  dob:String
    ):Response<LoginRes>

    //Home
    @FormUrlEncoded
    @POST("getHomePage")
    suspend fun getHomePage(
        @Field("user_id") user_id:String
    ):Response<HomeRes>

    //Loot Products
    @POST("getAllLootoffers")
    suspend fun getLootProduct(
        @Query("user_id") user_id:String,
        @Query("page") page:String
    ):Response<LootProductRes>

   //MiniAppByCategory
   @FormUrlEncoded
    @POST("getMiniAppByCategory")
    suspend fun getMiniAppByCategory(
        @Field("user_id") user_id:String,
        @Field("category_id") category_id:String
    ):Response<MiniAppbyCategory>
//searchMiniApps
@FormUrlEncoded
    @POST("searchMiniApps")
    suspend fun searchminiApps(
        @Field("user_id") user_id:String,
        @Field("miniapp_name") miniapp_name:String
    ):Response<MiniAppbyCategory>

    @FormUrlEncoded
    @POST("updateNotificationToken")
    suspend fun updateNotification(
        @Field("user_id") user_id:String,
        @Field("token") token:String
    ):Response<MiniAppbyCategory>

    @FormUrlEncoded
    @POST("genrateSubID")
    suspend fun generateUrl(
        @Field("user_id") user_id:String,
        @Field("miniapp_id") miniapp_id:String
    ):Response<MiniAppbyCategory>

    @FormUrlEncoded
    @POST("getAllGames")
    suspend fun getAllGames(
        @Field("user_id") user_id:String
    ):Response<GamesRes>

    @FormUrlEncoded
    @POST("getTxnList")
    suspend fun getAllTransaction(
        @Field("user_id") user_id:String
    ):Response<TransactionDataRes>



    @FormUrlEncoded
    @POST("requestwithdrawal")
    suspend fun ReqForWithdrawal(
        @Field("user_id") user_id:String,
        @Field("VPA_Id") VPA_Id:String,
        @Field("requested_withdrawal_amt") requested_withdrawal_amt:String
    ):Response<withdrawalRes>


    @FormUrlEncoded
    @POST("withdrawalTxnList")
    suspend fun getWithdrawalTxn(
        @Field("user_id") user_id:String
    ):Response<withdrawalRes>



    @FormUrlEncoded
    @POST("getNotificationList")
    suspend fun getNotificationList(
        @Field("user_id") user_id:String
    ):Response<NotificationListRes>



    companion object{
      operator  fun invoke(context : Context):MyApi{
            var BaseURl = "https://66b5-103-235-0-32.ngrok-free.app/api/"
            val httploggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .addInterceptor(httploggingInterceptor).build()

            val retrofit = Retrofit.Builder().baseUrl(BaseURl).addConverterFactory(GsonConverterFactory.create()).client(client).build()

            return retrofit.create(MyApi::class.java)
        }
    }
}