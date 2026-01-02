package com.ozodrukh.feature_dialogs.di

import com.ozodrukh.feature_dialogs.ui.dialogs.DialogsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val FeatureDialogsModule = module {
    viewModelOf(::DialogsViewModel)
}
