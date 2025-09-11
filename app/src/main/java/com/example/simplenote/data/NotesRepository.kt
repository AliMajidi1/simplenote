package com.example.simplenote.data

import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import com.example.simplenote.data.model.PaginatedNoteList
import com.example.simplenote.data.remote.NotesApi
import retrofit2.Response

class NotesRepository(private val notesApi: NotesApi) {
    suspend fun getNotes(page: Int? = null, pageSize: Int? = null): Response<PaginatedNoteList> =
        notesApi.getNotes(page, pageSize)

    suspend fun filterNotes(
        title: String? = null,
        description: String? = null,
        updatedGte: String? = null,
        updatedLte: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): Response<PaginatedNoteList> =
        notesApi.filterNotes(title, description, updatedGte, updatedLte, page, pageSize)

    suspend fun createNote(note: NoteRequest): Response<Note> =
        notesApi.createNote(note)

    suspend fun getNote(id: Int): Response<Note> =
        notesApi.getNote(id)

    suspend fun updateNote(id: Int, note: NoteRequest): Response<Note> =
        notesApi.updateNote(id, note)

    suspend fun deleteNote(id: Int): Response<Unit> =
        notesApi.deleteNote(id)
}

