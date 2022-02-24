package com.karimmammadov.weatherappp.service

import com.karimmammadov.weatherappp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    //http://api.openweathermap.org/data/2.5/weather?q=baku&APPID=ddac845f86e065e9a75d63a220c1c211

    @GET("data/2.5/weather?&units=metric&APPID=ddac845f86e065e9a75d63a220c1c211")
    fun getData(
        @Query("q")cityName:String
    ): Single<WeatherModel>
}