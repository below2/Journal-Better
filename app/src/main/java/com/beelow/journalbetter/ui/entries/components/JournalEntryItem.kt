package com.beelow.journalbetter.ui.entries.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.beelow.journalbetter.R
import com.beelow.journalbetter.data.JournalEntry
import kotlinx.coroutines.delay
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JournalEntryItem(
    navController: NavController,
    entry: JournalEntry,
    onEntryTextChange: (String) -> Unit,
    onDeleteEntry: () -> Unit,
    onToggleHideStatus: () -> Unit,
    focusRequestedEntryId: String?,
    onFocusRequestHandled: () -> Unit,
    inSelectMode: Boolean,
    isSelected: Boolean,
    onSelectEntry: () -> Unit
) {
    // Local state for debounce logic
    var localText by remember(entry.id) { mutableStateOf(entry.text) }
    var localUpdatedTimestamp by remember(entry.id) {
        mutableStateOf(entry.updatedTimestamp ?: entry.createdTimestamp ?: Date())
    }
    val focusRequester = remember { FocusRequester() }

    // Debounce sync
    LaunchedEffect(localText) {
        if (localText != entry.text) {
            delay(1000)
            onEntryTextChange(localText)
        }
    }

    // Sync timestamp from server
    LaunchedEffect(entry.updatedTimestamp) {
        if (entry.updatedTimestamp != null) {
            localUpdatedTimestamp = entry.updatedTimestamp
        }
    }

    // Save on dispose
    val currentOnEntryTextChange by rememberUpdatedState(onEntryTextChange)
    val currentLocalText by rememberUpdatedState(localText)
    val currentEntryText by rememberUpdatedState(entry.text)

    // Sync on dispose
    DisposableEffect(Unit) {
        onDispose {
            if (currentLocalText != currentEntryText) {
                currentOnEntryTextChange(currentLocalText)
            }
        }
    }

    // Swipe logic
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { it != SwipeToDismissBoxValue.StartToEnd }
    )

    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
            onToggleHideStatus()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteEntry()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.secondaryContainer   // Hide
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer       // Delete
                    else -> Color.Transparent
                }
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f
            )
            // Swipe UI
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        if (entry.hidden) painterResource(R.drawable.visibility_off_24px) else painterResource(R.drawable.visibility_24px),
                        contentDescription = "Hide/Unhide",
                        modifier = Modifier.scale(scale)
                    )
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.scale(scale)
                    )
                }
            }
        }
    ) {
        // Entry UI
        EntryCardContent(
            text = localText,
            timestamp = localUpdatedTimestamp,
            isHidden = entry.hidden,
            inSelectMode = inSelectMode,
            isSelected = isSelected,
            focusRequester = focusRequester,
            shouldRequestFocus = (entry.id == focusRequestedEntryId),
            onTextChange = {
                localText = it
                localUpdatedTimestamp = Date()
            },
            onClick = onSelectEntry,
            onLongClick = {
                if (!inSelectMode) navController.navigate("entryDetails/${entry.id}")
            },
            onFocusHandled = onFocusRequestHandled
        )
    }
}