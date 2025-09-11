package com.example.simplenote.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

/**
 * Local representation of a note stored in Room.
 * Fields added to support offline-first sync:
 * - isSynced: whether this record has been successfully synced to the remote server
 * - isDeleted: soft-delete marker (so deletes can be synced)
 * - remoteId: the server-side integer id (null for locally-created notes not yet pushed)
 * - remoteUpdatedAt: last-updated timestamp from the server (epoch millis)
 * - localUpdatedAt: last-updated timestamp recorded locally (epoch millis)
 * - syncAction: pending action to perform on the server (CREATE/UPDATE/DELETE) when syncing
 * - hasConflict: whether this note currently has a sync conflict between local and remote
 * - localId: optional numeric id used to represent locally-created notes in UI routing (negative integer)
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val remoteId: Int? = null,
    val remoteUpdatedAt: Long? = null,
    val localUpdatedAt: Long = System.currentTimeMillis(),
    val syncAction: String? = null,
    val hasConflict: Boolean = false,
    val localId: Int? = null
)

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}