package dev.ioio.estetique.di

import com.ozodrukh.llm.api.ChatSuggestion
import com.ozodrukh.mess.BuildConfig
import com.ozodrukh.openrouter.impl.ChatSuggestionsImpl
import org.koin.dsl.module

val AppModule = module {
    factory<ChatSuggestion> {
        ChatSuggestionsImpl(BuildConfig.OPENROUTER_API_KEY)
    }
}
