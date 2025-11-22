package com.beelow.journalbetter.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beelow.journalbetter.data.CalendarDay

@Composable
fun CalendarGrid(
    calendarDays: List<CalendarDay>,
    numberOfRows: Int,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayHeaders = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    Column(modifier = modifier) {
        // Day of week headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayHeaders.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Grid
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            // Calculate aspect ratio of cells
            val itemHeight = maxHeight / numberOfRows
            val itemWidth = maxWidth / 7
            val dynamicAspectRatio = itemWidth / itemHeight

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) {
                items(calendarDays, key = { "${it.year}-${it.month}-${it.day}" }) { day ->
                    CalendarDayItem(
                        day = day,
                        modifier = Modifier.aspectRatio(dynamicAspectRatio),
                        onDayClick = onDayClick
                    )
                }
            }
        }
    }
}