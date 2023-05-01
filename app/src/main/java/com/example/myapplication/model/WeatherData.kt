package com.example.myapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


    data class WeatherData(

        @SerializedName("message")
        @Expose
        var message: String? = null,

        @SerializedName("cod")
        @Expose
        var cod: String? = null,

        @SerializedName("count")
        @Expose
        var count: Int? = null,

        @SerializedName("list")
        @Expose
        var list: ArrayList<List> = arrayListOf()

    )

