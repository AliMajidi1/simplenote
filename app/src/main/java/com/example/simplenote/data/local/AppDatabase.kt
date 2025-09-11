package com.example.simplenote.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val remoteId: Int? = null,
    val remoteUpdatedAt: Long? = null,
    val localUpdatedAt: Long = System.currentTimeMillis()
)
