package com.example.simplenote.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["remoteId"], unique = true),
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int? = null,
    val title: String,
    val content: String,
    val remoteId: Int? = null,
    val remoteUpdatedAt: String? = null,
    val syncAction: String? = null,
)

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}