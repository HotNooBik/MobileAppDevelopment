package com.pw2.stolbokapp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pw2.stolbokapp.R
import com.pw2.stolbokapp.data.calendar.CalendarDay
import com.pw2.stolbokapp.data.calendar.CalendarDaysService
import com.pw2.stolbokapp.data.calendar.CalendarRepository
import com.pw2.stolbokapp.data.calendar.CalendarTextFormatter
import com.pw2.stolbokapp.data.calendar.DayStatus
import com.pw2.stolbokapp.data.calendar.FakeServerCalendarService
import com.pw2.stolbokapp.data.local.AppDatabase
import com.pw2.stolbokapp.data.local.PlanEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarFragment : Fragment() {

    private lateinit var db: AppDatabase
    private val calendarDaysService: CalendarDaysService = FakeServerCalendarService()

    private var bookmarkedDays: List<PlanEntity> = emptyList()
    private var calendarDays: List<CalendarDay> = emptyList()
    private var currentCalendarDay: CalendarDay? = null
    private var selectedYearMonth: YearMonth? = null
    private var availableMonthsByValue: Map<Int, YearMonth> = emptyMap()
    private var pendingTargetDate: LocalDate? = null

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

        db = AppDatabase.getDatabase(requireContext())
        pendingTargetDate = parsePendingTargetDate()

        setupMonthChips(view)
        observeBookmarks(view)
        loadCalendarData(view)
    }

    private fun observeBookmarks(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            db.planDao().getAllPlans().collect { plans ->
                bookmarkedDays = plans
                if (calendarDays.isNotEmpty()) {
                    buildCalendarGrid(view, selectedYearMonth)
                }
            }
        }
    }

    private fun loadCalendarData(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            val days = try {
                calendarDaysService.getCalendarDays()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Не удалось загрузить прогноз погоды",
                    Toast.LENGTH_SHORT
                ).show()
                emptyList()
            }

            calendarDays = days.sortedBy { it.date }
            CalendarRepository.updateCalendarDays(calendarDays)
            availableMonthsByValue = calendarDays
                .map { YearMonth.of(it.year, it.monthIndex) }
                .distinct()
                .associateBy { it.monthValue }

            val today = LocalDate.now()
            currentCalendarDay = calendarDays.firstOrNull { it.date == today } ?: calendarDays.firstOrNull()
            selectedYearMonth = pendingTargetDate?.let { YearMonth.of(it.year, it.monthValue) }
                ?: currentCalendarDay?.let { YearMonth.of(it.year, it.monthIndex) }
                ?: availableMonthsByValue.values.firstOrNull()

            setupCurrentDayInfo(view)
            updateChipSelection(view)
            buildCalendarGrid(view, selectedYearMonth)
            openTargetDayIfNeeded()
        }
    }

    private fun parsePendingTargetDate(): LocalDate? {
        val args = arguments ?: return null
        if (!args.containsKey("targetDay") || !args.containsKey("targetMonth")) return null

        val day = args.getInt("targetDay")
        val month = args.getInt("targetMonth")
        val year = args.getInt("targetYear", LocalDate.now().year)
        return runCatching { LocalDate.of(year, month, day) }.getOrNull()
    }

    private fun setupCurrentDayInfo(view: View) {
        val todayData = currentCalendarDay ?: return

        val tvMonth = view.findViewById<TextView>(R.id.tvCurrentMonth)
        tvMonth.text = CalendarTextFormatter.monthNameNominative(todayData.monthIndex)

        val tvDayNumber = view.findViewById<TextView>(R.id.tvCurrentDayNumber)
        tvDayNumber.text = todayData.dayNumber.toString()

        val bigBgRes = when (todayData.status) {
            DayStatus.AWESOME -> R.drawable.bg_day_awesome_big
            DayStatus.GOOD -> R.drawable.bg_day_good_big
            DayStatus.NOT_GOOD -> R.drawable.bg_day_not_good_big
            DayStatus.BAD -> R.drawable.bg_day_bad_big
        }
        tvDayNumber.background = ContextCompat.getDrawable(requireContext(), bigBgRes)

        val tvStatus = view.findViewById<TextView>(R.id.tvCurrentDayStatus)
        tvStatus.text = CalendarTextFormatter.dayStatusText(todayData.status)

        val tvWeather = view.findViewById<TextView>(R.id.tvCurrentWeather)
        tvWeather.text = CalendarTextFormatter.weatherText(todayData.weather)


        val iconRes = CalendarWeatherUiMapper.iconRes(todayData.weather)
        val drawable = ContextCompat.getDrawable(view.context, iconRes)
        val sizeInPx = (20 * view.resources.displayMetrics.density).toInt()
        drawable?.setBounds(0, 0, sizeInPx, sizeInPx)
        tvWeather.setCompoundDrawablesRelative(null, null, drawable, null)

        view.findViewById<View>(R.id.constraintLayout).setOnClickListener {
            CalendarDayDetailsBottomSheet.newInstance(todayData)
                .show(childFragmentManager, "day_details")
        }
    }

    private fun setupMonthChips(view: View) {
        for (index in monthChipIds.indices) {
            val chip = view.findViewById<TextView>(monthChipIds[index])
            chip.setOnClickListener {
                val monthValue = index + 1
                val fallbackYear = selectedYearMonth?.year ?: LocalDate.now().year
                selectedYearMonth = availableMonthsByValue[monthValue]
                    ?: YearMonth.of(fallbackYear, monthValue)
                updateChipSelection(view)
                buildCalendarGrid(view, selectedYearMonth)
            }
        }
    }

    private fun updateChipSelection(view: View) {
        val selectedMonthValue = selectedYearMonth?.monthValue
        for (index in monthChipIds.indices) {
            val chip = view.findViewById<TextView>(monthChipIds[index])
            if (index + 1 == selectedMonthValue) {
                chip.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_selected)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                chip.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_unselected)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            }
        }
    }

    private fun buildCalendarGrid(view: View, yearMonth: YearMonth?) {
        val grid = view.findViewById<LinearLayout>(R.id.calendarGrid)
        grid.removeAllViews()

        val currentYearMonth = yearMonth ?: return
        val daysByNumber = calendarDays
            .filter { it.year == currentYearMonth.year && it.monthIndex == currentYearMonth.monthValue }
            .associateBy { it.dayNumber }

        val totalDays = currentYearMonth.lengthOfMonth()
        val firstDayOffset = currentYearMonth.atDay(1).dayOfWeek.value - 1
        val fixedRows = 6
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
                    tvDayNumber.text = ""
                    tvDayNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_none)
                    dotBookmark.visibility = View.INVISIBLE
                } else {
                    val currentDayNum = dayCounter
                    val dayData = daysByNumber[currentDayNum]

                    if (dayData != null) {
                        tvDayNumber.text = currentDayNum.toString()
                        val bgRes = when (dayData.status) {
                            DayStatus.AWESOME -> R.drawable.bg_day_awesome
                            DayStatus.GOOD -> R.drawable.bg_day_good
                            DayStatus.NOT_GOOD -> R.drawable.bg_day_not_good
                            DayStatus.BAD -> R.drawable.bg_day_bad
                        }
                        tvDayNumber.background = ContextCompat.getDrawable(requireContext(), bgRes)

                        val isBookmarkedInDb = bookmarkedDays.any {
                            it.dayNumber == currentDayNum &&
                                it.month == currentYearMonth.monthValue &&
                                it.year == currentYearMonth.year
                        }
                        dotBookmark.visibility = if (isBookmarkedInDb) View.VISIBLE else View.INVISIBLE

                        cellView.setOnClickListener {
                            CalendarDayDetailsBottomSheet.newInstance(dayData)
                                .show(childFragmentManager, "day_details")
                        }
                    } else {
                        tvDayNumber.text = ""
                        tvDayNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_none)
                        dotBookmark.visibility = View.INVISIBLE
                        cellView.setOnClickListener(null)
                    }

                    dayCounter++
                }

                rowLayout.addView(cellView)
            }

            grid.addView(rowLayout)
        }
    }

    private fun openTargetDayIfNeeded() {
        val targetDate = pendingTargetDate ?: return
        val dayData = CalendarRepository.getDay(
            month = targetDate.monthValue,
            day = targetDate.dayOfMonth,
            year = targetDate.year
        ) ?: return

        CalendarDayDetailsBottomSheet.newInstance(dayData)
            .show(parentFragmentManager, "day_details")

        arguments?.remove("targetDay")
        arguments?.remove("targetMonth")
        arguments?.remove("targetYear")
        pendingTargetDate = null
    }
}
