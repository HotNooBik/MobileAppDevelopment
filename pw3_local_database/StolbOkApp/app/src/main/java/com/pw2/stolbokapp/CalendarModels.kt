package com.pw2.stolbokapp

enum class DayStatus { AWESOME, GOOD, NOT_GOOD, BAD }

enum class Weather { SUNNY, PARTLY, CLOUDY, FOG, WEAK_RAIN, SNOWY, THUNDER, RAINY }

enum class TickActivity { NONE, LOW, MODERATE, HIGH }

enum class Attendance { LOW, MEDIUM, HIGH }

enum class StolbyStatus { OPEN, CLOSED, FESTIVAL }

enum class Season { WINTER, SPRING, SUMMER, AUTUMN }

data class CalendarDay(
    val dayOfWeek: String,       // Пн, Вт, Ср...
    val month: String,           // Май
    val monthIndex: Int,         // 1..12 (например, Май = 5)
    val dayNumber: Int,          // 1..31
    val year: Int,               // 2025
    val status: DayStatus,
    val isBookmarked: Boolean,   // добавлен ли в запланированное
    val temperature: Int,        // температура днём в °C
    val weather: Weather,
    val tickActivity: TickActivity,
    val attendance: Attendance,
    val stolbyStatus: StolbyStatus,
    val season: Season
)

