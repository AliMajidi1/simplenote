package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.NotesRepository
import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NoteEditUiState {
    object Loading : NoteEditUiState()
    object Saving : NoteEditUiState()
    object Deleting : NoteEditUiState()
    data class Success(val note: Note?) : NoteEditUiState()
    data class Error(val message: String) : NoteEditUiState()
}

class NoteEditViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<NoteEditUiState>(NoteEditUiState.Loading)
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    private val _lastEdited = MutableStateFlow<String?>(null)
    val lastEdited: StateFlow<String?> = _lastEdited.asStateFlow()
    private var noteId: Int? = null

    fun loadNote(id: Int?) {
        noteId = id
        if (id == null) {
            _uiState.value = NoteEditUiState.Success(null)
            _title.value = ""
            _description.value = ""
            _lastEdited.value = null
            return
        }
        _uiState.value = NoteEditUiState.Loading
        viewModelScope.launch {
            try {
                val note = notesRepository.getNote(id)
                if (note != null) {
                    _title.value = note.title
                    _description.value = note.description
                    _lastEdited.value = note.updatedAt
                    _uiState.value = NoteEditUiState.Success(note)
                } else {
                    _uiState.value = NoteEditUiState.Error("Note not found")
                }
            } catch (e: Exception) {
                _uiState.value = NoteEditUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onDescriptionChange(newDesc: String) {
        _description.value = newDesc
    }

    fun saveNote(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val title = _title.value.trim()
        val desc = _description.value.trim()
        if (title.isBlank()) {
            onSuccess()
            return
        }
        _uiState.value = NoteEditUiState.Saving
        viewModelScope.launch {
            try {
                val request = NoteRequest(title, desc)
                val note = if (noteId == null) {
                    notesRepository.createNote(request)
                } else {
                    notesRepository.updateNote(noteId!!, request)
                }
                if (note != null) {
                    _uiState.value = NoteEditUiState.Success(note)
                    onSuccess()
                } else {
                    _uiState.value = NoteEditUiState.Error("Failed to save note")
                    onError("Failed to save note")
                }
            } catch (e: Exception) {
                _uiState.value = NoteEditUiState.Error(e.message ?: "Unknown error")
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteNote(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val id = noteId ?: return
        _uiState.value = NoteEditUiState.Deleting
        viewModelScope.launch {
            try {
                val success = notesRepository.deleteNote(id)
                if (success) {
                    _uiState.value = NoteEditUiState.Success(null)
                    onSuccess()
                } else {
                    _uiState.value = NoteEditUiState.Error("Failed to delete note")
                    onError("Failed to delete note")
                }
            } catch (e: Exception) {
                _uiState.value = NoteEditUiState.Error(e.message ?: "Unknown error")
                onError(e.message ?: "Unknown error")
            }
        }
    }
}
