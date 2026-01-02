package com.ozodrukh.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RefreshTokenService {
    @Headers("Accept: application/json")
    @POST("/api/v1/users/refresh-token/")
    fun refreshToken(@Body body: RefreshTokenRequest): Call<RefreshTokenResponse>
}

data class RefreshTokenRequest(val refreshToken: String)
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)