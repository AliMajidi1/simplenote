package com.example.simplenote.data.model

import com.google.gson.annotations.SerializedName

data class Note(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("creator_name") val creatorName: String,
    @SerializedName("creator_username") val creatorUsername: String
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
