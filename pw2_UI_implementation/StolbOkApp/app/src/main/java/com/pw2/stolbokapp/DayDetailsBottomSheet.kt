package com.pw2.stolbokapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DayDetailsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_DAY_OF_WEEK = "dayOfWeek"
        private const val ARG_MONTH = "month"
        private const val ARG_DAY_NUMBER = "dayNumber"
        private const val ARG_YEAR = "year"
        private const val ARG_STATUS = "status"
        private const val ARG_IS_BOOKMARKED = "isBookmarked"
        private const val ARG_TEMPERATURE = "temperature"
        private const val ARG_WEATHER = "weather"
        private const val ARG_TICK_ACTIVITY = "tickActivity"
        private const val ARG_ATTENDANCE = "attendance"
        private const val ARG_STOLBY_STATUS = "stolbyStatus"
        private const val ARG_SEASON = "season"

        fun newInstance(day: CalendarDay): DayDetailsBottomSheet {
            return DayDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_DAY_OF_WEEK, day.dayOfWeek)
                    putString(ARG_MONTH, day.month)
                    putInt(ARG_DAY_NUMBER, day.dayNumber)
                    putInt(ARG_YEAR, day.year)
                    putInt(ARG_STATUS, day.status.ordinal)
                    putBoolean(ARG_IS_BOOKMARKED, day.isBookmarked)
                    putInt(ARG_TEMPERATURE, day.temperature)
                    putInt(ARG_WEATHER, day.weather.ordinal)
                    putInt(ARG_TICK_ACTIVITY, day.tickActivity.ordinal)
                    putInt(ARG_ATTENDANCE, day.attendance.ordinal)
                    putInt(ARG_STOLBY_STATUS, day.stolbyStatus.ordinal)
                    putInt(ARG_SEASON, day.season.ordinal)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_day_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments ?: return
        val dayOfWeek = args.getString(ARG_DAY_OF_WEEK, "")
        val month = args.getString(ARG_MONTH, "")
        val dayNumber = args.getInt(ARG_DAY_NUMBER)
        val year = args.getInt(ARG_YEAR)
        val status = DayStatus.entries[args.getInt(ARG_STATUS)]
        val isBookmarked = args.getBoolean(ARG_IS_BOOKMARKED)
        val temperature = args.getInt(ARG_TEMPERATURE)
        val weather = Weather.entries[args.getInt(ARG_WEATHER)]
        val tickActivity = TickActivity.entries[args.getInt(ARG_TICK_ACTIVITY)]
        val attendance = Attendance.entries[args.getInt(ARG_ATTENDANCE)]
        val stolbyStatus = StolbyStatus.entries[args.getInt(ARG_STOLBY_STATUS)]
        val season = Season.entries[args.getInt(ARG_SEASON)]

        // Дата
        val tvDate = view.findViewById<TextView>(R.id.tvDayDate)
        tvDate.text = "$dayOfWeek, $dayNumber $month"

        // Статус дня
        val tvStatus = view.findViewById<TextView>(R.id.tvDayStatus)
        val (statusText, statusBg) = when (status) {
            DayStatus.AWESOME -> "Идеальный день" to R.drawable.bg_badge_awesome
            DayStatus.GOOD -> "Хороший день" to R.drawable.bg_badge_good
            DayStatus.NOT_GOOD -> "Плохой день" to R.drawable.bg_badge_not_good
            DayStatus.BAD -> "Ужасный день" to R.drawable.bg_badge_bad
        }
        tvStatus.text = statusText
        tvStatus.background = ContextCompat.getDrawable(requireContext(), statusBg)

        // Закладка
        val ivBookmark = view.findViewById<ImageView>(R.id.ivBookmark)
        var bookmarked = isBookmarked
        ivBookmark.setImageResource(
            if (bookmarked) R.drawable.ic_bookmark_added else R.drawable.ic_bookmark
        )
        ivBookmark.setOnClickListener {
            bookmarked = !bookmarked
            ivBookmark.setImageResource(
                if (bookmarked) R.drawable.ic_bookmark_added else R.drawable.ic_bookmark
            )
        }

        // Температура
        val tvTemperature = view.findViewById<TextView>(R.id.tvTemperature)
        tvTemperature.text = "${temperature}°C"
        // Цвет иконки термометра зависит от температуры
        val ivThermometer = view.findViewById<ImageView>(R.id.ivThermometer)
        val thermoColor = when {
            temperature < -20 -> R.color.temp_m_20
            temperature < -10 -> R.color.temp_m_10
            temperature < 0 -> R.color.temp_0
            temperature < 10 -> R.color.temp_10
            temperature < 20 -> R.color.temp_20
            temperature < 30 -> R.color.temp_30
            else -> R.color.temp_31
        }
        ivThermometer.setColorFilter(ContextCompat.getColor(requireContext(), thermoColor))

        // Погода
        val tvWeather = view.findViewById<TextView>(R.id.tvWeather)
        val ivWeatherIcon = view.findViewById<ImageView>(R.id.ivWeatherIcon)
        val (weatherText, weatherIcon) = getWeatherTextAndIcon(weather)
        tvWeather.text = weatherText
        ivWeatherIcon.setImageResource(weatherIcon)

        // Клещевая активность
        val tvTick = view.findViewById<TextView>(R.id.tvTickActivity)
        val (tickText, tickBg) = when (tickActivity) {
            TickActivity.NONE -> "нет" to R.drawable.bg_badge_awesome
            TickActivity.LOW -> "мало" to R.drawable.bg_badge_good
            TickActivity.MODERATE -> "не много" to R.drawable.bg_badge_not_good
            TickActivity.HIGH -> "много" to R.drawable.bg_badge_bad
        }
        tvTick.text = tickText
        tvTick.background = ContextCompat.getDrawable(requireContext(), tickBg)

        // Посещаемость
        val tvAttendance = view.findViewById<TextView>(R.id.tvAttendance)
        val (attendanceText, attendanceBg) = when (attendance) {
            Attendance.LOW -> "малая" to R.drawable.bg_badge_awesome
            Attendance.MEDIUM -> "средняя" to R.drawable.bg_badge_good
            Attendance.HIGH -> "высокая" to R.drawable.bg_badge_bad
        }
        tvAttendance.text = attendanceText
        tvAttendance.background = ContextCompat.getDrawable(requireContext(), attendanceBg)

        // Статус Столбов
        val tvStolbyStatus = view.findViewById<TextView>(R.id.tvStolbyStatus)
        val (stolbyText, stolbyBg) = when (stolbyStatus) {
            StolbyStatus.OPEN -> "открыто" to R.drawable.bg_badge_awesome
            StolbyStatus.CLOSED -> "закрыто" to R.drawable.bg_badge_bad
            StolbyStatus.FESTIVAL -> "фестиваль" to R.drawable.bg_badge_awesome
        }
        tvStolbyStatus.text = stolbyText
        tvStolbyStatus.background = ContextCompat.getDrawable(requireContext(), stolbyBg)

        // Картинка столбов (заглушка: зависит от сезона + погоды)
        val ivStolbyPhoto = view.findViewById<ImageView>(R.id.ivStolbyPhoto)
        val photoRes = getStolbyPhotoRes(season, weather)
        if (photoRes != 0) {
            ivStolbyPhoto.setImageResource(photoRes)
        }
    }

    private fun getWeatherTextAndIcon(weather: Weather): Pair<String, Int> {
        return when (weather) {
            Weather.SUNNY -> "Ясно" to R.drawable.ic_weather_sunny
            Weather.PARTLY -> "Переменно" to R.drawable.ic_weather_partly
            Weather.CLOUDY -> "Облачно" to R.drawable.ic_weather_cloudy
            Weather.FOG -> "Туман" to R.drawable.ic_weather_fog
            Weather.WEAK_RAIN -> "Слабый дождь" to R.drawable.ic_weather_weak_rain
            Weather.SNOWY -> "Снегопад" to R.drawable.ic_weather_snowy
            Weather.THUNDER -> "Гроза" to R.drawable.ic_weather_thunder
            Weather.RAINY -> "Ливень" to R.drawable.ic_weather_rainy
        }
    }

    /**
     * Заглушка: возвращает ресурс фото Столбов в зависимости от комбинации сезона и погоды.
     * TODO: Добавить реальные изображения для каждой комбинации сезон+погода.
     */
    private fun getStolbyPhotoRes(season: Season, weather: Weather): Int {
        return when (season) {
            Season.WINTER -> when (weather) {
                Weather.SUNNY -> R.drawable.img_day_winter_sunny
                Weather.PARTLY -> R.drawable.img_day_winter_partly
                Weather.SNOWY -> R.drawable.img_day_winter_snowy
                else -> R.drawable.img_day_winter
            }
            Season.SPRING -> when (weather) {
                Weather.SUNNY -> R.drawable.img_day_spring_sunny
                Weather.CLOUDY -> R.drawable.img_day_spring_cloudy
                Weather.SNOWY -> R.drawable.img_day_spring_snowy
                Weather.WEAK_RAIN -> R.drawable.img_day_spring_weak_rain
                Weather.RAINY -> R.drawable.img_day_spring_rainy
                Weather.FOG -> R.drawable.img_day_spring_fog
                Weather.THUNDER -> R.drawable.img_day_spring_thunder
                else -> R.drawable.img_day_spring_partly
            }
            Season.SUMMER -> when (weather) {
                Weather.SUNNY -> R.drawable.img_day_summer_sunny
                Weather.CLOUDY -> R.drawable.img_day_summer_cloudy
                Weather.WEAK_RAIN -> R.drawable.img_day_summer_weak_rain
                Weather.RAINY -> R.drawable.img_day_summer_rainy
                Weather.FOG -> R.drawable.img_day_summer_fog
                Weather.THUNDER -> R.drawable.img_day_summer_thunder
                else -> R.drawable.img_day_summer_partly
            }
            Season.AUTUMN -> when (weather) {
                Weather.SUNNY -> R.drawable.img_day_autumn_sunny
                Weather.CLOUDY -> R.drawable.img_day_autumn_cloudy
                Weather.SNOWY -> R.drawable.img_day_autumn_snowy
                Weather.WEAK_RAIN -> R.drawable.img_day_autumn_weak_rain
                Weather.RAINY -> R.drawable.img_day_autumn_rainy
                Weather.FOG -> R.drawable.img_day_autumn_fog
                Weather.THUNDER -> R.drawable.img_day_autumn_thunder
                else -> R.drawable.img_day_autumn_partly
            }
        }
    }
}

