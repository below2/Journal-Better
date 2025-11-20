package com.beelow.journalbetter.ui.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.beelow.journalbetter.data.CalendarDay


@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    numberOfRows: Int,
    calendarDays: List<CalendarDay>,
    navController: NavController
) {
    val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // Day of week headers
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dayHeaders.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    // Days
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val gridHeight = this.maxHeight
        val gridWidth = this.maxWidth

        val itemHeight = gridHeight / numberOfRows
        val itemWidth = gridWidth / 7

        val dynamicAspectRatio = itemWidth / itemHeight

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            items(calendarDays, key = { "${it.year}-${it.month + 1}-${it.day}" }) { day ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(dynamicAspectRatio)
                        .clickable(enabled = day.isCurrentMonth) {
                            navController.navigate("details/${day.year}-${day.month + 1}-${day.day}")
                        }
                ) {
                    Text(
                        text = day.day.toString(),
                        color = if (day.isCurrentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}