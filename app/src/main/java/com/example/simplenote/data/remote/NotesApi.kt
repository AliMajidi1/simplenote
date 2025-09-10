package com.example.simplenote.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Data models for API responses
// You should define NoteResponse elsewhere with id, title, description, etc.
data class NoteResponse(
    val id: Int,
    val title: String,
    val description: String,
    val created_at: String?,
    val updated_at: String?,
    val creator_name: String?,
    val creator_username: String?
)
data class PaginatedNoteListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NoteResponse>
)

interface NotesApi {
    @GET("/api/notes/")
    suspend fun getNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<PaginatedNoteListResponse>

    @GET("/api/notes/filter")
    suspend fun filterNotes(
        @Header("Authorization") token: String,
        @Query("title") title: String? = null,
        @Query("description") description: String? = null,
        @Query("updated__gte") updatedGte: String? = null,
        @Query("updated__lte") updatedLte: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<PaginatedNoteListResponse>
}

