package com.ozodrukh.feature.user.auth.domain

import com.ozodrukh.feature.user.auth.UserProfile

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, code: String): Result<AuthStatus>
    suspend fun registerUser(phone: String, profile: UserProfile): Result<Unit>
}

sealed interface AuthStatus {
    data object UserExists : AuthStatus
    data object NewUser : AuthStatus
}
