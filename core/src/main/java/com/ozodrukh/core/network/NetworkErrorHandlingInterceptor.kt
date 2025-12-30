package com.ozodrukh.core.network

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorHandlingInterceptor : Interceptor {

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e, "Network error occurred: ${e.message}")

            val errorMessage = when (e) {
                is UnknownHostException, is ConnectException -> "No internet connection available"
                is SocketTimeoutException -> "Request timed out"
                is IOException -> e.message ?: "Network error occurred"
                else -> "Unexpected error occurred"
            }

            val errorResponse = mapOf(
                "success" to false,
                "error" to errorMessage,
                "code" to 503, // Service Unavailable
            )

            val jsonBody = gson.toJson(errorResponse)

            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(503) // Service Unavailable
                .message(errorMessage)
                .body(jsonBody.toResponseBody(jsonMediaType))
                .build()
        }
    }
}
