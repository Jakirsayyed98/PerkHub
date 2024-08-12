package app.perkhub.api

import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.Screens.Home.Model.LootProduct.LootProductRes
import app.perkhub.Screens.login.models.LoginRes
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyApi {

    //Login APIs

    @FormUrlEncoded
    @POST("sendOTP")
    suspend fun sendOtp(
      @Field("number")  number:String
    ):Response<LoginRes>

    @FormUrlEncoded
    @POST("verifyOTP")
    suspend fun verifyOTP(
        @Field("number")  number:String,
        @Field("otp")  otp:String
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

    companion object{
      operator  fun invoke():MyApi{
            var BaseURl = "http://perkhub.in/perkAdmin/api/"
            val httploggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().addInterceptor(httploggingInterceptor).build()

            val retrofit = Retrofit.Builder().baseUrl(BaseURl).addConverterFactory(GsonConverterFactory.create()).client(client).build()

            return retrofit.create(MyApi::class.java)
        }
    }
}