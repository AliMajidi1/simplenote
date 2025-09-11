package com.example.simplenote.data.local

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY localUpdatedAt DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE isSynced = 0 OR isDeleted = 1")
    suspend fun getPendingSyncNotes(): List<NoteEntity>

    @Query("DELETE FROM notes WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun purgeDeletedNotes()
}
