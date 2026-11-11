package com.beelow.journalbetter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [JournalEntryEntity::class],
    version = 1,
    exportSchema = true
)
abstract class JournalDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        private const val DATABASE_NAME = "journal_better.db"

        @Volatile
        private var instance: JournalDatabase? = null

        fun getInstance(context: Context): JournalDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
    }
}