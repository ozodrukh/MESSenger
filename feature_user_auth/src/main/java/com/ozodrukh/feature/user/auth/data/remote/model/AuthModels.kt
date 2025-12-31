package com.ozodrukh.feature.user.auth.data.remote.model

import com.google.gson.annotations.SerializedName

data class PhonePayload(
    @SerializedName("phone") val phone: String
)

data class CheckAuthCodePayload(
    @SerializedName("phone") val phone: String,
    @SerializedName("code") val code: String
)

data class RegisterPayload(
    @SerializedName("phone") val phone: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String
)

data class SuccessResponse(
    @SerializedName("is_success") val isSuccess: Boolean
)

data class AuthTokensResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("is_user_exists") val isUserExists: Boolean = false
)
