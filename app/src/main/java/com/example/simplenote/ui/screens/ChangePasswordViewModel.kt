package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.simplenote.data.AuthRepository

sealed class ChangePasswordUiState {
    object Idle : ChangePasswordUiState()
    object Loading : ChangePasswordUiState()
    data class Success(val message: String) : ChangePasswordUiState()
    data class Error(val error: String) : ChangePasswordUiState()
    object LoggedOut : ChangePasswordUiState()
}

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    var currentPassword = MutableStateFlow("")
    var newPassword = MutableStateFlow("")
    var retypePassword = MutableStateFlow("")
    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun submitChangePassword() {
        val old = currentPassword.value
        val new = newPassword.value
        val retype = retypePassword.value
        if (old.isBlank() || new.isBlank() || retype.isBlank()) {
            _uiState.value = ChangePasswordUiState.Error("All fields are required.")
            return
        }
        if (new != retype) {
            _uiState.value = ChangePasswordUiState.Error("Passwords do not match.")
            return
        }
        _uiState.value = ChangePasswordUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.changePassword(old, new)) {
                is com.example.simplenote.data.remote.NetworkResult.Success -> {
                    authRepository.logout()
                    _uiState.value = ChangePasswordUiState.LoggedOut
                }
                is com.example.simplenote.data.remote.NetworkResult.HttpError -> {
                    val errorMsg = result.message ?: "Password change failed. Please check your details."
                    _uiState.value = ChangePasswordUiState.Error(errorMsg)
                }
                is com.example.simplenote.data.remote.NetworkResult.NetworkError -> {
                    _uiState.value = ChangePasswordUiState.Error("Please check your internet connection.")
                }
                is com.example.simplenote.data.remote.NetworkResult.UnknownError -> {
                    _uiState.value = ChangePasswordUiState.Error("Something went wrong. Please try again.")
                }
            }
        }
    }

    fun clearError() {
        if (_uiState.value is ChangePasswordUiState.Error) {
            _uiState.value = ChangePasswordUiState.Idle
        }
    }
}