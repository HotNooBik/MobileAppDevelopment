package com.pw2.stolbokapp.data.calendar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import kotlin.math.min
import kotlin.math.roundToInt

class WeatherForecastCalendarService : CalendarDaysService {

    companion object {
        private const val STOLBY_LAT = 55.917827440941885
        private const val STOLBY_LNG = 92.7300190228174
        private const val FORECAST_DAYS = 16
    }

    override suspend fun getCalendarDays(): List<CalendarDay> = withContext(Dispatchers.IO) {
        val forecast = fetchForecast()
        forecast.map { dayForecast ->
            val season = CalendarRepository.getSeason(dayForecast.date)
            val weather = mapWeatherCode(dayForecast.weatherCode)
            val temperature = dayForecast.meanTemperature.roundToInt()
            val tickActivity = CalendarRepository.getTickActivity(dayForecast.date, temperature, weather)
            val attendance = CalendarRepository.getAttendance(dayForecast.date)
            val stolbyStatus = CalendarRepository.getStolbyStatus(dayForecast.date)
            val status = CalendarDayStatusCalculator.calculateStatus(
                temperature = temperature,
                weather = weather,
                tickActivity = tickActivity,
                attendance = attendance,
                stolbyStatus = stolbyStatus,
                season = season
            )

            CalendarDay(
                dayOfWeek = CalendarTextFormatter.shortDayOfWeek(dayForecast.date),
                month = CalendarTextFormatter.monthNameGenitive(dayForecast.date.monthValue),
                monthIndex = dayForecast.date.monthValue,
                dayNumber = dayForecast.date.dayOfMonth,
                year = dayForecast.date.year,
                status = status,
                isBookmarked = false,
                temperature = temperature,
                weather = weather,
                tickActivity = tickActivity,
                attendance = attendance,
                stolbyStatus = stolbyStatus,
                season = season
            )
        }
    }

    private fun fetchForecast(): List<OpenMeteoForecastDay> {
        val requestUrl = buildString {
            append("https://api.open-meteo.com/v1/forecast")
            append("?latitude=$STOLBY_LAT")
            append("&longitude=$STOLBY_LNG")
            append("&daily=weather_code,temperature_2m_mean")
            append("&forecast_days=$FORECAST_DAYS")
            append("&timezone=auto")
        }

        val connection = (URL(requestUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("User-Agent", "StolbOkApp/1.0")
        }

        return try {
            val responseCode = connection.responseCode
            val responseText = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                throw IOException("Open-Meteo request failed with HTTP $responseCode. $errorBody")
            }

            parseForecast(responseText)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseForecast(responseText: String): List<OpenMeteoForecastDay> {
        val root = JSONObject(responseText)
        val daily = root.getJSONObject("daily")
        val dates = daily.getJSONArray("time")
        val temperatures = daily.getJSONArray("temperature_2m_mean")
        val weatherCodes = daily.getJSONArray("weather_code")

        val count = min(dates.length(), min(temperatures.length(), weatherCodes.length()))
        return buildList(count) {
            for (index in 0 until count) {
                if (dates.isNull(index) || temperatures.isNull(index) || weatherCodes.isNull(index)) {
                    continue
                }

                add(
                    OpenMeteoForecastDay(
                        date = LocalDate.parse(dates.getString(index)),
                        meanTemperature = temperatures.getDouble(index),
                        weatherCode = weatherCodes.getInt(index)
                    )
                )
            }
        }
    }

    private fun mapWeatherCode(weatherCode: Int): Weather = when (weatherCode) {
        0 -> Weather.SUNNY
        1, 2 -> Weather.PARTLY
        3 -> Weather.CLOUDY
        45, 48 -> Weather.FOG
        51, 53, 55, 56, 57, 61, 63, 80 -> Weather.WEAK_RAIN
        65, 66, 67, 81, 82 -> Weather.RAINY
        71, 73, 75, 77, 85, 86 -> Weather.SNOWY
        95, 96, 99 -> Weather.THUNDER
        else -> Weather.CLOUDY
    }

    private data class OpenMeteoForecastDay(
        val date: LocalDate,
        val meanTemperature: Double,
        val weatherCode: Int
    )
}
