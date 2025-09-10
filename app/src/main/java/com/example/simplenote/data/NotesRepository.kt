package com.example.simplenote.data

import com.example.simplenote.data.remote.NotesApi
import com.example.simplenote.ui.components.NoteCardColorType
import com.example.simplenote.ui.components.NoteUiModel
import retrofit2.Response

class NotesRepository(private val api: NotesApi, private val tokenProvider: () -> String) {
    suspend fun getNotes(): List<NoteUiModel> {
        val token = "Bearer ${tokenProvider()}"
        val response: Response<com.example.simplenote.data.remote.PaginatedNoteListResponse> = api.getNotes(token)
        return if (response.isSuccessful) {
            response.body()?.results?.mapIndexed { idx, note ->
                NoteUiModel(
                    title = note.title,
                    description = note.description,
                    colorType = when (idx % 3) {
                        0 -> NoteCardColorType.YELLOW
                        1 -> NoteCardColorType.PEACH
                        else -> NoteCardColorType.ROSE
                    }
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun searchNotes(query: String): List<NoteUiModel> {
        val token = "Bearer ${tokenProvider()}"
        val response: Response<com.example.simplenote.data.remote.PaginatedNoteListResponse> = api.filterNotes(token, title = query, description = query)
        return if (response.isSuccessful) {
            response.body()?.results?.mapIndexed { idx, note ->
                NoteUiModel(
                    title = note.title,
                    description = note.description,
                    colorType = when (idx % 3) {
                        0 -> NoteCardColorType.YELLOW
                        1 -> NoteCardColorType.PEACH
                        else -> NoteCardColorType.ROSE
                    }
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}

