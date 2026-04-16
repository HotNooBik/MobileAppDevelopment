package com.pw2.stolbokapp.ui.calendar

import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.calendar.Weather

object CalendarWeatherUiMapper {
    fun iconRes(weather: Weather): Int = when (weather) {
        Weather.SUNNY -> R.drawable.ic_weather_sunny
        Weather.PARTLY -> R.drawable.ic_weather_partly
        Weather.CLOUDY -> R.drawable.ic_weather_cloudy
        Weather.FOG -> R.drawable.ic_weather_fog
        Weather.WEAK_RAIN -> R.drawable.ic_weather_weak_rain
        Weather.SNOWY -> R.drawable.ic_weather_snowy
        Weather.THUNDER -> R.drawable.ic_weather_thunder
        Weather.RAINY -> R.drawable.ic_weather_rainy
    }
}
