package com.beelow.journalbetter.ui.entries

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beelow.journalbetter.data.JournalEntry
import com.google.firebase.firestore.FieldValue
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

@RequiresApi(Build.VERSION_CODES.O)
class EntriesViewModel : ViewModel() {

    // Initialize variables
    var uiState by mutableStateOf(EntriesUiState())
        private set
    private val repository = EntriesRepository()
    private var entriesJob: Job? = null


    fun observeEntriesForDate(date: String) {
        // TODO: If we are already observing this date, do nothing
        entriesJob?.cancel()

        entriesJob = viewModelScope.launch {
            uiState = uiState.copy(entries = emptyList()) // Optional: Clear list while loading
            repository.getEntriesForDate(date).collect { firestoreList ->
                uiState = uiState.copy(entries = firestoreList)
            }
        }
    }

    // Repository functions
    fun onAddEntry(date: String) {
        val newId = UUID.randomUUID().toString()
        val newEntry = JournalEntry(
            id = newId,
            date = date,
            createdTimestamp = Date(),
            updatedTimestamp = Date()
        )

        uiState = uiState.copy(entryToFocusId = newId)

        repository.addEntry(newEntry)
    }

    fun onQuickNote(quickNote: String?) {
        if (quickNote != null) {
            val newId = UUID.randomUUID().toString()
            val quickNoteEntry = JournalEntry(
                id = newId,
                date = LocalDate.now().toString(),
                createdTimestamp = Date(),
                updatedTimestamp = Date(),
                text = URLDecoder.decode(quickNote, StandardCharsets.UTF_8.toString())
            )

            uiState = uiState.copy(entryToFocusId = newId)

            repository.addEntry(quickNoteEntry)
        }
    }

    fun onEntryTextChange(entry: JournalEntry, newText: String) {
        // Only updating the text and updatedTimestamp of the entry
        val updates = mapOf(
            "text" to newText,
            "updatedTimestamp" to FieldValue.serverTimestamp()
        )
        repository.updateEntry(entry.id, updates)
    }

    fun onDeleteEntry(entry: JournalEntry) {
        repository.deleteEntry(entry.id)
    }

    fun onToggleHideStatus(entry: JournalEntry) {
        repository.updateEntry(entry.id, mapOf("hidden" to !entry.hidden))
    }

    fun onApplyBulkOperation() {
        val selectedIds = uiState.selectedEntries

        when (uiState.selectedMode) {
            BulkOperation.DELETE -> {
                repository.deleteEntries(selectedIds)
            }
            BulkOperation.HIDE -> {
                repository.hideEntries(selectedIds, shouldHide = true)
            }
            BulkOperation.NOTHING -> { }
        }

        // Clear selection state
        uiState = uiState.copy(
            inSelectMode = false,
            selectedEntries = emptySet()
        )
    }

    // UI state functions
    fun onFocusRequestHandled() {
        uiState = uiState.copy(entryToFocusId = null)
    }

    fun onSelectEntry(entry: JournalEntry) {
        val selectedEntries = uiState.selectedEntries.toMutableSet()
        if (selectedEntries.contains(entry.id)) {
            selectedEntries.remove(entry.id)

        } else {
            selectedEntries.add(entry.id)
        }
        uiState = uiState.copy(selectedEntries = selectedEntries)
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