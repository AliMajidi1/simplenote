package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.remote.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import android.util.Patterns

sealed class LoginEvent {
    object NavigateHome : LoginEvent()
    object NavigateRegister : LoginEvent()
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val apiError: String? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null, apiError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null, apiError = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun submit() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        var valid = true
        var emailError: String? = null
        var passwordError: String? = null
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email address"
            valid = false
        }
        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            valid = false
        }
        if (!valid) {
            _uiState.value = _uiState.value.copy(emailError = emailError, passwordError = passwordError)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, apiError = null)
            authRepository.login(email, password).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _events.emit(LoginEvent.NavigateHome)
                    }
                    is NetworkResult.HttpError -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, apiError = "Invalid email or password")
                    }
                    is NetworkResult.NetworkError -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, apiError = "Please check your internet connection.")
                    }
                    is NetworkResult.UnknownError -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, apiError = "Something went wrong. Please try again.")
                    }
                }
            }
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _events.emit(LoginEvent.NavigateRegister)
        }
    }
}

