package com.pw2.stolbokapp.data.calendar

import java.time.DayOfWeek
import java.time.LocalDate

object CalendarRepository {
    private var calendarDays: List<CalendarDay> = emptyList()

    fun updateCalendarDays(days: List<CalendarDay>) {
        calendarDays = days.sortedBy { it.date }
    }

    fun getDay(month: Int, day: Int, year: Int? = null): CalendarDay? {
        return calendarDays.firstOrNull {
            it.monthIndex == month &&
                it.dayNumber == day &&
                (year == null || it.year == year)
        }
    }

    fun getTickActivity(date: LocalDate, temperature: Int, weather: Weather): TickActivity {
        val baseActivity = when (date.monthValue) {
            5, 6, 8 -> TickActivity.HIGH
            4, 7 -> TickActivity.MODERATE
            3, 9 -> TickActivity.LOW
            else -> TickActivity.NONE
        }

        return when {
            temperature < 0 -> TickActivity.NONE
            temperature > 25 && isDryWeather(weather) -> lowerTickActivity(baseActivity)
            else -> baseActivity
        }
    }

    fun getAttendance(date: LocalDate): Attendance {
        val season = getSeason(date)
        val weekendAttendance = when (season) {
            Season.SUMMER -> Attendance.HIGH
            Season.SPRING -> Attendance.HIGH
            Season.AUTUMN -> Attendance.MEDIUM
            Season.WINTER -> Attendance.LOW
        }
        val weekdayAttendance = when (season) {
            Season.SUMMER -> Attendance.MEDIUM
            Season.SPRING -> Attendance.MEDIUM
            Season.AUTUMN -> Attendance.LOW
            Season.WINTER -> Attendance.LOW
        }

        return when (date.dayOfWeek) {
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> weekendAttendance
            DayOfWeek.FRIDAY -> if (weekendAttendance == Attendance.HIGH) Attendance.MEDIUM else weekendAttendance
            else -> weekdayAttendance
        }
    }

    fun getStolbyStatus(date: LocalDate): StolbyStatus {
        val season = getSeason(date)
        return when {
            isTraditionalClosurePeriod(date) -> StolbyStatus.CLOSED
            season == Season.SUMMER &&
                date.dayOfWeek == DayOfWeek.SATURDAY &&
                date.dayOfMonth in 10..20 -> StolbyStatus.FESTIVAL
            else -> StolbyStatus.OPEN
        }
    }

    fun getSeason(date: LocalDate): Season = when (date.monthValue) {
        12, 1, 2 -> Season.WINTER
        3, 4, 5 -> Season.SPRING
        6, 7, 8 -> Season.SUMMER
        else -> Season.AUTUMN
    }

    private fun isDryWeather(weather: Weather): Boolean {
        return weather == Weather.SUNNY ||
            weather == Weather.PARTLY ||
            weather == Weather.CLOUDY
    }

    private fun lowerTickActivity(activity: TickActivity): TickActivity = when (activity) {
        TickActivity.HIGH -> TickActivity.MODERATE
        TickActivity.MODERATE -> TickActivity.LOW
        TickActivity.LOW -> TickActivity.NONE
        TickActivity.NONE -> TickActivity.NONE
    }

    private fun isTraditionalClosurePeriod(date: LocalDate): Boolean {
        return (date.monthValue == 5 && date.dayOfMonth >= 15) ||
            (date.monthValue == 6 && date.dayOfMonth <= 5)
    }
}
