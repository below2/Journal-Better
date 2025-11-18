package com.beelow.journalbetter.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDateTime
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Calendar
import java.util.Locale

data class CalendarDay(val day: Int, val month: Int, val year: Int, val isCurrentMonth: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    var quickNote by remember { mutableStateOf("") }
    var calendarMonthOffset by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal Better") },
                actions = {
                    IconButton(onClick = { /* TODO: Add entry logic */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
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

        val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with month name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    calendarMonthOffset -= 1
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
                }
                Text(
                    text = "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.uppercase()} $year",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = {
                    calendarMonthOffset += 1
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
                }
            }

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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Days
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                                color = if (day.isCurrentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }

            // Quick note text field and FAB
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedTextField(
                    value = quickNote,
                    onValueChange = { quickNote = it },
                    label = { Text("Quick note") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(25)
                )
                FloatingActionButton(
                    onClick = {
                        val year = LocalDateTime.now().year.toString()
                        val month = LocalDateTime.now().monthValue.toString()
                        val day = LocalDateTime.now().dayOfMonth.toString()
                        val date = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
                        val encodedQuickNote = URLEncoder.encode(quickNote, StandardCharsets.UTF_8.toString())
                        navController.navigate("details/${date}?quickNote=$encodedQuickNote")
                        quickNote = ""
                    },
                    modifier = Modifier.padding(start = 16.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Filled.Add, "Add journal entry")
                }
            }
        }
    }
}