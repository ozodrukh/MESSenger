package com.ozodrukh.feature.profile.domain.usecase

import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveMyProfileUseCase(
    private val repository: ProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return repository.observeProfile()
    }
}
