package com.ozodrukh.feature.user.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodrukh.feature.user.auth.domain.AuthRepository
import com.ozodrukh.feature.user.auth.domain.AuthStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<AuthUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onPhoneNumberChange(phone: String, fullPhone: String, countryCode: String, isValid: Boolean) {
        _uiState.update {
            it.copy(
                phoneNumber = phone,
                fullPhoneNumber = fullPhone,
                countryCode = countryCode,
                isPhoneValid = isValid,
                error = null
            )
        }
    }

    fun sendOtp() {
        val phone = _uiState.value.fullPhoneNumber
        if (phone.isBlank()) {
            _uiState.update { it.copy(error = "Phone number cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.sendOtp(phone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.NavigateToOtp)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _uiEvent.send(AuthUiEvent.ShowError(error.message ?: "Unknown error"))
                }
        }
    }

    fun onOtpChange(code: String) {
        _uiState.update {
            it.copy(otpCode = code, isOtpError = false, error = null)
        }
        if (code.length == 6) {
            verifyOtp(code)
        }
    }

    private fun verifyOtp(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.verifyOtp(_uiState.value.fullPhoneNumber, code)
                .onSuccess { status ->
                    _uiState.update { it.copy(isLoading = false) }
                    when (status) {
                        AuthStatus.UserExists -> _uiEvent.send(AuthUiEvent.NavigateToHome)
                        AuthStatus.NewUser -> _uiEvent.send(AuthUiEvent.NavigateToSignUp)
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isOtpError = true, error = error.message) }
                }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(userProfile = it.userProfile.copy(name = name))
        }
    }

    fun onUsernameChange(username: String) {
        val isValid = username.matches(Regex("^[a-zA-Z0-9_-]*$"))
        _uiState.update {
            it.copy(
                userProfile = it.userProfile.copy(username = username),
                isUsernameValid = isValid
            )
        }
    }

    fun registerUser() {
        val state = _uiState.value
        if (!state.isUsernameValid || state.userProfile.username.isBlank()) {
            _uiState.update { it.copy(error = "Invalid username") }
            return
        }
        if (state.userProfile.name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.registerUser(state.fullPhoneNumber, state.userProfile)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
