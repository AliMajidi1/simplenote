package com.example.simplenote.data.remote

import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import com.example.simplenote.data.model.PaginatedNoteList
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {
    @GET("/api/notes/")
    suspend fun getNotes(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<PaginatedNoteList>

    @GET("/api/notes/filter")
    suspend fun filterNotes(
        @Query("title") title: String? = null,
        @Query("description") description: String? = null,
        @Query("updated__gte") updatedGte: String? = null,
        @Query("updated__lte") updatedLte: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<PaginatedNoteList>

    @POST("/api/notes/")
    suspend fun createNote(@Body note: NoteRequest): Response<Note>

    @GET("/api/notes/{id}/")
    suspend fun getNote(@Path("id") id: Int): Response<Note>

    @PUT("/api/notes/{id}/")
    suspend fun updateNote(@Path("id") id: Int, @Body note: NoteRequest): Response<Note>

    @PATCH("/api/notes/{id}/")
    suspend fun patchNote(@Path("id") id: Int, @Body note: Map<String, Any?>): Response<Note>

    @DELETE("/api/notes/{id}/")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>
}
