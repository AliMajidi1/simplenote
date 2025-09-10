package com.example.simplenote.data.model

import com.squareup.moshi.Json

data class Note(
    val id: Int,
    val title: String,
    val description: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "creator_name") val creatorName: String,
    @Json(name = "creator_username") val creatorUsername: String
)

data class NoteRequest(
    val title: String,
    val description: String
)

data class PaginatedNoteList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Note>
)

