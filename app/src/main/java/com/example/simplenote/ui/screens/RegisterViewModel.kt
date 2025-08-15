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
        val firstNameError = if (state.firstName.isBlank()) "First name is required" else null
        val lastNameError = if (state.lastName.isBlank()) "Last name is required" else null
        val usernameError = when {
            state.username.isBlank() -> "Username is required"
            state.username.length < 3 -> "Username must be at least 3 characters"
            !state.username.matches(Regex("^[a-zA-Z][a-zA-Z0-9_]*$")) -> "Invalid username format"
            else -> null
        }
        val emailError = if (!state.email.contains("@")) "Invalid email address" else null
        val passwordError = when {
            state.password.length < 8 -> "Password must be at least 8 characters"
            !state.password.any { it.isDigit() } -> "Password must contain a digit"
            !state.password.any { it.isLetter() } -> "Password must contain a letter"
            else -> null
        }
        val confirmPasswordError = if (state.password != state.confirmPassword) "Passwords do not match" else null

        val isValid = listOf(
            firstNameError,
            lastNameError,
            usernameError,
            emailError,
            passwordError,
            confirmPasswordError
        ).all { it == null }

        _uiState.value = state.copy(
            isSubmitEnabled = isValid,
            firstNameError = firstNameError,
            lastNameError = lastNameError,
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )
    }

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, apiError = null)
            when (val result = authRepository.register(
                firstName = _uiState.value.firstName,
                lastName = _uiState.value.lastName,
                username = _uiState.value.username,
                email = _uiState.value.email,
                password = _uiState.value.password
            )) {
                is com.example.simplenote.data.remote.NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, apiError = null)
                    onSuccess()
                }
                is com.example.simplenote.data.remote.NetworkResult.HttpError -> {
                    val errorMsg = result.message ?: "Registration failed. Please check your details."
                    _uiState.value = _uiState.value.copy(isSubmitting = false, apiError = errorMsg)
                }
                is com.example.simplenote.data.remote.NetworkResult.NetworkError -> {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, apiError = "Please check your internet connection.")
                }
                is com.example.simplenote.data.remote.NetworkResult.UnknownError -> {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, apiError = "Something went wrong. Please try again.")
                }
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
    val isSubmitting: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val apiError: String? = null
)
