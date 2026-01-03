package com.ozodrukh.feature.profile.data.repository

import com.ozodrukh.core.network.AppConfigs
import com.ozodrukh.feature.profile.data.datasource.ProfileLocalDataSource
import com.ozodrukh.feature.profile.data.model.UploadImageDto
import com.ozodrukh.feature.profile.data.model.UpdateProfileRequestDto
import com.ozodrukh.feature.profile.data.model.UserProfileDto
import com.ozodrukh.feature.profile.data.remote.ProfileApi
import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.model.UserProfileUpdate
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl.Companion.toHttpUrl

class ProfileRepositoryImpl(
    private val api: ProfileApi,
    private val localDataSource: ProfileLocalDataSource
) : ProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> {
        return localDataSource.observeProfile()
    }

    override suspend fun getProfile(): Result<UserProfile> = runCatching {
        val response = api.getProfile()
        val profile = response.profileData.toDomain()
        localDataSource.saveProfile(profile)
        profile
    }

    override suspend fun updateProfile(update: UserProfileUpdate): Result<Unit> = runCatching {
        val request = UpdateProfileRequestDto(
            name = update.name,
            username = update.username,
            birthday = update.birthday,
            city = update.city,
            vk = update.vk,
            instagram = update.instagram,
            status = update.status,
            avatar = update.avatar?.let {
                UploadImageDto(
                    filename = it.filename,
                    base64 = it.base64
                )
            }
        )
        api.updateProfile(request)
        // Refresh profile data to update local storage
        getProfile().getOrThrow()
    }

    private fun getMediaUrl(path: String?): String? {
        return path?.let {
            AppConfigs.baseEndpoint.toHttpUrl().newBuilder().addPathSegments(path).build().toString()
        }
    }

    private fun UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            username = username,
            phone = phone,
            city = city,
            birthday = birthday,
            vk = vk,
            instagram = instagram,
            status = status,
            avatarUrl = getMediaUrl(avatar),
            bigAvatarUrl = getMediaUrl(avatars?.bigAvatar),
            miniAvatarUrl = getMediaUrl(avatars?.miniAvatar),
            completedTask = completedTask ?: 0,
            online = online ?: false
        )
    }
}
