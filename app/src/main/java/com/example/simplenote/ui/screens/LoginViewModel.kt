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

sealed class LoginEvent {
    object NavigateHome : LoginEvent()
    object NavigateRegister : LoginEvent()
}

sealed class LoginError {
    object UsernameRequired : LoginError()
    object PasswordInvalid : LoginError()
    object InvalidCredentials : LoginError()
    object Network : LoginError()
    object Unknown : LoginError()
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val usernameError: LoginError? = null,
    val passwordError: LoginError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val apiError: LoginError? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username, usernameError = null, apiError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null, apiError = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun submit() {
        val username = _uiState.value.username.trim()
        val password = _uiState.value.password
        var valid = true
        var usernameError: LoginError? = null
        var passwordError: LoginError? = null
        if (username.isEmpty()) {
            usernameError = LoginError.UsernameRequired
            valid = false
        }
        if (password.length < 6) {
            passwordError = LoginError.PasswordInvalid
            valid = false
        }
        if (!valid) {
            _uiState.value = _uiState.value.copy(usernameError = usernameError, passwordError = passwordError)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, apiError = null)
            authRepository.login(username, password).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _events.emit(LoginEvent.NavigateHome)
                    }
                    is NetworkResult.HttpError -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, apiError = LoginError.InvalidCredentials)
                    }
                    is NetworkResult.NetworkError -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, apiError = LoginError.Network)
                    }
                    is NetworkResult.UnknownError -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, apiError = LoginError.Unknown)
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
