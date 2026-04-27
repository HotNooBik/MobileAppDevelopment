package com.pw2.stolbokapp.data.calendar

import com.pw2.stolbokapp.data.local.CalendarDayEntity

fun CalendarDay.toEntity(): CalendarDayEntity {
    return CalendarDayEntity(
        date = date.toString(),
        dayOfWeek = dayOfWeek,
        month = month,
        monthIndex = monthIndex,
        dayNumber = dayNumber,
        year = year,
        status = status,
        temperature = temperature,
        weather = weather,
        tickActivity = tickActivity,
        attendance = attendance,
        stolbyStatus = stolbyStatus,
        season = season
    )
}

fun CalendarDayEntity.toDomain(): CalendarDay {
    return CalendarDay(
        dayOfWeek = dayOfWeek,
        month = month,
        monthIndex = monthIndex,
        dayNumber = dayNumber,
        year = year,
        status = status,
        isBookmarked = false,
        temperature = temperature,
        weather = weather,
        tickActivity = tickActivity,
        attendance = attendance,
        stolbyStatus = stolbyStatus,
        season = season
    )
}
