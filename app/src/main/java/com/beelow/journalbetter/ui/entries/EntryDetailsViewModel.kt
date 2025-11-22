package com.beelow.journalbetter.ui.entries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beelow.journalbetter.data.JournalEntry
import kotlinx.coroutines.launch

class EntryDetailsViewModel : ViewModel() {
    private val repository = EntriesRepository()

    var entry by mutableStateOf<JournalEntry?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun loadEntry(entryId: String) {
        viewModelScope.launch {
            isLoading = true
            entry = repository.getEntryById(entryId)
            isLoading = false
        }
    }
}