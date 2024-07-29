package app.perkhub.api

import app.perkhub.login.models.LoginRes
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

interface MyApi {

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