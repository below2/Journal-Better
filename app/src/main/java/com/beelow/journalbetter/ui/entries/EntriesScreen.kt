package com.beelow.journalbetter.ui.entries

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.beelow.journalbetter.data.BulkOperation
import com.beelow.journalbetter.ui.entries.components.JournalEntryItem
import com.beelow.journalbetter.util.getDayHeader

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesScreen(
    date: String?,
    quickNote: String?,
    navController: NavController,
    viewModel: EntriesViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val formattedDate = getDayHeader(date)

    LaunchedEffect(quickNote) {
        viewModel.onQuickNote(quickNote)
    }

    BackHandler(
        enabled = uiState.inSelectMode,
    ) {
        viewModel.onBackInSelectMode()
    }

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
                ),
                actions = {
                    IconButton(onClick = {
                        if (uiState.inSelectMode) {
                            viewModel.onApplyBulkOperation()
                        } else {
                            viewModel.onToggleEntriesMenu(true)
                        }
                    }) {
                        Icon(
                            if (uiState.inSelectMode) Icons.Filled.Check else Icons.Filled.Menu,
                            contentDescription = if (uiState.inSelectMode) "Apply" else "Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.expandEntriesMenu,
                        onDismissRequest = { viewModel.onToggleEntriesMenu(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { viewModel.onBulkOperation(BulkOperation.DELETE) }
                        )
                        DropdownMenuItem(
                            text = { Text("Hide") },
                            onClick = { viewModel.onBulkOperation(BulkOperation.HIDE) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.inSelectMode) {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(50),
                    onClick = viewModel::onAddEntry
                ) {
                    Icon(Icons.Filled.Add, "Add new journal entry")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.entries, key = { it.id }) { entry ->
                JournalEntryItem(
                    entry = entry,
                    onEntryTextChange = { newText -> viewModel.onEntryTextChange(entry, newText) },
                    onDeleteEntry = { viewModel.onDeleteEntry(entry) },
                    onToggleHideStatus = { viewModel.onToggleHideStatus(entry) },
                    focusRequestedEntryId = uiState.entryToFocusId,
                    onFocusRequestHandled = viewModel::onFocusRequestHandled,
                    inSelectMode = uiState.inSelectMode,
                    isSelected = uiState.selectedEntries.contains(entry.id),
                    onSelectEntry = { viewModel.onSelectEntry(entry) }
                )
            }
        }
    }
}