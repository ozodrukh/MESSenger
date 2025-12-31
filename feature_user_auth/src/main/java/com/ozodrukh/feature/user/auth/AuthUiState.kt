package com.ozodrukh.feature.user.auth

import androidx.compose.runtime.Immutable

@Immutable
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val phoneNumber: String = "", //"+79219999999",
    val fullPhoneNumber: String = "", // Including country code
    val countryCode: String = "",//"KZ",
    val isPhoneValid: Boolean = false,
    val otpCode: String = "",
    val isOtpError: Boolean = false,
    val userProfile: UserProfile = UserProfile(),
    val isUsernameValid: Boolean = true
)

@Immutable
data class UserProfile(
    val name: String = "",
    val username: String = "",
    val avatarUrl: String? = null
)

sealed interface AuthUiEvent {
    data object NavigateToOtp : AuthUiEvent
    data object NavigateToHome : AuthUiEvent
    data object NavigateToSignUp : AuthUiEvent
    data class ShowError(val message: String) : AuthUiEvent
}
