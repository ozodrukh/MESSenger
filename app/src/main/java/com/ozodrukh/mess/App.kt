package com.ozodrukh.mess

import android.app.Application
import com.ozodrukh.auth.di.AuthModule
import com.ozodrukh.core.CoreModule
import com.ozodrukh.feature.user.auth.di.FeatureUserAuthModule
import com.ozodrukh.feature_dialogs.di.FeatureDialogsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(
                CoreModule,
                AuthModule,
                FeatureUserAuthModule,
                FeatureDialogsModule
            )
        }

        // todo: add debug flavor check
        Timber.plant(Timber.DebugTree())
    }

}