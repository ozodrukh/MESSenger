package com.ozodrukh.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

interface TokenManager {
    fun installNewAuthToken(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
}

internal class SharedTokenManager(context: Context) : TokenManager {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun installNewAuthToken(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString("ACCESS_TOKEN", accessToken)
                .putString("REFRESH_TOKEN", refreshToken)
        }
    }

    override fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)
    override fun getRefreshToken(): String? = prefs.getString("REFRESH_TOKEN", null)

    override fun clearTokens() {
        prefs.edit { clear() }
    }
}