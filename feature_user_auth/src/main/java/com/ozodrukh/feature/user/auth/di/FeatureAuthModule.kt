package com.ozodrukh.feature.user.auth.di

import com.ozodrukh.core.CoreModuleQualifiers
import com.ozodrukh.feature.user.auth.AuthViewModel
import com.ozodrukh.feature.user.auth.data.NetworkAuthRepository
import com.ozodrukh.feature.user.auth.data.remote.AuthApiService
import com.ozodrukh.feature.user.auth.domain.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val FeatureUserAuthModule = module {
    single<AuthApiService> {
        get<Retrofit>(CoreModuleQualifiers.UnauthorizedRetrofit)
            .create(AuthApiService::class.java)
    }
    singleOf(::NetworkAuthRepository) bind AuthRepository::class
    viewModelOf(::AuthViewModel)
}
