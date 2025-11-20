package com.beelow.journalbetter.ui.entries.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beelow.journalbetter.data.JournalEntry
import java.time.format.DateTimeFormatter
import com.beelow.journalbetter.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryItem(
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
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            // Allow swipe-to-delete, but prevent swipe-to-hide from dismissing
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )

    // Handle the hide/unhide action
    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
            onToggleHideStatus()
            // Reset the swipe state to snap back
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    // Handle the delete action after dismissal
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteEntry()
        }
    }

    // Swipe box
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,  // Hide
        enableDismissFromEndToStart = true,  // Delete
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.secondaryContainer // Hide
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer   // Delete
                    else -> Color.Transparent
                }
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        if (entry.isHidden) painterResource(R.drawable.visibility_off_24px) else painterResource(R.drawable.visibility_24px),
                        contentDescription = if (entry.isHidden) "Unhide" else "Hide",
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
        // Entry item
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (inSelectMode && isSelected) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surface)
                .clickable(
                    onClick = {
                        onSelectEntry()
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Timestamp
                Text(
                    text = entry.timestamp.format(DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a")),
                    modifier = Modifier.padding(end = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Entry text field
                if (!entry.isHidden) {
                    val itemFocusRequester = remember { FocusRequester() }
                    OutlinedTextField(
                        value = entry.text,
                        onValueChange = onEntryTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable()
                            .focusRequester(itemFocusRequester),
                        label = { Text("Entry") },
                        shape = RoundedCornerShape(8.dp),
                        readOnly = inSelectMode,
                        enabled = !inSelectMode
                    )
                    LaunchedEffect(entry.id) {
                        if (entry.id == focusRequestedEntryId) {
                            itemFocusRequester.requestFocus()
                            onFocusRequestHandled() // Notify parent that focus has been handled
                        }
                    }
                }
            }

            // Divider
//            HorizontalDivider(
//                modifier = Modifier.padding(horizontal = 10.dp)
//            )
        }
    }
}