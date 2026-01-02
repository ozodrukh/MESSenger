package com.ozodrukh.feature.profile.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponseDto(
    @SerializedName("profile_data")
    val profileData: UserProfileDto
)

data class UserProfileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("city") val city: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("vk") val vk: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("avatars") val avatars: AvatarsDto?,
    @SerializedName("completed_task") val completedTask: Int?,
    @SerializedName("online") val online: Boolean?
)

data class AvatarsDto(
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("bigAvatar") val bigAvatar: String?,
    @SerializedName("miniAvatar") val miniAvatar: String?
)

data class UpdateProfileRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("vk") val vk: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("avatar") val avatar: UploadImageDto?
)

data class UploadImageDto(
    @SerializedName("filename") val filename: String,
    @SerializedName("base_64") val base64: String
)

data class UpdateProfileResponseDto(
    @SerializedName("avatars") val avatars: AvatarsDto?
)
