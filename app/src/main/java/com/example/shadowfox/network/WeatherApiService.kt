package com.example.shadowfox.network

import com.example.shadowfox.data.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // Or "imperial" for Fahrenheit
    ): Response<WeatherResponse>

    // TODO: Add endpoint for forecast if needed later
    // Example:
    // @GET("forecast/daily")
    // suspend fun getDailyForecast(
    //     @Query("q") location: String,
    //     @Query("appid") apiKey: String,
    //     @Query("cnt") dayCount: Int = 7, // Number of days
    //     @Query("units") units: String = "metric"
    // ): Response<ForecastResponse> // Assuming you have a ForecastResponse data class
}