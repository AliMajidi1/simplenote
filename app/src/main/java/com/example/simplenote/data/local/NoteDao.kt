package com.example.simplenote.data.local

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY localUpdatedAt DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE localId = :localId")
    suspend fun getNoteByLocalId(localId: Int): NoteEntity?

    @Query("SELECT * FROM notes WHERE remoteId = :remoteId")
    suspend fun getNoteByRemoteId(remoteId: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE syncAction IS NOT NULL")
    suspend fun getNotesPendingSync(): List<NoteEntity>
}
