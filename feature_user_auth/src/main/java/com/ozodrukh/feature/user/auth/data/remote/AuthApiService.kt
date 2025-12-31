package com.ozodrukh.feature.user.auth.data.remote

import com.ozodrukh.feature.user.auth.data.remote.model.AuthTokensResponse
import com.ozodrukh.feature.user.auth.data.remote.model.CheckAuthCodePayload
import com.ozodrukh.feature.user.auth.data.remote.model.PhonePayload
import com.ozodrukh.feature.user.auth.data.remote.model.RegisterPayload
import com.ozodrukh.feature.user.auth.data.remote.model.SuccessResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("users/send-auth-code/")
    suspend fun sendAuthCode(@Body payload: PhonePayload): SuccessResponse

    @POST("users/check-auth-code/")
    suspend fun checkAuthCode(@Body payload: CheckAuthCodePayload): AuthTokensResponse

    @POST("users/register/")
    suspend fun register(@Body payload: RegisterPayload): AuthTokensResponse
}
