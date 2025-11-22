package com.beelow.journalbetter.ui.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.beelow.journalbetter.data.CalendarDay
import java.time.LocalDate

@Composable
fun CalendarDayItem(
    day: CalendarDay,
    modifier: Modifier = Modifier,
    onDayClick: (CalendarDay) -> Unit
) {
    val today = LocalDate.now()
    val isToday = day.year == today.year &&
            (day.month + 1) == today.monthValue &&
            day.day == today.dayOfMonth

    Box(
        modifier = Modifier
            .clickable(enabled = day.isCurrentMonth) { onDayClick(day) }
    )
    {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent
                )
        ) {
            Text(
                text = day.day.toString(),
                style = MaterialTheme.typography.titleMedium,
                // Current day is highlighted
                color = when {
                    isToday -> MaterialTheme.colorScheme.onPrimary
                    day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
            )
        }
    }
}