package com.beelow.journalbetter.util

import com.beelow.journalbetter.data.CalendarDay
import java.util.Calendar

fun getCalendarData(calendarMonthOffset: Int): Triple<List<CalendarDay>, Calendar, Int> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, calendarMonthOffset)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val prevCalendar = calendar.clone() as Calendar
    prevCalendar.add(Calendar.MONTH, -1)
    val prevMonth = prevCalendar.get(Calendar.MONTH)
    val prevYear = prevCalendar.get(Calendar.YEAR)
    val daysInPrevMonth = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val nextCalendar = calendar.clone() as Calendar
    nextCalendar.add(Calendar.MONTH, 1)
    val nextMonth = nextCalendar.get(Calendar.MONTH)
    val nextYear = nextCalendar.get(Calendar.YEAR)

    val calendarDays = mutableListOf<CalendarDay>()

    // Previous month's days
    for (i in (daysInPrevMonth - firstDayOfWeek + 1)..daysInPrevMonth) {
        calendarDays.add(CalendarDay(i, prevMonth, prevYear, false))
    }

    // Current month's days
    for (i in 1..daysInMonth) {
        calendarDays.add(CalendarDay(i, month, year, true))
    }

    val requiredCells = firstDayOfWeek + daysInMonth
    val numberOfRows = if (requiredCells > 35) 6 else 5

    // Next month's days
    val remainingCells = (numberOfRows * 7) - calendarDays.size
    for (i in 1..remainingCells) {
        calendarDays.add(CalendarDay(i, nextMonth, nextYear, false))
    }

    return Triple( calendarDays, calendar, numberOfRows)
}