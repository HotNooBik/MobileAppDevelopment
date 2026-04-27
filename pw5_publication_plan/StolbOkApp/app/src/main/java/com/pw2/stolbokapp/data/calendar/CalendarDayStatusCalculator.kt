package com.pw2.stolbokapp.data.calendar

object CalendarDayStatusCalculator {

    fun calculateStatus(
        temperature: Int,
        weather: Weather,
        tickActivity: TickActivity,
        attendance: Attendance,
        stolbyStatus: StolbyStatus,
        season: Season
    ): DayStatus {
        if (stolbyStatus == StolbyStatus.CLOSED) return DayStatus.BAD
        if (weather == Weather.THUNDER) return DayStatus.BAD

        val score = buildScore(
            temperature = temperature,
            weather = weather,
            tickActivity = tickActivity,
            attendance = attendance,
            stolbyStatus = stolbyStatus,
            season = season
        )

        return when {
            score >= 5 -> DayStatus.AWESOME
            score >= 2 -> DayStatus.GOOD
            score >= -1 -> DayStatus.NOT_GOOD
            else -> DayStatus.BAD
        }
    }

    private fun buildScore(
        temperature: Int,
        weather: Weather,
        tickActivity: TickActivity,
        attendance: Attendance,
        stolbyStatus: StolbyStatus,
        season: Season
    ): Int {
        var score = 0
        score += weatherScore(weather, season)
        score += temperatureScore(temperature, season)
        score += tickScore(tickActivity)
        score += attendanceScore(attendance)
        score += stolbyStatusScore(stolbyStatus)
        return score
    }

    private fun weatherScore(weather: Weather, season: Season): Int = when (weather) {
        Weather.SUNNY -> 3
        Weather.PARTLY -> 3
        Weather.CLOUDY -> 2
        Weather.FOG -> -1
        Weather.WEAK_RAIN -> -2
        Weather.RAINY -> -4
        Weather.THUNDER -> -5
        Weather.SNOWY -> if (season == Season.WINTER) 1 else -3
    }

    private fun temperatureScore(temperature: Int, season: Season): Int = when {
        season == Season.WINTER && temperature in -12..-2 -> 2
        temperature in 12..24 -> 2
        temperature in 5..11 -> 1
        temperature in 0..4 -> 0
        temperature in 25..30 -> 1
        temperature in -10..-1 -> -1
        else -> -2
    }

    private fun tickScore(tickActivity: TickActivity): Int = when (tickActivity) {
        TickActivity.NONE -> 1
        TickActivity.LOW -> 0
        TickActivity.MODERATE -> -1
        TickActivity.HIGH -> -3
    }

    private fun attendanceScore(attendance: Attendance): Int = when (attendance) {
        Attendance.LOW -> 1
        Attendance.MEDIUM -> 0
        Attendance.HIGH -> -1
    }

    private fun stolbyStatusScore(status: StolbyStatus): Int = when (status) {
        StolbyStatus.OPEN -> 1
        StolbyStatus.FESTIVAL -> 2
        StolbyStatus.CLOSED -> -5
    }
}
