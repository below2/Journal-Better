package com.beelow.journalbetter.data.local

import com.beelow.journalbetter.data.JournalEntry
import java.util.Date

fun JournalEntryEntity.toDomain(): JournalEntry = JournalEntry(
    id = id,
    date = date,
    createdTimestamp = Date(createdAt),
    updatedTimestamp = Date(updatedAt),
    text = text,
    hidden = hidden
)

fun JournalEntry.toEntity(): JournalEntryEntity = JournalEntryEntity(
    id = id,
    date = date,
    createdAt = createdTimestamp.time,
    updatedAt = updatedTimestamp.time,
    text = text,
    hidden = hidden
)