package com.ozodrukh.feature.profile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodrukh.auth.TokenManager
import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.usecase.GetMyProfileUseCase
import com.ozodrukh.feature.profile.domain.usecase.ObserveMyProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val observeMyProfileUseCase: ObserveMyProfileUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeMyProfileUseCase().collect { profile ->
                if (profile != null) {
                    _uiState.value = ProfileUiState.Success(profile)
                }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            if (_uiState.value !is ProfileUiState.Success) {
                _uiState.value = ProfileUiState.Loading
            }
            getMyProfileUseCase()
                .onFailure { error ->
                    Timber.e(error, "Failed to load profile")
                    if (_uiState.value !is ProfileUiState.Success) {
                        _uiState.value = ProfileUiState.Error(error.message ?: "Unknown error")
                    }
                }
        }
    }

    fun logout() {
        tokenManager.clearTokens()
    }
}