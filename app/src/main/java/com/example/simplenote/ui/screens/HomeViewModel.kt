package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.NotesRepository
import com.example.simplenote.data.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val notes: List<Note>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = notesRepository.getNotes()
                if (response.isSuccessful) {
                    val notes = response.body()?.results ?: emptyList()
                    _uiState.value = if (notes.isEmpty()) HomeUiState.Empty else HomeUiState.Success(notes)
                } else {
                    _uiState.value = HomeUiState.Error("Failed to load notes")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchNotes(query)
    }

    private fun searchNotes(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = notesRepository.filterNotes(title = query)
                if (response.isSuccessful) {
                    val notes = response.body()?.results ?: emptyList()
                    _uiState.value = if (notes.isEmpty()) HomeUiState.Empty else HomeUiState.Success(notes)
                } else {
                    _uiState.value = HomeUiState.Error("Failed to search notes")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
