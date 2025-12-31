package com.ozodrukh.auth.session

interface IsAuthenticated {
    suspend fun isAuthenticated(): Boolean
}