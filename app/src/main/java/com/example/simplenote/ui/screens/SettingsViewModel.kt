package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.remote.NetworkResult
import com.example.simplenote.data.remote.UserInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(val user: UserInfoResponse) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

class SettingsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun fetchUserInfo() {
        _uiState.value = SettingsUiState.Loading
        viewModelScope.launch {
            try {
                authRepository.getUserInfo().collect { result ->
                    when (result) {
                        is NetworkResult.Success -> _uiState.value = SettingsUiState.Success(result.value)
                        is NetworkResult.HttpError -> _uiState.value = SettingsUiState.Error(result.message ?: "HTTP error")
                        is NetworkResult.NetworkError -> _uiState.value = SettingsUiState.Error("Network error")
                        is NetworkResult.UnknownError -> _uiState.value = SettingsUiState.Error("Unknown error")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
