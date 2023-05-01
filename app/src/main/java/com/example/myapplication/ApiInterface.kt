package com.example.myapplication

import com.example.myapplication.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface  ApiInterface {

    @Headers("Content-Type: application/json")
    @GET("data/2.5/find")
    fun getData(
        @Query("q") page: String,
        @Query("lang") lang: String,
        @Query("mode") mode: String,
        @Query("appid") appid: String):  Call<WeatherData>


}