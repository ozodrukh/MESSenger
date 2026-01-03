package com.ozodrukh.feature.profile.di

import com.ozodrukh.core.CoreModuleQualifiers
import com.ozodrukh.feature.profile.data.datasource.ProfileLocalDataSource
import com.ozodrukh.feature.profile.data.datasource.SharedPreferencesProfileDataSource
import com.ozodrukh.feature.profile.data.remote.ProfileApi
import com.ozodrukh.feature.profile.data.repository.ProfileRepositoryImpl
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository
import com.ozodrukh.feature.profile.domain.usecase.GetMyProfileUseCase
import com.ozodrukh.feature.profile.domain.usecase.ObserveMyProfileUseCase
import com.ozodrukh.feature.profile.domain.usecase.UpdateMyProfileUseCase
import com.ozodrukh.feature.profile.ui.viewmodel.EditProfileViewModel
import com.ozodrukh.feature.profile.ui.viewmodel.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

val ProfileModule = module {
    single<ProfileApi> {
        get<Retrofit>(CoreModuleQualifiers.AuthorizedRetrofit).create(ProfileApi::class.java)
    }

    single<ProfileLocalDataSource> {
        SharedPreferencesProfileDataSource(androidContext(), get())
    }

    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }

    factoryOf(::GetMyProfileUseCase)
    factoryOf(::UpdateMyProfileUseCase)
    factoryOf(::ObserveMyProfileUseCase)

    viewModelOf(::ProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}
