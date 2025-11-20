package com.beelow.journalbetter.ui.entries

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.beelow.journalbetter.data.BulkOperation
import com.beelow.journalbetter.data.JournalEntry
import java.time.LocalDateTime

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

    var uiState by mutableStateOf(EntriesUiState())
        private set

    fun onQuickNote(quickNote: String?) {
        if (quickNote != null) {
            val quickNoteEntry = JournalEntry(timestamp = LocalDateTime.now(), text = quickNote)
            uiState = uiState.copy(
                entries = uiState.entries + quickNoteEntry,
                entryToFocusId = quickNoteEntry.id
            )
        }
    }

    fun onAddEntry() {
        val newEntry = JournalEntry(timestamp = LocalDateTime.now())
        uiState = uiState.copy(
            entries = uiState.entries + newEntry,
            entryToFocusId = newEntry.id
        )
    }

    fun onEntryTextChange(entry: JournalEntry, newText: String) {
        val index = uiState.entries.indexOf(entry)
        if (index != -1) {
            val updatedEntries = uiState.entries.toMutableList()
            updatedEntries[index] = entry.copy(text = newText, timestamp = LocalDateTime.now())
            uiState = uiState.copy(entries = updatedEntries)
        }
    }

    fun onDeleteEntry(entry: JournalEntry) {
        uiState = uiState.copy(entries = uiState.entries - entry)
    }

    fun onToggleHideStatus(entry: JournalEntry) {
        val index = uiState.entries.indexOf(entry)
        if (index != -1) {
            val updatedEntries = uiState.entries.toMutableList()
            updatedEntries[index] = entry.copy(isHidden = !entry.isHidden)
            uiState = uiState.copy(entries = updatedEntries)
        }
    }

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

    fun onApplyBulkOperation() {
        when (uiState.selectedMode) {
            BulkOperation.DELETE -> {
                val updatedEntries = uiState.entries.filterNot { uiState.selectedEntries.contains(it.id) }
                uiState = uiState.copy(entries = updatedEntries)
            }
            BulkOperation.HIDE -> {
                val updatedEntries = uiState.entries.map { entry ->
                    if (uiState.selectedEntries.contains(entry.id)) {
                        entry.copy(isHidden = !entry.isHidden)
                    } else {
                        entry
                    }
                }
                uiState = uiState.copy(entries = updatedEntries)
            }
            BulkOperation.NOTHING -> {}
        }
        uiState = uiState.copy(
            inSelectMode = false,
            selectedEntries = emptySet()
        )
    }
}