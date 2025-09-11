package com.example.simplenote.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY localUpdatedAt DESC")
    fun getAllNotesFlow(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getNoteByRemoteId(remoteId: Int): NoteEntity?

    @Query("SELECT * FROM notes WHERE localId = :localId LIMIT 1")
    suspend fun getNoteByLocalId(localId: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, syncAction = :action, localUpdatedAt = :updatedAt WHERE id = :id")
    suspend fun markDeleted(id: String, action: String, updatedAt: Long)

    @Query("SELECT * FROM notes WHERE (isSynced = 0 OR isDeleted = 1)")
    suspend fun getPendingSyncNotes(): List<NoteEntity>

    @Query("DELETE FROM notes WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun purgeDeletedNotes()
}
