package com.beelow.journalbetter.ui.entries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beelow.journalbetter.data.JournalEntry
import com.beelow.journalbetter.data.JournalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.Date
import java.util.UUID

data class EntriesUiState(
    val entries: List<JournalEntry> = emptyList(),
    val entryToFocusId: String? = null,
    val inSelectMode: Boolean = false,
    val selectedEntries: Set<String> = emptySet(),
    val selectedMode: BulkOperation = BulkOperation.NOTHING,
    val expandEntriesMenu: Boolean = false
)

class EntriesViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    var uiState by mutableStateOf(EntriesUiState())
        private set

    private var entriesJob: Job? = null
    private var observedDate: String? = null

    // The screen's LaunchedEffect re-runs after rotation while this ViewModel survives,
    // so without this flag a quick note would be inserted again on every recreation.
    private var quickNoteHandled = false

    fun observeEntriesForDate(date: String) {
        if (date == observedDate) return
        observedDate = date

        entriesJob?.cancel()
        entriesJob = viewModelScope.launch {
            repository.observeEntriesForDate(date).collect { entries ->
                uiState = uiState.copy(entries = entries)
            }
        }
    }

    // Repository operations
    fun onAddEntry(date: String) {
        val now = Date()
        val newEntry = JournalEntry(
            id = UUID.randomUUID().toString(),
            date = date,
            createdTimestamp = now,
            updatedTimestamp = now
        )

        uiState = uiState.copy(entryToFocusId = newEntry.id)
        viewModelScope.launch { repository.saveEntry(newEntry) }
    }

    fun onQuickNote(quickNote: String?) {
        if (quickNote == null || quickNoteHandled) return
        quickNoteHandled = true

        val now = Date()
        val entry = JournalEntry(
            id = UUID.randomUUID().toString(),
            date = LocalDate.now().toString(),
            createdTimestamp = now,
            updatedTimestamp = now,
            text = URLDecoder.decode(quickNote, StandardCharsets.UTF_8.toString())
        )

        uiState = uiState.copy(entryToFocusId = entry.id)
        viewModelScope.launch { repository.saveEntry(entry) }
    }

    fun onEntryTextChange(entry: JournalEntry, newText: String) {
        viewModelScope.launch { repository.updateEntryText(entry.id, newText) }
    }

    fun onDeleteEntry(entry: JournalEntry) {
        viewModelScope.launch { repository.deleteEntry(entry.id) }
    }

    fun onToggleHideStatus(entry: JournalEntry) {
        viewModelScope.launch { repository.setEntryHidden(entry.id, !entry.hidden) }
    }

    fun onApplyBulkOperation() {
        val selectedIds = uiState.selectedEntries

        viewModelScope.launch {
            when (uiState.selectedMode) {
                BulkOperation.DELETE -> repository.deleteEntries(selectedIds)
                BulkOperation.HIDE -> repository.hideEntries(selectedIds, hidden = true)
                BulkOperation.NOTHING -> Unit
            }
        }

        uiState = uiState.copy(
            inSelectMode = false,
            selectedEntries = emptySet()
        )
    }

    // UI state
    fun onFocusRequestHandled() {
        uiState = uiState.copy(entryToFocusId = null)
    }

    fun onSelectEntry(entry: JournalEntry) {
        val selected = uiState.selectedEntries.toMutableSet()
        if (!selected.add(entry.id)) selected.remove(entry.id)
        uiState = uiState.copy(selectedEntries = selected)
    }

    fun onBulkOperation(operation: BulkOperation) {
        uiState = uiState.copy(
            selectedMode = operation,
            inSelectMode = true,
            expandEntriesMenu = false
        )
    }

    fun onToggleEntriesMenu(expand: Boolean) {
        uiState = uiState.copy(expandEntriesMenu = expand)
    }

    fun onBackInSelectMode() {
        uiState = uiState.copy(inSelectMode = false)
    }
}