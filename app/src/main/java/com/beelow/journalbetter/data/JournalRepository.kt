package com.beelow.journalbetter.data

import com.beelow.journalbetter.data.local.JournalEntryDao
import com.beelow.journalbetter.data.local.toDomain
import com.beelow.journalbetter.data.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Date

class JournalRepository(private val dao: JournalEntryDao) {

    fun observeEntriesForDate(date: String): Flow<List<JournalEntry>> =
        dao.observeByDate(date).map { list -> list.map { it.toDomain() } }

    fun observeAllEntries(): Flow<List<JournalEntry>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun searchEntries(query: String): Flow<List<JournalEntry>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return flowOf(emptyList())
        return dao.search(trimmed.escapeLike()).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getEntryById(entryId: String): JournalEntry? =
        dao.getById(entryId)?.toDomain()

    suspend fun saveEntry(entry: JournalEntry) = dao.upsert(entry.toEntity())

    suspend fun updateEntryText(entryId: String, text: String) =
        dao.updateText(entryId, text, Date().time)

    suspend fun setEntryHidden(entryId: String, hidden: Boolean) =
        dao.setHidden(entryId, hidden)

    suspend fun deleteEntry(entryId: String) = dao.deleteById(entryId)

    suspend fun deleteEntries(entryIds: Set<String>) {
        if (entryIds.isNotEmpty()) dao.deleteByIds(entryIds)
    }

    suspend fun hideEntries(entryIds: Set<String>, hidden: Boolean) {
        if (entryIds.isNotEmpty()) dao.setHiddenForIds(entryIds, hidden)
    }

    // Keeps user input from being interpreted as LIKE wildcards.
    private fun String.escapeLike(): String =
        replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
}