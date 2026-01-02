package com.ozodrukh.auth.session

sealed interface AuthEvent {
    data object SessionExpired : AuthEvent
}
