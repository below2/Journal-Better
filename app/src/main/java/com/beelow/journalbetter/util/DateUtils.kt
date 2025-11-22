package com.beelow.journalbetter.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun getDayNumberSuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayHeader(date: String?): String {
    if (date != null) {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d"))
        val day = parsedDate.dayOfMonth
        val monthName = parsedDate.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
        val dayWithSuffix = "$day${getDayNumberSuffix(day)}"
        return "$monthName $dayWithSuffix, ${parsedDate.year}"
    } else {
        return "Details"
    }
}

// Convert LocalDateTime -> Date
fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

// Convert Date -> LocalDateTime
fun Date.toLocalDateTime(): LocalDateTime {
    return this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}