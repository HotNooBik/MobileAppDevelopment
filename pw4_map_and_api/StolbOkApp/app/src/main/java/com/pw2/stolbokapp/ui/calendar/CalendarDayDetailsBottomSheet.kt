package com.pw2.stolbokapp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.calendar.Attendance
import com.pw2.stolbokapp.data.calendar.CalendarDay
import com.pw2.stolbokapp.data.calendar.CalendarTextFormatter
import com.pw2.stolbokapp.data.calendar.DayStatus
import com.pw2.stolbokapp.data.calendar.Season
import com.pw2.stolbokapp.data.calendar.StolbyStatus
import com.pw2.stolbokapp.data.calendar.TickActivity
import com.pw2.stolbokapp.data.calendar.Weather
import com.pw2.stolbokapp.data.local.AppDatabase
import com.pw2.stolbokapp.data.local.PlanEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarDayDetailsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var db: AppDatabase

    companion object {
        private const val ARG_DAY_OF_WEEK = "dayOfWeek"
        private const val ARG_MONTH = "month"
        private const val ARG_MONTH_INDEX = "monthIndex"
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

        fun newInstance(day: CalendarDay): CalendarDayDetailsBottomSheet {
            return CalendarDayDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_DAY_OF_WEEK, day.dayOfWeek)
                    putString(ARG_MONTH, day.month)
                    putInt(ARG_MONTH_INDEX, day.monthIndex)
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
        db = AppDatabase.getDatabase(requireContext())
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
        val monthStr = args.getString(ARG_MONTH, "")
        val monthIndex = args.getInt(ARG_MONTH_INDEX, 1)
        val dayNumber = args.getInt(ARG_DAY_NUMBER)
        val year = args.getInt(ARG_YEAR)
        val status = DayStatus.entries[args.getInt(ARG_STATUS)]
        val isBookmarkedArg = args.getBoolean(ARG_IS_BOOKMARKED)
        val temperature = args.getInt(ARG_TEMPERATURE)
        val weather = Weather.entries[args.getInt(ARG_WEATHER)]
        val tickActivity = TickActivity.entries[args.getInt(ARG_TICK_ACTIVITY)]
        val attendance = Attendance.entries[args.getInt(ARG_ATTENDANCE)]
        val stolbyStatus = StolbyStatus.entries[args.getInt(ARG_STOLBY_STATUS)]
        val season = Season.entries[args.getInt(ARG_SEASON)]

        view.findViewById<TextView>(R.id.tvDayDate).text = "$dayOfWeek, $dayNumber $monthStr"

        val tvStatus = view.findViewById<TextView>(R.id.tvDayStatus)
        val statusBg = when (status) {
            DayStatus.AWESOME -> R.drawable.bg_badge_awesome
            DayStatus.GOOD -> R.drawable.bg_badge_good
            DayStatus.NOT_GOOD -> R.drawable.bg_badge_not_good
            DayStatus.BAD -> R.drawable.bg_badge_bad
        }
        tvStatus.text = CalendarTextFormatter.dayStatusText(status)
        tvStatus.background = ContextCompat.getDrawable(requireContext(), statusBg)

        val ivBookmark = view.findViewById<ImageView>(R.id.ivBookmark)
        var bookmarked = isBookmarkedArg
        ivBookmark.setImageResource(if (bookmarked) R.drawable.ic_bookmark_added else R.drawable.ic_bookmark)

        lifecycleScope.launch {
            val dbBookmarked = withContext(Dispatchers.IO) {
                db.planDao().isBookmarked(dayNumber, monthIndex, year)
            }
            bookmarked = dbBookmarked
            ivBookmark.setImageResource(if (bookmarked) R.drawable.ic_bookmark_added else R.drawable.ic_bookmark)
        }

        ivBookmark.setOnClickListener {
            lifecycleScope.launch {
                val newStatus = !bookmarked
                withContext(Dispatchers.IO) {
                    val plan = PlanEntity(dayNumber, monthIndex, year)
                    if (newStatus) {
                        db.planDao().addPlan(plan)
                    } else {
                        db.planDao().removePlan(plan)
                    }
                }
                bookmarked = newStatus
                ivBookmark.setImageResource(if (bookmarked) R.drawable.ic_bookmark_added else R.drawable.ic_bookmark)
            }
        }

        view.findViewById<TextView>(R.id.tvTemperature).text = "$temperature°C"
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

        view.findViewById<TextView>(R.id.tvWeather).text = CalendarTextFormatter.weatherText(weather)
        view.findViewById<ImageView>(R.id.ivWeatherIcon).setImageResource(CalendarWeatherUiMapper.iconRes(weather))

        val tvTick = view.findViewById<TextView>(R.id.tvTickActivity)
        val tickBg = when (tickActivity) {
            TickActivity.NONE -> R.drawable.bg_badge_awesome
            TickActivity.LOW -> R.drawable.bg_badge_good
            TickActivity.MODERATE -> R.drawable.bg_badge_not_good
            TickActivity.HIGH -> R.drawable.bg_badge_bad
        }
        tvTick.text = CalendarTextFormatter.tickActivityText(tickActivity)
        tvTick.background = ContextCompat.getDrawable(requireContext(), tickBg)

        val tvAttendance = view.findViewById<TextView>(R.id.tvAttendance)
        val attendanceBg = when (attendance) {
            Attendance.LOW -> R.drawable.bg_badge_awesome
            Attendance.MEDIUM -> R.drawable.bg_badge_good
            Attendance.HIGH -> R.drawable.bg_badge_bad
        }
        tvAttendance.text = CalendarTextFormatter.attendanceText(attendance)
        tvAttendance.background = ContextCompat.getDrawable(requireContext(), attendanceBg)

        val tvStolbyStatus = view.findViewById<TextView>(R.id.tvStolbyStatus)
        val stolbyStatusBg = when (stolbyStatus) {
            StolbyStatus.OPEN -> R.drawable.bg_badge_awesome
            StolbyStatus.CLOSED -> R.drawable.bg_badge_bad
            StolbyStatus.FESTIVAL -> R.drawable.bg_badge_good
        }
        tvStolbyStatus.text = CalendarTextFormatter.stolbyStatusText(stolbyStatus)
        tvStolbyStatus.background = ContextCompat.getDrawable(requireContext(), stolbyStatusBg)

        val ivStolbyPhoto = view.findViewById<ImageView>(R.id.ivStolbyPhoto)
        val photoRes = getStolbyPhotoRes(season, weather)
        if (photoRes != 0) {
            ivStolbyPhoto.setImageResource(photoRes)
        }
    }

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
