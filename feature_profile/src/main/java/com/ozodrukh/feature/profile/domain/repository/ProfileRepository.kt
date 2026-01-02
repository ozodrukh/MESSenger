package com.ozodrukh.feature.profile.domain.repository

import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.model.UserProfileUpdate

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(update: UserProfileUpdate): Result<Unit>
}
