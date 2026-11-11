package com.beelow.journalbetter.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    // Oldest first so new entries append to the bottom of a day's list.
    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY created_at ASC")
    fun observeByDate(date: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries ORDER BY created_at DESC")
    fun observeAll(): Flow<List<JournalEntryEntity>>

    // Hidden entries are excluded so search can't leak text the user chose to conceal.
    // The caller is responsible for escaping %, _ and \ in the query.
    @Query(
        """
        SELECT * FROM journal_entries
        WHERE hidden = 0 AND text LIKE '%' || :query || '%' ESCAPE '\'
        ORDER BY created_at DESC
        """
    )
    fun search(query: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getById(id: String): JournalEntryEntity?

    @Upsert
    suspend fun upsert(entry: JournalEntryEntity)

    // Plain UPDATE (not an upsert) so a late debounced save for an entry that was just
    // deleted is a no-op instead of resurrecting the row.
    @Query("UPDATE journal_entries SET text = :text, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateText(id: String, text: String, updatedAt: Long)

    @Query("UPDATE journal_entries SET hidden = :hidden WHERE id = :id")
    suspend fun setHidden(id: String, hidden: Boolean)

    @Query("UPDATE journal_entries SET hidden = :hidden WHERE id IN (:ids)")
    suspend fun setHiddenForIds(ids: Collection<String>, hidden: Boolean)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM journal_entries WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: Collection<String>)
}