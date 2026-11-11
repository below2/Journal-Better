package com.beelow.journalbetter.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.beelow.journalbetter.JournalApplication
import com.beelow.journalbetter.ui.entries.EntriesViewModel
import com.beelow.journalbetter.ui.entryDetails.EntryDetailsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { EntriesViewModel(journalApplication().container.journalRepository) }
        initializer { EntryDetailsViewModel(journalApplication().container.journalRepository) }
    }
}

private fun CreationExtras.journalApplication(): JournalApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JournalApplication