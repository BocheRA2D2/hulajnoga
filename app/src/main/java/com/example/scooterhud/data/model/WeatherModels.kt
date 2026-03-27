package com.example.scooterhud.data.model

data class OneCallResponse(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>
)

data class CurrentWeather(
    val temp: Double,
    val weather: List<WeatherCondition>
)

data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    val weather: List<WeatherCondition>
)

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WeatherInfo(
    val tempCelsius: Double,
    val description: String,
    val iconCode: String
) {
    val emoji: String
        get() = when {
            iconCode.startsWith("01") -> "☀️"
            iconCode.startsWith("02") -> "🌤️"
            iconCode.startsWith("03") -> "☁️"
            iconCode.startsWith("04") -> "☁️"
            iconCode.startsWith("09") -> "🌧️"
            iconCode.startsWith("10") -> "🌦️"
            iconCode.startsWith("11") -> "⛈️"
            iconCode.startsWith("13") -> "❄️"
            iconCode.startsWith("50") -> "🌫️"
            else -> "🌡️"
        }

    val tempFormatted: String get() = "${tempCelsius.toInt()}°C"
}
