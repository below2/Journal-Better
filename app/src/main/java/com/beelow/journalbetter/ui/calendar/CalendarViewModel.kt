package com.beelow.journalbetter.ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class CalendarUiState(
    val calendarMonthOffset: Int = 0
)

class CalendarViewModel: ViewModel() {
    var uiState by mutableStateOf(CalendarUiState())
        private set

    fun onSetPreviousMonth() {
        uiState = uiState.copy(
            calendarMonthOffset = uiState.calendarMonthOffset - 1
        )
    }

    fun onSetNextMonth() {
        uiState = uiState.copy(
            calendarMonthOffset = uiState.calendarMonthOffset + 1
        )
    }
}