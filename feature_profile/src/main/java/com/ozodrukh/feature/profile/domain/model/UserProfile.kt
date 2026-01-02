package com.ozodrukh.feature.profile.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val id: Int,
    val name: String,
    val username: String,
    val phone: String,
    val city: String?,
    val birthday: String?, // Keeping as String for now, can be LocalDate
    val vk: String?,
    val instagram: String?,
    val status: String?,
    val avatarUrl: String?,
    val bigAvatarUrl: String?,
    val miniAvatarUrl: String?,
    val completedTask: Int,
    val online: Boolean
): Parcelable

data class UserProfileUpdate(
    val name: String,
    val username: String,
    val birthday: String?,
    val city: String?,
    val vk: String?,
    val instagram: String?,
    val status: String?,
    val avatar: AvatarUpdate? = null
)

data class AvatarUpdate(
    val filename: String,
    val base64: String
)
