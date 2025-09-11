package com.example.simplenote.data.local

import com.example.simplenote.data.model.Note

fun Note.toEntity(): NoteEntity = NoteEntity(
    localId = null,
    title = title,
    content = description,
    remoteId = id,
    remoteUpdatedAt = updatedAt,
    remoteCreatedAt = createdAt,
    syncAction = null
)

fun NoteEntity.toNote(): Note = Note(
    id = remoteId ?: -1,
    title = title,
    description = content,
    createdAt = remoteCreatedAt ?: "",
    updatedAt = remoteUpdatedAt ?: "",
    creatorName = "",
    creatorUsername = ""
)
