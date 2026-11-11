package com.beelow.journalbetter.di

import android.content.Context
import com.beelow.journalbetter.data.JournalRepository
import com.beelow.journalbetter.data.local.JournalDatabase

// Manual DI is enough for a single-module app; swap for Hilt if the graph grows.
class AppContainer(context: Context) {
    private val database = JournalDatabase.getInstance(context)

    val journalRepository: JournalRepository by lazy {
        JournalRepository(database.journalEntryDao())
    }
}