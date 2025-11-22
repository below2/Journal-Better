package com.beelow.journalbetter.ui.entries

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.beelow.journalbetter.ui.entries.components.EntriesTopBar
import com.beelow.journalbetter.ui.entries.components.JournalEntryItem
import com.beelow.journalbetter.util.getDayHeader

// TODO: add more bulk operations like highlight, probably refactor to separate file
enum class BulkOperation {
    DELETE, HIDE, NOTHING
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntriesScreen(
    date: String,
    quickNote: String?,
    navController: NavController,
    viewModel: EntriesViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val listState = rememberLazyListState()

    // Load Data & Scroll Logic
    LaunchedEffect(date) { viewModel.observeEntriesForDate(date) }
    LaunchedEffect(quickNote) { viewModel.onQuickNote(quickNote) }

    LaunchedEffect(uiState.entries, uiState.entryToFocusId) {
        uiState.entryToFocusId?.let { focusId ->
            val index = uiState.entries.indexOfFirst { it.id == focusId }
            if (index >= 0) listState.animateScrollToItem(index)
        }
    }

    // Back button exits select mode
    BackHandler(enabled = uiState.inSelectMode) {
        viewModel.onBackInSelectMode()
    }

    Scaffold(
        topBar = {
            EntriesTopBar(
                title = getDayHeader(date),
                inSelectMode = uiState.inSelectMode,
                isMenuExpanded = uiState.expandEntriesMenu,
                onBackClick = { navController.popBackStack() },
                onMenuClick = { viewModel.onToggleEntriesMenu(true) },
                onDismissMenu = { viewModel.onToggleEntriesMenu(false) },
                onBulkOperation = { op -> viewModel.onBulkOperation(op) },
                onApplySelection = { viewModel.onApplyBulkOperation() }
            )
        },
        floatingActionButton = {
            if (!uiState.inSelectMode) {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(50),
                    onClick = { viewModel.onAddEntry(date) }
                ) {
                    Icon(Icons.Filled.Add, "Add new journal entry")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            items(uiState.entries, key = { it.id }) { entry ->
                JournalEntryItem(
                    navController = navController,
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