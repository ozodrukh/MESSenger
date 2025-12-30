package com.ozodrukh.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    private val ignoredPaths = listOf(
        "/api/v1/users/register/",
        "/api/v1/users/send-auth-code/",
        "/api/v1/users/check-auth-code/"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        if (ignoredPaths.any { path.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessToken()
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}