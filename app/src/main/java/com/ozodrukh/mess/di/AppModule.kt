package dev.ioio.estetique.di

import com.ozodrukh.auth.di.AuthModule
import com.ozodrukh.core.CoreModule
import org.koin.dsl.module

val AppModule = module {
    includes(CoreModule)
    includes(AuthModule)
}
