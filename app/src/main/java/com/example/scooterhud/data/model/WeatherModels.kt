data class CurrentWeatherResponse(
    val main: MainData,
    val weather: List<WeatherCondition>,
    val name: String
)

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: MainData,
    val weather: List<WeatherCondition>
)

data class MainData(
    val temp: Double
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
