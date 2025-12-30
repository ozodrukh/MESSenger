package com.ozodrukh.auth.di

import com.ozodrukh.auth.RefreshTokenService
import com.ozodrukh.auth.TokenAuthenticator
import com.ozodrukh.auth.TokenManager
import com.ozodrukh.core.CoreModuleQualifiers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit

var AuthModule = module {
    singleOf(::TokenManager)
    single<TokenAuthenticator> {
        TokenAuthenticator(
            tokenManager = get<TokenManager>(),
            refreshTokenServiceProvider = {
                get<RefreshTokenService>()
            })
    }

    single<RefreshTokenService> {
        get<Retrofit>(CoreModuleQualifiers.UnauthorizedRetrofit)
            .create(RefreshTokenService::class.java)
    }
}