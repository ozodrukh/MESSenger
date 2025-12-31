package com.ozodrukh.feature.user.auth.data

import com.ozodrukh.feature.user.auth.UserProfile
import com.ozodrukh.feature.user.auth.domain.AuthRepository
import com.ozodrukh.feature.user.auth.domain.AuthStatus
import kotlinx.coroutines.delay

class MockAuthRepository : AuthRepository {
    override suspend fun sendOtp(phone: String): Result<Unit> {
        delay(1000) // Simulate network
        return Result.success(Unit)
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<AuthStatus> {
        delay(1000)
        return if (code == "111111") {
            Result.success(AuthStatus.UserExists)
        } else if (code == "222222") {
            Result.success(AuthStatus.NewUser)
        } else {
            Result.failure(Exception("Invalid OTP"))
        }
    }

    override suspend fun registerUser(phone: String, profile: UserProfile): Result<Unit> {
        delay(1500)
        return Result.success(Unit)
    }
}
