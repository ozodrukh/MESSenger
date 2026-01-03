package com.ozodrukh.feature.profile.domain.repository

import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.model.UserProfileUpdate

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(update: UserProfileUpdate): Result<Unit>
}
