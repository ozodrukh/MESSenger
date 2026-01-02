package com.ozodrukh.feature.profile.data.repository

import com.ozodrukh.core.network.AppConfigs
import com.ozodrukh.feature.profile.data.model.UploadImageDto
import com.ozodrukh.feature.profile.data.model.UpdateProfileRequestDto
import com.ozodrukh.feature.profile.data.model.UserProfileDto
import com.ozodrukh.feature.profile.data.remote.ProfileApi
import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.domain.model.UserProfileUpdate
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository
import okhttp3.HttpUrl.Companion.toHttpUrl

class ProfileRepositoryImpl(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> = runCatching {
        val response = api.getProfile()
        response.profileData.toDomain()
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
