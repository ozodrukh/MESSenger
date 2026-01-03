package com.ozodrukh.auth.di

import com.ozodrukh.auth.session.IsAuthenticated
import com.ozodrukh.auth.RefreshTokenService
import com.ozodrukh.auth.SharedTokenManager
import com.ozodrukh.auth.session.SessionManager
import com.ozodrukh.auth.TokenAuthenticator
import com.ozodrukh.auth.TokenManager
import com.ozodrukh.core.CoreModuleQualifiers
import com.ozodrukh.core.singleAuthorizedRetrofit
import okhttp3.Authenticator
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit

val AuthModule = module {
    single<TokenManager> {
        SharedTokenManager(context = get())
    }

    singleOf(::SessionManager) { bind<IsAuthenticated>() }
    single<Authenticator> {
        TokenAuthenticator(
            tokenManager = get<TokenManager>(),
            sessionManager = get<SessionManager>(),
            refreshTokenServiceProvider = {
                get<RefreshTokenService>()
            })
    }

    singleAuthorizedRetrofit()

    single<RefreshTokenService> {
        get<Retrofit>(CoreModuleQualifiers.UnauthorizedRetrofit)
            .create(RefreshTokenService::class.java)
    }
}