package com.ozodrukh.feature.profile.data.datasource

import com.ozodrukh.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun clearProfile()
    suspend fun getProfile(): UserProfile?
}
