package com.beelow.journalbetter.ui.calendar.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.beelow.journalbetter.data.CalendarDay
import com.beelow.journalbetter.util.getCalendarData

@Composable
fun AnimatedCalendarView(
    monthOffset: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.pointerInput(Unit) {
            var totalDrag = 0f
            detectHorizontalDragGestures(
                onDragStart = { totalDrag = 0f },
                onDragEnd = {
                    val threshold = 100f
                    if (totalDrag > threshold) onPreviousMonth()
                    else if (totalDrag < -threshold) onNextMonth()
                }
            ) { change, dragAmount ->
                change.consume()
                totalDrag += dragAmount
            }
        }
    ) {
        AnimatedContent(
            targetState = monthOffset,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }.using(SizeTransform(clip = false))
            },
            label = "CalendarAnimation"
        ) { targetOffset ->
            val (calendarDays, _, numberOfRows) = getCalendarData(targetOffset)

            ElevatedCard(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                CalendarGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    calendarDays = calendarDays,
                    numberOfRows = numberOfRows,
                    onDayClick = onDayClick
                )
            }
        }
    }
}