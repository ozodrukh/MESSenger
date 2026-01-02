package com.ozodrukh.feature.profile.data.remote

import com.ozodrukh.feature.profile.data.model.ProfileResponseDto
import com.ozodrukh.feature.profile.data.model.UpdateProfileRequestDto
import com.ozodrukh.feature.profile.data.model.UpdateProfileResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApi {
    @GET("users/me/")
    suspend fun getProfile(): ProfileResponseDto

    @PUT("users/me/")
    suspend fun updateProfile(@Body body: UpdateProfileRequestDto): UpdateProfileResponseDto
}
