package com.ozodrukh.mess

import android.app.Application
import com.ozodrukh.auth.di.AuthModule
import com.ozodrukh.core.CoreModule
import com.ozodrukh.feature.chat.di.FeatureChatModule
import com.ozodrukh.feature.profile.di.ProfileModule
import com.ozodrukh.feature.user.auth.di.FeatureUserAuthModule
import com.ozodrukh.feature_dialogs.di.FeatureDialogsModule
import dev.ioio.estetique.di.AppModule
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.lazyModules
import org.koin.dsl.lazyModule
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(
                AppModule,
                AuthModule,
                CoreModule,
                FeatureUserAuthModule,
                FeatureDialogsModule,
                FeatureChatModule,
                ProfileModule,
            )
        }

        // todo: add debug flavor check
        Timber.plant(Timber.DebugTree())
    }

}