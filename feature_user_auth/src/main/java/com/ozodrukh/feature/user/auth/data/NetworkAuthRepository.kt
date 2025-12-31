package com.ozodrukh.feature.user.auth.data

import com.google.gson.Gson
import com.ozodrukh.auth.TokenManager
import com.ozodrukh.feature.user.auth.UserProfile
import com.ozodrukh.feature.user.auth.data.remote.AuthApiService
import com.ozodrukh.feature.user.auth.data.remote.model.CheckAuthCodePayload
import com.ozodrukh.feature.user.auth.data.remote.model.Error404Response
import com.ozodrukh.feature.user.auth.data.remote.model.Error422Response
import com.ozodrukh.feature.user.auth.data.remote.model.PhonePayload
import com.ozodrukh.feature.user.auth.data.remote.model.RegisterPayload
import com.ozodrukh.feature.user.auth.domain.AuthRepository
import com.ozodrukh.feature.user.auth.domain.AuthStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class NetworkAuthRepository(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val gson = Gson()

    override suspend fun sendOtp(phone: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.sendAuthCode(PhonePayload(phone))
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send OTP"))
            }
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<AuthStatus> = withContext(Dispatchers.IO) {
        try {
            val response = api.checkAuthCode(CheckAuthCodePayload(phone, code))
            if (response.isUserExists) {
                if (response.accessToken != null && response.refreshToken != null) {
                    tokenManager.installNewAuthToken(response.accessToken, response.refreshToken)
                    Result.success(AuthStatus.UserExists)
                } else {
                    Result.failure(Exception("User exists but tokens are missing"))
                }
            } else {
                Result.success(AuthStatus.NewUser)
            }
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    override suspend fun registerUser(phone: String, profile: UserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = RegisterPayload(
                phone = phone,
                name = profile.name,
                username = profile.username
            )
            val response = api.register(payload)
            if (response.accessToken != null && response.refreshToken != null) {
                tokenManager.installNewAuthToken(response.accessToken, response.refreshToken)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed: tokens missing"))
            }
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    private fun handleException(e: Throwable): Throwable {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                if (errorBody != null) {
                    when (e.code()) {
                        404 -> {
                            val errorResponse = gson.fromJson(errorBody, Error404Response::class.java)
                            if (errorResponse?.detail?.message != null) {
                                Exception(errorResponse.detail.message)
                            } else {
                                e
                            }
                        }
                        422 -> {
                            val errorResponse = gson.fromJson(errorBody, Error422Response::class.java)
                            if (!errorResponse?.detail.isNullOrEmpty()) {
                                val messages = errorResponse.detail!!.mapNotNull { it.msg }
                                Exception(messages.joinToString("\n"))
                            } else {
                                e
                            }
                        }
                        else -> e
                    }
                } else {
                    e
                }
            } catch (parseException: Exception) {
                e
            }
        } else {
            e
        }
    }
}
