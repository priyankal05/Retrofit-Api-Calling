package com.example.myapplication

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

object RetrofitHelper {


  val baseUrl = "https://api"


    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    private val httpLoggingInterceptor = HttpLoggingInterceptor()

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    fun getInstance(): Retrofit {

        httpClient.addInterceptor(httpLoggingInterceptor)
        val client: OkHttpClient = httpClient.build()

        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

}

/*
object RetrofitHelperAuth {

    val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    val httpLoggingInterceptor = HttpLoggingInterceptor()

    val gson = GsonBuilder()
        .setLenient()
        .create()

    fun getInstance(auth_token: String?): Retrofit {

//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request: Request = chain.request().newBuilder().addHeader("authorization", auth_token.toString()).build()
                return chain.proceed(request)
            }
        })
        httpClient.addInterceptor(httpLoggingInterceptor)
        val client: OkHttpClient = httpClient.build()

        return Retrofit.Builder().baseUrl("BaseUrl")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
}*/
