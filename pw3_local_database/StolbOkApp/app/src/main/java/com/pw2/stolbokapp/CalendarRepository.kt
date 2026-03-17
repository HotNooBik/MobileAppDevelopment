package com.pw2.stolbokapp

object CalendarRepository {
    // Данные календаря: ключ — номер месяца (1..12), значение — список дней
    val calendarData: Map<Int, List<CalendarDay>> = mapOf(
        5 to listOf(
            CalendarDay("Чт", "Мая", 5, 1, 2025, DayStatus.NOT_GOOD, false, 12, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 5, 2, 2025, DayStatus.GOOD, false, 15, Weather.PARTLY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Сб", "Мая", 5, 3, 2025, DayStatus.AWESOME, false, 18, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 5, 4, 2025, DayStatus.AWESOME, false, 19, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 5, 5, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 5, 6, 2025, DayStatus.NOT_GOOD, false, 14, Weather.CLOUDY, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Ср", "Мая", 5, 7, 2025, DayStatus.GOOD, false, 16, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 5, 8, 2025, DayStatus.NOT_GOOD, false, 13, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 5, 9, 2025, DayStatus.BAD, false, 8, Weather.RAINY, TickActivity.HIGH, Attendance.LOW, StolbyStatus.CLOSED, Season.SPRING),
            CalendarDay("Сб", "Мая", 5, 10, 2025, DayStatus.AWESOME, false, 20, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 5, 11, 2025, DayStatus.GOOD, false, 18, Weather.PARTLY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 5, 12, 2025, DayStatus.NOT_GOOD, false, 14, Weather.CLOUDY, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 5, 13, 2025, DayStatus.GOOD, false, 16, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Ср", "Мая", 5, 14, 2025, DayStatus.AWESOME, false, 21, Weather.SUNNY, TickActivity.NONE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 5, 15, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 5, 16, 2025, DayStatus.AWESOME, true, 21, Weather.SUNNY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Сб", "Мая", 5, 17, 2025, DayStatus.GOOD, false, 19, Weather.PARTLY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 5, 18, 2025, DayStatus.NOT_GOOD, false, 13, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 5, 19, 2025, DayStatus.GOOD, false, 16, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 5, 20, 2025, DayStatus.NOT_GOOD, false, 14, Weather.CLOUDY, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Ср", "Мая", 5, 21, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 5, 22, 2025, DayStatus.NOT_GOOD, false, 12, Weather.FOG, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 5, 23, 2025, DayStatus.GOOD, false, 18, Weather.SUNNY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Сб", "Мая", 5, 24, 2025, DayStatus.NOT_GOOD, false, 15, Weather.CLOUDY, TickActivity.MODERATE, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вс", "Мая", 5, 25, 2025, DayStatus.AWESOME, false, 22, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пн", "Мая", 5, 26, 2025, DayStatus.GOOD, false, 19, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Вт", "Мая", 5, 27, 2025, DayStatus.BAD, false, 7, Weather.THUNDER, TickActivity.HIGH, Attendance.LOW, StolbyStatus.CLOSED, Season.SPRING),
            CalendarDay("Ср", "Мая", 5, 28, 2025, DayStatus.NOT_GOOD, false, 13, Weather.WEAK_RAIN, TickActivity.MODERATE, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Чт", "Мая", 5, 29, 2025, DayStatus.GOOD, false, 17, Weather.PARTLY, TickActivity.LOW, Attendance.LOW, StolbyStatus.OPEN, Season.SPRING),
            CalendarDay("Пт", "Мая", 5, 30, 2025, DayStatus.AWESOME, false, 21, Weather.SUNNY, TickActivity.LOW, Attendance.MEDIUM, StolbyStatus.FESTIVAL, Season.SPRING),
            CalendarDay("Сб", "Мая", 5, 31, 2025, DayStatus.GOOD, false, 20, Weather.SUNNY, TickActivity.LOW, Attendance.HIGH, StolbyStatus.FESTIVAL, Season.SPRING)
        )
    )

    fun getDay(month: Int, day: Int): CalendarDay? {
        return calendarData[month]?.find { it.dayNumber == day }
    }
}


