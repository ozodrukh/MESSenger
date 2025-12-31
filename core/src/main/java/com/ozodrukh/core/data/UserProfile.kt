package com.ozodrukh.core.data

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val id: Int,
    val name: String,
    val username: String,
    val birthday: String,
    val city: String,
    val vk: String,
    val instagram: String,
    val status: String,
    val avatar: String,
    val last: String,
    val online: Boolean,
    val created: String,
    val phone: String,
    @SerializedName("completed_task")
    val completedTask: Int,
    val avatars: Avatars
)

data class Avatars(
    val avatar: String,
    val bigAvatar: String,
    val miniAvatar: String
)