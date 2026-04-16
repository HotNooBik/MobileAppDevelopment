package com.pw2.stolbokapp.data.calendar

interface CalendarDaysService {
    suspend fun getCalendarDays(): List<CalendarDay>
}
