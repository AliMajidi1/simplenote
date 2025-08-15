package com.example.simplenote.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value)
        validateForm()
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
        validateForm()
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
        validateForm()
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
        validateForm()
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
        validateForm()
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value)
        validateForm()
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.firstName.isNotBlank() &&
                state.lastName.isNotBlank() &&
                state.username.length >= 3 && state.username.matches(Regex("^[a-zA-Z][a-zA-Z0-9_]*$")) &&
                state.email.contains("@") &&
                state.password.length >= 8 && state.password.any { it.isDigit() } && state.password.any { it.isLetter() } &&
                state.password == state.confirmPassword

        _uiState.value = state.copy(isSubmitEnabled = isValid)
    }

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            try {
                authRepository.register(
                    firstName = _uiState.value.firstName,
                    lastName = _uiState.value.lastName,
                    username = _uiState.value.username,
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )
                onSuccess()
            } catch (e: Exception) {
                // Handle error (e.g., show snackbar)
            } finally {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
            }
        }
    }
}

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSubmitEnabled: Boolean = false,
    val isSubmitting: Boolean = false
)
