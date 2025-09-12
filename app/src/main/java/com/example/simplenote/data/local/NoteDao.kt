package com.example.simplenote.data.local

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes LIMIT :pageSize OFFSET :offset")
    suspend fun getAllNotes(pageSize: Int, offset: Int): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE localId = :localId")
    suspend fun getNoteByLocalId(localId: Int): NoteEntity?

    @Query("SELECT * FROM notes WHERE remoteId = :remoteId")
    suspend fun getNoteByRemoteId(remoteId: Int): NoteEntity?

    @Query("SELECT * FROM notes WHERE (:title IS NULL OR title LIKE '%' || :title || '%') AND (:description IS NULL OR content LIKE '%' || :description || '%') LIMIT :pageSize OFFSET :offset")
    suspend fun filterNotes(title: String?, description: String?, pageSize: Int, offset: Int): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE localId = :localId")
    suspend fun deleteNoteByLocalId(localId: Int)

    @Query("DELETE FROM notes WHERE remoteId = :remoteId")
    suspend fun deleteNoteByRemoteId(remoteId: Int)

    @Query("UPDATE notes SET syncAction = :syncAction WHERE remoteId = :remoteId")
    suspend fun markNoteForSync(remoteId: Int, syncAction: String)

    @Query("SELECT * FROM notes WHERE localId = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE syncAction IS NOT NULL")
    suspend fun getNotesWithSyncAction(): List<NoteEntity>
}
