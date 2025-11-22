package com.beelow.journalbetter.ui.entries

import android.util.Log
import com.beelow.journalbetter.data.JournalEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class EntriesRepository {

    // Get current user ID
    private val userId = Firebase.auth.currentUser?.uid

    // Reference to: users/{userId}/entries
    private val entriesRef = userId?.let {
        Firebase.firestore.collection("users").document(it).collection("entries")
    }

    // READ: Get entries for a specific date
    fun getEntriesForDate(dateString: String): Flow<List<JournalEntry>> = callbackFlow {
        println(dateString)
        if (entriesRef == null) {
            close(Exception("User not logged in"))
            return@callbackFlow
        }

        val subscription: ListenerRegistration = entriesRef
            .whereEqualTo("date", dateString) // yyyy-mm-dd
            .orderBy("createdTimestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching entries", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val entries = snapshot.toObjects(JournalEntry::class.java)
                    trySend(entries)
                }
            }

        awaitClose { subscription.remove() }
    }

    // READ: Get a single entry by ID
    suspend fun getEntryById(entryId: String): JournalEntry? {
        return try {
            val snapshot = entriesRef?.document(entryId)?.get()?.await()
            snapshot?.toObject(JournalEntry::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // CREATE: Add a new entry
    fun addEntry(entry: JournalEntry) {
        if (entry.id.isNotEmpty()) {
            entriesRef?.document(entry.id)?.set(entry)
        } else {
            entriesRef?.add(entry)
        }
    }

    // UPDATE: Update entry (text/hidden)
    fun updateEntry(entryId: String, updates: Map<String, Any>) {
        entriesRef?.document(entryId)?.update(updates)
    }

    // DELETE: Remove a single entry
    fun deleteEntry(entryId: String) {
        entriesRef?.document(entryId)?.delete()
    }

    // BATCH: Handle bulk operations
    // Delete
    fun deleteEntries(entryIds: Set<String>) {
        val batch = Firebase.firestore.batch()
        entryIds.forEach { id ->
            val docRef = entriesRef?.document(id)
            if (docRef != null) {
                batch.delete(docRef)
            }
        }
        batch.commit()
    }
    // Hide
    fun hideEntries(entryIds: Set<String>, shouldHide: Boolean) {
        val batch = Firebase.firestore.batch()
        entryIds.forEach { id ->
            val docRef = entriesRef?.document(id)
            if (docRef != null) {
                batch.update(docRef, "hidden", shouldHide)
            }
        }
        batch.commit()
    }
}