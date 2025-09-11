package com.example.simplenote.data

import com.example.simplenote.data.local.NoteDao
import com.example.simplenote.data.local.NoteEntity
import com.example.simplenote.data.local.toEntity
import com.example.simplenote.data.local.toNote
import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import com.example.simplenote.data.remote.NotesApi

class NotesRepository(private val notesApi: NotesApi, private val noteDao: NoteDao) {
    suspend fun getNotes(page: Int? = null, pageSize: Int? = null): List<Note> {
        return try {
            val response = notesApi.getNotes(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.results?.map { note ->
                    val entity = note.toEntity()
                    noteDao.insertNote(entity)
                    note
                } ?: emptyList()
            } else {
                noteDao.getAllNotes().map { it.toNote() }
            }
        } catch (e: Exception) {
            noteDao.getAllNotes().map { it.toNote() }
        }
    }

    suspend fun filterNotes(
        title: String? = null,
        description: String? = null,
        updatedGte: String? = null,
        updatedLte: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): List<Note> {
        return try {
            val response = notesApi.filterNotes(title, description, updatedGte, updatedLte, page, pageSize)
            if (response.isSuccessful) {
                response.body()?.results?.map { note ->
                    val entity = note.toEntity()
                    noteDao.insertNote(entity)
                    note
                } ?: emptyList()
            } else {
                noteDao.filterNotes(title, description).map { it.toNote() }
            }
        } catch (e: Exception) {
            noteDao.filterNotes(title, description).map { it.toNote() }
        }
    }

    suspend fun createNote(note: NoteRequest): Note? {
        return try {
            val response = notesApi.createNote(note)
            if (response.isSuccessful) {
                val createdNote = response.body()!!
                noteDao.insertNote(createdNote.toEntity())
                createdNote
            } else {
                null
            }
        } catch (e: Exception) {
            val entity = NoteEntity(
                title = note.title,
                content = note.description,
                syncAction = "CREATE"
            )
            noteDao.insertNote(entity)
            null
        }
    }

    suspend fun getNote(id: Int): Note? {
        return try {
            val response = notesApi.getNote(id)
            if (response.isSuccessful) {
                val note = response.body()!!
                noteDao.insertNote(note.toEntity())
                note
            } else {
                noteDao.getNoteByRemoteId(id)?.toNote()
            }
        } catch (e: Exception) {
            noteDao.getNoteByRemoteId(id)?.toNote()
        }
    }

    suspend fun updateNote(id: Int, note: NoteRequest): Note? {
        return try {
            val response = notesApi.updateNote(id, note)
            if (response.isSuccessful) {
                val updatedNote = response.body()!!
                noteDao.insertNote(updatedNote.toEntity())
                updatedNote
            } else {
                null
            }
        } catch (e: Exception) {
            val entity = noteDao.getNoteByRemoteId(id)?.copy(
                title = note.title,
                content = note.description,
                syncAction = "UPDATE"
            )
            if (entity != null) {
                noteDao.insertNote(entity)
            }
            null
        }
    }

    suspend fun deleteNote(id: Int): Boolean {
        return try {
            val response = notesApi.deleteNote(id)
            if (response.isSuccessful) {
                noteDao.deleteNoteByRemoteId(id)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            noteDao.markNoteForSync(id, "DELETE")
            false
        }
    }
}
