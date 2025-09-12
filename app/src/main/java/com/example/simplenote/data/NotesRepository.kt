package com.example.simplenote.data

import com.example.simplenote.data.local.NoteDao
import com.example.simplenote.data.local.NoteEntity
import com.example.simplenote.data.local.toEntity
import com.example.simplenote.data.local.toNote
import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import com.example.simplenote.data.remote.NotesApi

class NotesRepository(private val notesApi: NotesApi, private val noteDao: NoteDao) {
    private var isOffline: Boolean = true

    suspend fun getNotes(page: Int? = null, pageSize: Int? = null): List<Note> {
        if (isOffline) {
            val actualPage = page ?: 1
            val actualPageSize = pageSize ?: 10
            val offset = (actualPage - 1) * actualPageSize
            return noteDao.getAllNotes(actualPageSize, offset).map { it.toNote() }
        }
        return try {
            val response = notesApi.getNotes(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.results?.map { note ->
                    val entity = note.toEntity()
                    noteDao.insertNote(entity)
                    note
                } ?: emptyList()
            } else if (response.code() == 404) {
                emptyList()
            } else {
                isOffline = true
                val actualPage = page ?: 1
                val actualPageSize = pageSize ?: 10
                val offset = (actualPage - 1) * actualPageSize
                noteDao.getAllNotes(actualPageSize, offset).map { it.toNote() }
            }
        } catch (e: Exception) {
            isOffline = true
            val actualPage = page ?: 1
            val actualPageSize = pageSize ?: 10
            val offset = (actualPage - 1) * actualPageSize
            noteDao.getAllNotes(actualPageSize, offset).map { it.toNote() }
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
        if (isOffline) {
            val actualPage = page ?: 1
            val actualPageSize = pageSize ?: 10
            val offset = (actualPage - 1) * actualPageSize
            return noteDao.filterNotes(title, description, actualPageSize, offset).map { it.toNote() }
        }
        return try {
            val response = notesApi.filterNotes(title, description, updatedGte, updatedLte, page, pageSize)
            if (response.isSuccessful) {
                response.body()?.results?.map { note ->
                    val entity = note.toEntity()
                    noteDao.insertNote(entity)
                    note
                } ?: emptyList()
            } else if (response.code() == 404) {
                emptyList()
            } else {
                isOffline = true
                val actualPage = page ?: 1
                val actualPageSize = pageSize ?: 10
                val offset = (actualPage - 1) * actualPageSize
                noteDao.filterNotes(title, description, actualPageSize, offset).map { it.toNote() }
            }
        } catch (e: Exception) {
            isOffline = true
            val actualPage = page ?: 1
            val actualPageSize = pageSize ?: 10
            val offset = (actualPage - 1) * actualPageSize
            noteDao.filterNotes(title, description, actualPageSize, offset).map { it.toNote() }
        }
    }

    suspend fun createNote(note: NoteRequest): Note? {
        if (isOffline) {
            val entity = NoteEntity(
                title = note.title,
                content = note.description,
                syncAction = "CREATE"
            )
            val id = noteDao.insertNote(entity)
            return noteDao.getNoteById(id)?.toNote()
        }
        return try {
            val response = notesApi.createNote(note)
            if (response.isSuccessful) {
                val createdNote = response.body()!!
                noteDao.insertNote(createdNote.toEntity())
                createdNote
            } else {
                isOffline = true
                null
            }
        } catch (e: Exception) {
            isOffline = true
            val entity = NoteEntity(
                title = note.title,
                content = note.description,
                syncAction = "CREATE"
            )
            val id = noteDao.insertNote(entity)
            noteDao.getNoteById(id)?.toNote()
        }
    }

    suspend fun getNote(id: Int): Note? {
        if (isOffline) {
            return noteDao.getNoteByRemoteId(id)?.toNote()
        }
        return try {
            val response = notesApi.getNote(id)
            if (response.isSuccessful) {
                val note = response.body()!!
                noteDao.insertNote(note.toEntity())
                note
            } else {
                isOffline = true
                noteDao.getNoteByRemoteId(id)?.toNote()
            }
        } catch (e: Exception) {
            isOffline = true
            noteDao.getNoteByRemoteId(id)?.toNote()
        }
    }

    suspend fun updateNote(id: Int, note: NoteRequest): Note? {
        if (isOffline) {
            val entity = noteDao.getNoteByRemoteId(id)?.copy(
                title = note.title,
                content = note.description,
                syncAction = "UPDATE"
            )
            if (entity != null) {
                noteDao.insertNote(entity)
                return entity.toNote()
            }
            return null
        }
        return try {
            val response = notesApi.updateNote(id, note)
            if (response.isSuccessful) {
                val updatedNote = response.body()!!
                noteDao.insertNote(updatedNote.toEntity())
                updatedNote
            } else {
                isOffline = true
                null
            }
        } catch (e: Exception) {
            isOffline = true
            val entity = noteDao.getNoteByRemoteId(id)?.copy(
                title = note.title,
                content = note.description,
                syncAction = "UPDATE"
            )
            if (entity != null) {
                noteDao.insertNote(entity)
                entity.toNote()
            } else {
                null
            }
        }
    }

    suspend fun deleteNote(id: Int): Boolean {
        if (isOffline) {
            noteDao.markNoteForSync(id, "DELETE")
            return true
        }
        return try {
            val response = notesApi.deleteNote(id)
            if (response.isSuccessful) {
                noteDao.deleteNoteByRemoteId(id)
                true
            } else {
                isOffline = true
                false
            }
        } catch (e: Exception) {
            isOffline = true
            noteDao.markNoteForSync(id, "DELETE")
            true
        }
    }

    suspend fun syncPendingNotes() {
        val pendingNotes = noteDao.getNotesWithSyncAction()
        var allSuccess = true
        for (note in pendingNotes) {
            try {
                when (note.syncAction) {
                    "CREATE" -> {
                        val request = NoteRequest(
                            title = note.title,
                            description = note.content
                        )
                        val response = notesApi.createNote(request)
                        if (response.isSuccessful) {
                            val createdNote = response.body()!!
                            val updatedEntity = note.copy(
                                remoteId = createdNote.id,
                                remoteUpdatedAt = createdNote.updatedAt,
                                remoteCreatedAt = createdNote.createdAt,
                                syncAction = null
                            )
                            noteDao.insertNote(updatedEntity)
                        } else {
                            allSuccess = false
                        }
                    }
                    "UPDATE" -> {
                        val remoteId = note.remoteId
                        if (remoteId != null) {
                            val request = NoteRequest(
                                title = note.title,
                                description = note.content
                            )
                            val response = notesApi.updateNote(remoteId, request)
                            if (response.isSuccessful) {
                                val updatedEntity = note.copy(
                                    remoteUpdatedAt = response.body()?.updatedAt,
                                    syncAction = null
                                )
                                noteDao.insertNote(updatedEntity)
                            } else {
                                allSuccess = false
                            }
                        } else {
                            allSuccess = false
                        }
                    }
                    "DELETE" -> {
                        val remoteId = note.remoteId
                        if (remoteId != null) {
                            val response = notesApi.deleteNote(remoteId)
                            if (response.isSuccessful) {
                                noteDao.deleteNoteByRemoteId(remoteId)
                            } else {
                                allSuccess = false
                            }
                        } else {
                            note.localId?.let { noteDao.deleteNoteByLocalId(it) }
                        }
                    }
                }
            } catch (_: Exception) {
                allSuccess = false
            }
        }
        isOffline = !allSuccess
    }
}
