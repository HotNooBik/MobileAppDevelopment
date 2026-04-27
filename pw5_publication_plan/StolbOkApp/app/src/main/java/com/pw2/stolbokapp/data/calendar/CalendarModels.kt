package com.pw2.stolbokapp.data.calendar

import java.time.LocalDate

enum class DayStatus { AWESOME, GOOD, NOT_GOOD, BAD }

enum class Weather { SUNNY, PARTLY, CLOUDY, FOG, WEAK_RAIN, SNOWY, THUNDER, RAINY }

enum class TickActivity { NONE, LOW, MODERATE, HIGH }

enum class Attendance { LOW, MEDIUM, HIGH }

enum class StolbyStatus { OPEN, CLOSED, FESTIVAL }

enum class Season { WINTER, SPRING, SUMMER, AUTUMN }

data class CalendarDay(
    val dayOfWeek: String,
    val month: String,
    val monthIndex: Int,
    val dayNumber: Int,
    val year: Int,
    val status: DayStatus,
    val isBookmarked: Boolean,
    val temperature: Int,
    val weather: Weather,
    val tickActivity: TickActivity,
    val attendance: Attendance,
    val stolbyStatus: StolbyStatus,
    val season: Season
) {
    val date: LocalDate
        get() = LocalDate.of(year, monthIndex, dayNumber)
}
