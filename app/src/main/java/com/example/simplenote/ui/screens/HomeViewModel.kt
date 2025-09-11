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
    data class Success(val notes: List<Note>, val isLoadingMore: Boolean = false, val endReached: Boolean = false) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var isLoadingMore = false
    private var lastQuery: String? = null
    private val pageSize = 10
    private var allNotes: MutableList<Note> = mutableListOf()
    private var endReached = false

    init {
        loadNotes(reset = true)
    }

    fun loadNotes(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                currentPage = 1
                allNotes.clear()
                endReached = false
                _uiState.value = HomeUiState.Loading
            } else {
                isLoadingMore = true
                _uiState.value = HomeUiState.Success(allNotes, isLoadingMore = true, endReached = false)
            }
            try {
                val notes = notesRepository.getNotes(page = currentPage, pageSize = pageSize)
                if (reset) allNotes.clear()
                allNotes.addAll(notes)
                endReached = notes.size < pageSize
                _uiState.value = if (allNotes.isEmpty()) HomeUiState.Empty else HomeUiState.Success(allNotes, isLoadingMore = false, endReached = endReached)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore) return
        if (endReached) return
        currentPage++
        val query = lastQuery ?: ""
        if (query.isBlank()) {
            loadNotes(reset = false)
        } else {
            searchNotes(query, append = true)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        lastQuery = query
        currentPage = 1
        allNotes.clear()
        endReached = false
        searchNotes(query, append = false)
    }

    private fun searchNotes(query: String, append: Boolean) {
        viewModelScope.launch {
            if (!append) {
                _uiState.value = HomeUiState.Loading
                allNotes.clear()
                currentPage = 1
                endReached = false
            } else {
                isLoadingMore = true
                _uiState.value = HomeUiState.Success(allNotes, isLoadingMore = true, endReached = false)
            }
            try {
                val notes = notesRepository.filterNotes(title = query, page = currentPage, pageSize = pageSize)
                if (!append) allNotes.clear()
                allNotes.addAll(notes)
                endReached = notes.size < pageSize
                _uiState.value = if (allNotes.isEmpty()) HomeUiState.Empty else HomeUiState.Success(allNotes, isLoadingMore = false, endReached = endReached)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            } finally {
                isLoadingMore = false
            }
        }
    }
}
