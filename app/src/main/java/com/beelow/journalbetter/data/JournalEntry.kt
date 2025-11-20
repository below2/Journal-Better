package com.beelow.journalbetter.data

import java.time.LocalDateTime
import java.util.UUID

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: LocalDateTime,
    val text: String = "",
    val isHidden: Boolean = false,
    var isSelected: Boolean = false
)

// TODO: add more bulk operations like highlight
enum class BulkOperation {
    DELETE, HIDE, NOTHING
}