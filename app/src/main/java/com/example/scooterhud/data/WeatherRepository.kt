package com.example.scooterhud.data

import com.example.scooterhud.data.model.WeatherInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    companion object {
        private const val API_KEY = "1a2bd67974b9a6025c9e245b42a4c530"
    }

    private val api: WeatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun getWeather(lat: Double, lon: Double): Pair<WeatherInfo, WeatherInfo>? {
        return try {
            val response = api.getWeather(lat = lat, lon = lon, apiKey = API_KEY)

            val current = WeatherInfo(
                tempCelsius = response.current.temp,
                description = response.current.weather.firstOrNull()?.description ?: "",
                iconCode = response.current.weather.firstOrNull()?.icon ?: ""
            )

            val inHour = response.hourly.getOrNull(1)?.let {
                WeatherInfo(
                    tempCelsius = it.temp,
                    description = it.weather.firstOrNull()?.description ?: "",
                    iconCode = it.weather.firstOrNull()?.icon ?: ""
                )
            } ?: current

            Pair(current, inHour)
        } catch (e: Exception) {
            null
        }
    }
}
