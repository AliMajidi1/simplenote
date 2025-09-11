package com.example.simplenote.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase

@Entity(tableName = "notes")
data class NoteEntity(
    val title: String,
    val content: String,
    val remoteId: Int? = null,
    val localId: Int? = null,
    val remoteUpdatedAt: String? = null,
    val localUpdatedAt: String? = null,
    val syncAction: String? = null,
)

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}