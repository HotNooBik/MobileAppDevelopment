package com.pw2.stolbokapp.data.calendar

import java.time.DayOfWeek
import java.time.LocalDate

object CalendarTextFormatter {
    fun shortDayOfWeek(date: LocalDate): String = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
        DayOfWeek.SUNDAY -> "Вс"
    }

    fun monthNameGenitive(month: Int): String = when (month) {
        1 -> "января"
        2 -> "февраля"
        3 -> "марта"
        4 -> "апреля"
        5 -> "мая"
        6 -> "июня"
        7 -> "июля"
        8 -> "августа"
        9 -> "сентября"
        10 -> "октября"
        11 -> "ноября"
        12 -> "декабря"
        else -> month.toString()
    }

    fun monthNameNominative(month: Int): String = when (month) {
        1 -> "Январь"
        2 -> "Февраль"
        3 -> "Март"
        4 -> "Апрель"
        5 -> "Май"
        6 -> "Июнь"
        7 -> "Июль"
        8 -> "Август"
        9 -> "Сентябрь"
        10 -> "Октябрь"
        11 -> "Ноябрь"
        12 -> "Декабрь"
        else -> month.toString()
    }

    fun dayStatusText(status: DayStatus): String = when (status) {
        DayStatus.AWESOME -> "Идеальный день"
        DayStatus.GOOD -> "Хороший день"
        DayStatus.NOT_GOOD -> "Не лучший день"
        DayStatus.BAD -> "Плохой день"
    }

    fun weatherText(weather: Weather): String = when (weather) {
        Weather.SUNNY -> "Ясно"
        Weather.PARTLY -> "Переменная облачность"
        Weather.CLOUDY -> "Облачно"
        Weather.FOG -> "Туман"
        Weather.WEAK_RAIN -> "Небольшой дождь"
        Weather.SNOWY -> "Снег"
        Weather.THUNDER -> "Гроза"
        Weather.RAINY -> "Дождь"
    }

    fun tickActivityText(tickActivity: TickActivity): String = when (tickActivity) {
        TickActivity.NONE -> "нет"
        TickActivity.LOW -> "низкая"
        TickActivity.MODERATE -> "средняя"
        TickActivity.HIGH -> "высокая"
    }

    fun attendanceText(attendance: Attendance): String = when (attendance) {
        Attendance.LOW -> "низкая"
        Attendance.MEDIUM -> "средняя"
        Attendance.HIGH -> "высокая"
    }

    fun stolbyStatusText(status: StolbyStatus): String = when (status) {
        StolbyStatus.OPEN -> "открыто"
        StolbyStatus.CLOSED -> "закрыто"
        StolbyStatus.FESTIVAL -> "фестиваль"
    }
}
