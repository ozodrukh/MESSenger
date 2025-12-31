package com.ozodrukh.core

import com.ozodrukh.core.network.NetworkErrorHandlingInterceptor
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CoreModuleQualifiers {
    val AuthorizedRetrofit = named("authorizedRetrofit")
    val UnauthorizedRetrofit = named("unauthorizedRetrofit")
}

private const val baseUrl = "https://plannerok.ru/api/v1/"

private fun provideOkHttpClient(
    authenticator: Authenticator?,
    interceptors: List<Interceptor> = emptyList()
): OkHttpClient {
    return OkHttpClient.Builder().run {
        authenticator?.let(::authenticator)
        interceptors.forEach(::addInterceptor)
        addInterceptor(NetworkErrorHandlingInterceptor())
        addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            },
        )
        build()
    }
}

val CoreModule = module {
    single(CoreModuleQualifiers.AuthorizedRetrofit) {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(get<Authenticator>()))
            .build()
    }

    single(CoreModuleQualifiers.UnauthorizedRetrofit) {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(null))
            .build()
    }

}