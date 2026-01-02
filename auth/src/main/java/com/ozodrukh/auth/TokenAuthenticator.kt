package com.ozodrukh.auth

import com.ozodrukh.auth.session.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val refreshTokenServiceProvider: () -> RefreshTokenService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentRefreshToken = tokenManager.getRefreshToken() ?: return null

        synchronized(this) {
            val newAccessToken = tokenManager.getAccessToken()
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

            // token already updated skip
            if (newAccessToken != null && newAccessToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }

            val refreshCall =
                refreshTokenServiceProvider().refreshToken(RefreshTokenRequest(currentRefreshToken))

            try {
                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val authData = refreshResponse.body()!!

                    tokenManager.installNewAuthToken(authData.accessToken, authData.refreshToken)

                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${authData.accessToken}")
                        .build()
                } else {
                    sessionManager.onSessionExpired()
                    return null
                }
            } catch (e: Exception) {
                return null
            }
        }
    }
}