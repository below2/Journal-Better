package com.beelow.journalbetter.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [
        // Entries are always loaded per day, and sorted by creation time elsewhere.
        Index(value = ["date"]),
        Index(value = ["created_at"])
    ]
)
data class JournalEntryEntity(
    @PrimaryKey
    val id: String,
    // yyyy-MM-dd, matches the route argument used by navigation
    val date: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    val text: String,
    val hidden: Boolean
)