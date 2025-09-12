package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.GeminiRepository
import com.example.simplenote.data.NotesRepository
import com.example.simplenote.data.model.Note
import com.example.simplenote.data.model.NoteRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

sealed class NoteEditUiState {
    object Loading : NoteEditUiState()
    object Saving : NoteEditUiState()
    object Deleting : NoteEditUiState()
    data class Success(val note: Note?) : NoteEditUiState()
    data class Error(val message: String) : NoteEditUiState()
}

class NoteEditViewModel(private val notesRepository: NotesRepository, private val geminiRepository: GeminiRepository) : ViewModel() {
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

    private val _showAiDialog = MutableStateFlow(false)
    val showAiDialog: StateFlow<Boolean> = _showAiDialog.asStateFlow()
    fun setShowAiDialog(show: Boolean) { _showAiDialog.value = show }

    private val _aiPrompt = MutableStateFlow("")
    val aiPrompt: StateFlow<String> = _aiPrompt.asStateFlow()
    fun onAiPromptChange(prompt: String) { _aiPrompt.value = prompt }

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()
    fun clearAiError() { _aiError.value = null }

    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult.asStateFlow()
    fun clearAiResult() { _aiResult.value = null }

    fun requestAiDraft(apiKey: String) {
        val prompt = _aiPrompt.value.trim()
        if (prompt.isBlank()) {
            _aiError.value = "Prompt cannot be empty"
            return
        }
        _aiLoading.value = true
        _aiError.value = null
        _aiResult.value = null
        viewModelScope.launch {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val body = """
                    {\n  \"contents\": [\n    {\n      \"parts\": [\n        { \"text\": \"$prompt\" }\n      ]\n    }\n  ]\n}\n"""
                val text = withContext(Dispatchers.IO) {
                    val client = okhttp3.OkHttpClient()
                    val request = okhttp3.Request.Builder()
                        .url(url)
                        .post(body.toRequestBody("application/json".toMediaTypeOrNull()))
                        .addHeader("Content-Type", "application/json")
                        .build()
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        _aiError.value = "AI request failed: ${response.code}"
                        return@withContext null
                    }
                    val respStr = response.body?.string() ?: ""
                    parseGeminiResponse(respStr)
                }
                if (text.isNullOrBlank()) {
                    _aiError.value = "No AI result returned"
                } else {
                    _aiResult.value = text
                }
            } catch (e: Exception) {
                _aiError.value = e.message ?: "Unknown error"
            } finally {
                _aiLoading.value = false
            }
        }
    }

    private fun parseGeminiResponse(json: String): String? {
        try {
            val obj = org.json.JSONObject(json)
            val candidates = obj.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            return parts.getJSONObject(0).optString("text")
        } catch (e: Exception) {
            return null
        }
    }

    fun applyAiDraft() {
        val result = _aiResult.value ?: return
        val lines = result.lines().filter { it.isNotBlank() }
        if (lines.isNotEmpty()) {
            _title.value = lines.first().take(60)
            _description.value = lines.drop(1).joinToString("\n").take(2000)
        }
        _showAiDialog.value = false
        clearAiResult()
    }
}
