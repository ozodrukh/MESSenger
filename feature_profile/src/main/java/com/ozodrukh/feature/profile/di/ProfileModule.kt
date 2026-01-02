package com.ozodrukh.feature.profile.di

import com.ozodrukh.core.CoreModuleQualifiers
import com.ozodrukh.feature.profile.data.remote.ProfileApi
import com.ozodrukh.feature.profile.data.repository.ProfileRepositoryImpl
import com.ozodrukh.feature.profile.domain.repository.ProfileRepository
import com.ozodrukh.feature.profile.domain.usecase.GetMyProfileUseCase
import com.ozodrukh.feature.profile.domain.usecase.UpdateMyProfileUseCase
import com.ozodrukh.feature.profile.ui.viewmodel.EditProfileViewModel
import com.ozodrukh.feature.profile.ui.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

val ProfileModule = module {
    single<ProfileApi> {
        get<Retrofit>(CoreModuleQualifiers.AuthorizedRetrofit).create(ProfileApi::class.java)
    }

    single<ProfileRepository> { ProfileRepositoryImpl(get()) }

    factoryOf(::GetMyProfileUseCase)
    factoryOf(::UpdateMyProfileUseCase)

    viewModelOf(::ProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}
