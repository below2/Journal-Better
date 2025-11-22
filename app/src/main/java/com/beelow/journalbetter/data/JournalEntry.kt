package com.beelow.journalbetter.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class JournalEntry(
    @DocumentId
    val id: String = "",
    val date: String? = null, // yyyy-mm-dd
    @ServerTimestamp
    val createdTimestamp: Date? = null,
    @ServerTimestamp
    val updatedTimestamp: Date? = null,
    val text: String = "",
    val hidden: Boolean = false,

    @get:Exclude
    var isSelected: Boolean = false
)