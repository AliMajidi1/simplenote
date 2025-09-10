package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.NotesRepository
import com.example.simplenote.ui.components.NoteUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _notes = MutableStateFlow<List<NoteUiModel>>(emptyList())
    val notes: StateFlow<List<NoteUiModel>> = _notes

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = notesRepository.getNotes()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _notes.value = notesRepository.getNotes()
            } else {
                _notes.value = notesRepository.searchNotes(query)
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadNotes()
    }
}

