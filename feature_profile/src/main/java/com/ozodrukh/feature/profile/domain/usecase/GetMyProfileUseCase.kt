package com.ozodrukh.feature.profile.domain.usecase

import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository

class GetMyProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return repository.getProfile()
    }
}
