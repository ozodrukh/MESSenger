package com.ozodrukh.auth.session

import com.ozodrukh.auth.TokenManager

class SessionManager(private val tokenManager: TokenManager) : IsAuthenticated {
    override suspend fun isAuthenticated(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}
