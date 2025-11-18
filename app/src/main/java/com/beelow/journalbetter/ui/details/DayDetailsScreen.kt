package com.beelow.journalbetter.ui.details

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

private fun getDayNumberSuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: LocalDateTime,
    var text: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailsScreen(date: String?, quickNote: String?, navController: NavController) {
    // Format the date header
    val formattedDate = if (date != null) {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d"))
        val day = parsedDate.dayOfMonth
        val monthName = parsedDate.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
        val dayWithSuffix = "$day${getDayNumberSuffix(day)}"
        "$monthName $dayWithSuffix, ${parsedDate.year}"
    } else {
        "Details"
    }

    val journalEntries = remember { mutableStateListOf<JournalEntry>() }
    var entryToFocusId by remember { mutableStateOf<String?>(null) }

    // Add quick note as an entry if provided
    quickNote?.let { journalEntries.add(JournalEntry(timestamp = LocalDateTime.now(), text = it)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = formattedDate) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(50),
                onClick = {
                    val newEntry = JournalEntry(timestamp = LocalDateTime.now())
                    journalEntries.add(newEntry)
                    entryToFocusId = newEntry.id
                }) {
                Icon(Icons.Filled.Add, "Add new journal entry")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(journalEntries, key = { it.id }) { entry ->
                    val itemFocusRequester = remember { FocusRequester() }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                    ) {
                        // Timestamp
                        Text(
                            text = entry.timestamp.format(DateTimeFormatter.ofPattern("yyyy/MM/dd h:mm a")),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Entry text field
                        OutlinedTextField(
                            value = entry.text,
                            onValueChange = {
                                journalEntries.indexOf(entry).let { index ->
                                    journalEntries[index] = entry.copy(text = it, timestamp = LocalDateTime.now())
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusable()
                                .focusRequester(itemFocusRequester),
                            label = { Text("Entry") },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Divider
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )

                    // Focus on new entry
                    LaunchedEffect(entry.id) {
                        if (entry.id == entryToFocusId) {
                            itemFocusRequester.requestFocus()
                            entryToFocusId = null // Clear the focus request
                        }
                    }
                }
            }
        }
    }
}