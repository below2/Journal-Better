package com.beelow.journalbetter.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.beelow.journalbetter.ui.calendar.components.AnimatedCalendarView
import com.beelow.journalbetter.ui.calendar.components.CalendarHeader
import com.beelow.journalbetter.ui.calendar.components.QuickNoteInput
import com.beelow.journalbetter.util.getCalendarData
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    val (_, headerCalendar, _) = getCalendarData(uiState.calendarMonthOffset)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Journal Better", style = MaterialTheme.typography.headlineMedium)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CalendarHeader(
                currentMonthYearText = "${headerCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.uppercase()} ${headerCalendar.get(Calendar.YEAR)}",
                onPreviousMonthClick = { viewModel.onSetPreviousMonth() },
                onNextMonthClick = { viewModel.onSetNextMonth() }
            )

            AnimatedCalendarView(
                monthOffset = uiState.calendarMonthOffset,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onPreviousMonth = { viewModel.onSetPreviousMonth() },
                onNextMonth = { viewModel.onSetNextMonth() },
                onDayClick = { day ->
                    val date = String.format("%d-%02d-%02d", day.year, day.month + 1, day.day)
                    navController.navigate("dayDetails/$date")
                }
            )

            QuickNoteInput(
                onQuickNoteEntered = { note ->
                    val now = LocalDateTime.now()
                    val date = String.format("%d-%02d-%02d", now.year, now.monthValue, now.dayOfMonth)
                    val encodedNote = URLEncoder.encode(note, StandardCharsets.UTF_8.toString())
                    navController.navigate("entries/$date?quickNote=$encodedNote")
                }
            )
        }
    }
}