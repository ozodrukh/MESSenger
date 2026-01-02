package com.ozodrukh.feature.chat.data

import com.ozodrukh.core.data.chat.FakeChatDataSource
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature.chat.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ChatRepository {
    fun getMessages(chatId: ChatId): Flow<List<Message>>
    suspend fun sendMessage(chatId: ChatId, text: String)
}

class DefaultChatRepository(
    private val dataSource: FakeChatDataSource
) : ChatRepository {

    override fun getMessages(chatId: ChatId): Flow<List<Message>> {
        return dataSource.getChatFlow(chatId)
            .map { chat -> 
                chat?.messages?.map { msg ->
                    Message(
                        id = msg.id,
                        text = msg.text,
                        timestamp = msg.timestamp,
                        isMe = msg.senderId == "me"
                    )
                } ?: emptyList()
            }
    }

    override suspend fun sendMessage(chatId: ChatId, text: String) {
        dataSource.sendMessage(chatId, text)
    }
}