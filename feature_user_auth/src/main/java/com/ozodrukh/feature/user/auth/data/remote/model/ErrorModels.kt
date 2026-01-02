package com.ozodrukh.feature.user.auth.data.remote.model

import com.google.gson.annotations.SerializedName

data class Error404Response(
    @SerializedName("detail") val detail: ErrorMessage?
)

data class ErrorMessage(
    @SerializedName("message") val message: String?
)

data class Error422Response(
    @SerializedName("detail") val detail: List<ValidationError>?
)

data class ValidationError(
    @SerializedName("loc") val loc: List<String>?,
    @SerializedName("msg") val msg: String?,
    @SerializedName("type") val type: String?
)
