package com.pw2.stolbokapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class CalendarFragment : Fragment() {

    // Текущий день (заглушка — 1 мая, логика определения будет реализована позже)
    private val currentDayMonth = 5
    private val currentDayNumber = 1

    // Данные календаря: ключ — номер месяца (1..12), значение — список дней
    private val calendarData: Map<Int, List<CalendarDay>> = mapOf(
        5 to listOf(
            CalendarDay("Чт", "Мая", 1, 2025, DayStatus.NOT_GOOD, false, 12, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 2, 2025, DayStatus.GOOD, false, 15, Weather.PARTLY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Сб", "Мая", 3, 2025, DayStatus.AWESOME, false, 18, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 4, 2025, DayStatus.AWESOME, false, 19, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 5, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 6, 2025, DayStatus.NOT_GOOD, false, 14, Weather.CLOUDY, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Ср", "Мая", 7, 2025, DayStatus.GOOD, false, 16, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 8, 2025, DayStatus.NOT_GOOD, false, 13, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 9, 2025, DayStatus.BAD, false, 8, Weather.RAINY, TickActivity.HIGH, Attendance.LOW, StolbyStatus.CLOSED, Season.SPRING),
            CalendarDay("Сб", "Мая", 10, 2025, DayStatus.AWESOME, false, 20, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 11, 2025, DayStatus.GOOD, false, 18, Weather.PARTLY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 12, 2025, DayStatus.NOT_GOOD, false, 14, Weather.CLOUDY, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 13, 2025, DayStatus.GOOD, false, 16, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Ср", "Мая", 14, 2025, DayStatus.AWESOME, false, 21, Weather.SUNNY, TickActivity.NONE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 15, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 16, 2025, DayStatus.AWESOME, true, 21, Weather.SUNNY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Сб", "Мая", 17, 2025, DayStatus.GOOD, false, 19, Weather.PARTLY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 18, 2025, DayStatus.NOT_GOOD, false, 13, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 19, 2025, DayStatus.GOOD, false, 16, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 20, 2025, DayStatus.NOT_GOOD, false, 14, Weather.CLOUDY, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Ср", "Мая", 21, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 22, 2025, DayStatus.NOT_GOOD, false, 12, Weather.FOG, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 23, 2025, DayStatus.GOOD, false, 18, Weather.SUNNY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Сб", "Мая", 24, 2025, DayStatus.NOT_GOOD, false, 15, Weather.CLOUDY, TickActivity.MODERATE, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 25, 2025, DayStatus.AWESOME, false, 22, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 26, 2025, DayStatus.GOOD, false, 19, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 27, 2025, DayStatus.BAD, false, 7, Weather.THUNDER, TickActivity.HIGH, Attendance.LOW, StolbyStatus.CLOSED, Season.SPRING),
            CalendarDay("Ср", "Мая", 28, 2025, DayStatus.NOT_GOOD, false, 13, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 29, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 30, 2025, DayStatus.AWESOME, false, 21, Weather.SUNNY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.FESTIVAL, Season.SPRING),
            CalendarDay("Сб", "Мая", 31, 2025, DayStatus.GOOD, false, 20, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.FESTIVAL, Season.SPRING)
        )
    )

    // Первый день недели каждого месяца в 2025 году (0=Пн, 6=Вс)
    private val monthFirstDayOffset = mapOf(
        1 to 2, 2 to 5, 3 to 5, 4 to 1, 5 to 3, 6 to 6,
        7 to 1, 8 to 4, 9 to 0, 10 to 2, 11 to 5, 12 to 0
    )

    private val daysInMonth = mapOf(
        1 to 31, 2 to 28, 3 to 31, 4 to 30, 5 to 31, 6 to 30,
        7 to 31, 8 to 31, 9 to 30, 10 to 31, 11 to 30, 12 to 31
    )

    private var selectedMonth = 5 // Май по умолчанию

    private val monthChipIds = listOf(
        R.id.chipJan, R.id.chipFeb, R.id.chipMar, R.id.chipApr,
        R.id.chipMay, R.id.chipJun, R.id.chipJul, R.id.chipAug,
        R.id.chipSep, R.id.chipOct, R.id.chipNov, R.id.chipDec
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCurrentDayInfo(view)
        setupMonthChips(view)
        buildCalendarGrid(view, selectedMonth)
    }

    private fun setupCurrentDayInfo(view: View) {
        val days = calendarData[currentDayMonth]
        val todayData = days?.find { it.dayNumber == currentDayNumber } ?: return

        // Месяц
        val tvMonth = view.findViewById<TextView>(R.id.tvCurrentMonth)
        tvMonth.text = todayData.month.replaceFirstChar { it.uppercase() }

        // Номер дня
        val tvDayNumber = view.findViewById<TextView>(R.id.tvCurrentDayNumber)
        tvDayNumber.text = todayData.dayNumber.toString()

        // Фон большого дня
        val bigBgRes = when (todayData.status) {
            DayStatus.AWESOME -> R.drawable.bg_day_awesome_big
            DayStatus.GOOD -> R.drawable.bg_day_good_big
            DayStatus.NOT_GOOD -> R.drawable.bg_day_not_good_big
            DayStatus.BAD -> R.drawable.bg_day_bad_big
        }
        tvDayNumber.background = ContextCompat.getDrawable(requireContext(), bigBgRes)

        // Статус дня
        val tvStatus = view.findViewById<TextView>(R.id.tvCurrentDayStatus)
        tvStatus.text = when (todayData.status) {
            DayStatus.AWESOME -> "Идеальный день"
            DayStatus.GOOD -> "Хороший день"
            DayStatus.NOT_GOOD -> "Плохой день"
            DayStatus.BAD -> "Ужасный день"
        }

        // Погода
        val tvWeather = view.findViewById<TextView>(R.id.tvCurrentWeather)
        val (weatherText, weatherIcon) = getWeatherTextAndIcon(todayData.weather)
        tvWeather.text = weatherText
        tvWeather.setCompoundDrawablesWithIntrinsicBounds(0, 0, weatherIcon, 0)

        // Клик на блок «Сегодня» — открывает BottomSheet
        val currentDayLayout = view.findViewById<View>(R.id.constraintLayout)
        currentDayLayout.setOnClickListener {
            DayDetailsBottomSheet.newInstance(todayData)
                .show(childFragmentManager, "day_details")
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

    private fun setupMonthChips(view: View) {
        for (i in monthChipIds.indices) {
            val chip = view.findViewById<TextView>(monthChipIds[i])
            chip.setOnClickListener {
                selectedMonth = i + 1
                updateChipSelection(view)
                buildCalendarGrid(view, selectedMonth)
            }
        }
        updateChipSelection(view)
    }

    private fun updateChipSelection(view: View) {
        for (i in monthChipIds.indices) {
            val chip = view.findViewById<TextView>(monthChipIds[i])
            if (i + 1 == selectedMonth) {
                chip.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_selected)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                chip.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_unselected)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            }
        }
    }

    private fun buildCalendarGrid(view: View, month: Int) {
        val grid = view.findViewById<LinearLayout>(R.id.calendarGrid)
        grid.removeAllViews()

        val totalDays = daysInMonth[month] ?: 31
        val firstDayOffset = monthFirstDayOffset[month] ?: 0
        val days = calendarData[month]

        val fixedRows = 6 // Всегда 6 строк
        var dayCounter = 1

        for (row in 0 until fixedRows) {
            val rowLayout = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            for (col in 0..6) {
                val cellIndex = row * 7 + col
                val cellView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_calendar_day, rowLayout, false)

                val tvDayNumber = cellView.findViewById<TextView>(R.id.tvDayNumber)
                val dotBookmark = cellView.findViewById<View>(R.id.dotBookmark)

                if (cellIndex < firstDayOffset || dayCounter > totalDays) {
                    // Пустая ячейка
                    tvDayNumber.text = ""
                    tvDayNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_none)
                    dotBookmark.visibility = View.INVISIBLE
                } else {
                    val currentDayNum = dayCounter
                    tvDayNumber.text = currentDayNum.toString()

                    val dayData = days?.find { it.dayNumber == currentDayNum }

                    if (dayData != null) {
                        val bgRes = when (dayData.status) {
                            DayStatus.AWESOME -> R.drawable.bg_day_awesome
                            DayStatus.GOOD -> R.drawable.bg_day_good
                            DayStatus.NOT_GOOD -> R.drawable.bg_day_not_good
                            DayStatus.BAD -> R.drawable.bg_day_bad
                        }
                        tvDayNumber.background = ContextCompat.getDrawable(requireContext(), bgRes)

                        dotBookmark.visibility = if (dayData.isBookmarked) View.VISIBLE else View.INVISIBLE

                        cellView.setOnClickListener {
                            DayDetailsBottomSheet.newInstance(dayData)
                                .show(childFragmentManager, "day_details")
                        }
                    } else {
                        tvDayNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_none)
                        dotBookmark.visibility = View.INVISIBLE
                    }

                    dayCounter++
                }

                rowLayout.addView(cellView)
            }

            grid.addView(rowLayout)
        }
    }
}
