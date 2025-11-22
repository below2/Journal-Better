package com.beelow.journalbetter.ui.entries.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EntryCardContent(
    text: String,
    timestamp: Date,
    isHidden: Boolean,
    inSelectMode: Boolean,
    isSelected: Boolean,
    focusRequester: FocusRequester,
    shouldRequestFocus: Boolean,
    onTextChange: (String) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFocusHandled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (inSelectMode && isSelected) MaterialTheme.colorScheme.surfaceBright
                else MaterialTheme.colorScheme.surface
            )
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Timestamp
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault())
            Text(
                text = dateFormatter.format(timestamp),
                modifier = Modifier.padding(end = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Entry text field
            if (!isHidden) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable()
                        .focusRequester(focusRequester),
                    label = { Text("Entry") },
                    shape = RoundedCornerShape(8.dp),
                    readOnly = inSelectMode,
                    enabled = !inSelectMode
                )

                // Handle Focus Request
                LaunchedEffect(shouldRequestFocus) {
                    if (shouldRequestFocus) {
                        focusRequester.requestFocus()
                        onFocusHandled()
                    }
                }
            }
        }
    }
}