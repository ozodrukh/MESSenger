package com.ozodrukh.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/v1/users/refresh-token/")
    fun refreshToken(@Body body: RefreshTokenRequest): Call<RefreshTokenResponse>
}


data class RefreshTokenRequest(val refreshToken: String)
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)