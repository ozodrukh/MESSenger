package com.ozodrukh.auth.session

import com.ozodrukh.auth.TokenManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionManager(private val tokenManager: TokenManager) : IsAuthenticated {
    private val _events = MutableSharedFlow<AuthEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: Flow<AuthEvent> = _events.asSharedFlow()

    override suspend fun isAuthenticated(): Boolean {
        return tokenManager.getAccessToken() != null
    }

    fun onSessionExpired() {
        tokenManager.clearTokens()
        _events.tryEmit(AuthEvent.SessionExpired)
    }
}
