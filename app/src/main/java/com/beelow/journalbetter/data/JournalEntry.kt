package com.beelow.journalbetter.data

import java.util.Date

data class JournalEntry(
    val id: String,
    val date: String, // yyyy-MM-dd
    val createdTimestamp: Date,
    val updatedTimestamp: Date,
    val text: String = "",
    val hidden: Boolean = false
)