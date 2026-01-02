package com.ozodrukh.feature.profile.domain.usecase

import com.ozodrukh.feature.profile.domain.model.UserProfileUpdate
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository

class UpdateMyProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(update: UserProfileUpdate): Result<Unit> {
        return repository.updateProfile(update)
    }
}
