package com.ozodrukh.auth

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RefreshTokenService {
    @Headers("accept: application/json")
    @POST("/api/v1/users/refresh-token/")
    fun refreshToken(@Body body: RefreshTokenRequest): Call<RefreshTokenResponse>
}

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)
data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,
)