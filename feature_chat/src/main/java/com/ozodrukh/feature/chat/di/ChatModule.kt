package com.ozodrukh.feature.chat.di

import com.ozodrukh.feature.chat.data.ChatRepository
import com.ozodrukh.feature.chat.data.DefaultChatRepository
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature.chat.ui.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val FeatureChatModule = module {
    single<ChatRepository> { DefaultChatRepository(get()) }
    
    viewModel { (chatId: ChatId) ->
        ChatViewModel(
            repository = get(),
            chatSuggestion = get(),
            chatId = chatId
        )
    }
}