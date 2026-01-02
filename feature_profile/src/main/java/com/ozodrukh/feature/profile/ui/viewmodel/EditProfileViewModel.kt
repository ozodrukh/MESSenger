package com.ozodrukh.feature.profile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodrukh.feature.profile.domain.model.AvatarUpdate
import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.model.UserProfileUpdate
import com.ozodrukh.feature.profile.domain.usecase.UpdateMyProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class EditProfileUiState {
    object Idle : EditProfileUiState()
    object Loading : EditProfileUiState()
    object Success : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

data class EditProfileState(
    val name: String = "",
    val username: String = "",
    val city: String = "",
    val birthday: String = "",
    val vk: String = "",
    val instagram: String = "",
    val status: String = "",
    val avatarBase64: String? = null,
    val avatarUri: android.net.Uri? = null,
    val avatarUrl: String? = null // For displaying current avatar if no new one selected
)

class EditProfileViewModel(
    private val updateMyProfileUseCase: UpdateMyProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(EditProfileState())
    val formState: StateFlow<EditProfileState> = _formState.asStateFlow()

    fun initialize(profile: UserProfile) {
        if (_formState.value.username.isNotEmpty()) return // Already initialized

        _formState.value = EditProfileState(
            name = profile.name,
            username = profile.username,
            city = profile.city ?: "",
            birthday = profile.birthday ?: "",
            vk = profile.vk ?: "",
            instagram = profile.instagram ?: "",
            status = profile.status ?: "",
            avatarUrl = profile.bigAvatarUrl ?: profile.avatarUrl
        )
    }

    fun onNameChanged(value: String) {
        _formState.value = _formState.value.copy(name = value)
    }

    fun onUsernameChanged(value: String) {
        _formState.value = _formState.value.copy(username = value)
    }

    fun onCityChanged(value: String) {
        _formState.value = _formState.value.copy(city = value)
    }

    fun onBirthdayChanged(value: String) {
        _formState.value = _formState.value.copy(birthday = value)
    }

    fun onVkChanged(value: String) {
        _formState.value = _formState.value.copy(vk = value)
    }

    fun onInstagramChanged(value: String) {
        _formState.value = _formState.value.copy(instagram = value)
    }
    
    fun onStatusChanged(value: String) {
        _formState.value = _formState.value.copy(status = value)
    }

    fun onAvatarSelected(base64: String, uri: android.net.Uri) {
        _formState.value = _formState.value.copy(
            avatarBase64 = base64,
            avatarUri = uri
        )
    }

    fun saveChanges() {
        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            val currentState = _formState.value

            val update = UserProfileUpdate(
                name = currentState.name,
                username = currentState.username,
                city = currentState.city.ifBlank { null },
                birthday = currentState.birthday.ifBlank { null },
                vk = currentState.vk.ifBlank { null },
                instagram = currentState.instagram.ifBlank { null },
                status = currentState.status.ifBlank { null },
                avatar = currentState.avatarBase64?.let {
                    AvatarUpdate(filename = "avatar.jpg", base64 = it)
                }
            )

            updateMyProfileUseCase(update)
                .onSuccess {
                    _uiState.value = EditProfileUiState.Success
                }
                .onFailure { error ->
                    Timber.e(error, "Failed to update profile")
                    _uiState.value = EditProfileUiState.Error(error.message ?: "Failed to update")
                }
        }
    }
    
    fun resetState() {
        _uiState.value = EditProfileUiState.Idle
    }
}
