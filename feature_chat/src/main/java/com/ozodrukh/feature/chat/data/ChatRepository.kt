package com.ozodrukh.feature.chat.data

import com.ozodrukh.core.data.chat.FakeChatDataSource
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature.chat.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.ozodrukh.feature.chat.models.ChatDetails
import kotlinx.coroutines.flow.filterNotNull

interface ChatRepository {
    fun getChatDetails(chatId: ChatId): Flow<ChatDetails>
    suspend fun sendMessage(chatId: ChatId, text: String)
}

class DefaultChatRepository(
    private val dataSource: FakeChatDataSource
) : ChatRepository {

    override fun getChatDetails(chatId: ChatId): Flow<ChatDetails> {
        return dataSource.getChatFlow(chatId)
            .filterNotNull()
            .map { chat ->
                ChatDetails(
                    messages = chat.messages.map { msg ->
                        Message(
                            id = msg.id,
                            text = msg.text,
                            timestamp = msg.timestamp,
                            isMe = msg.senderId == "me"
                        )
                    },
                    chatName = chat.participant.name,
                    chatAvatarUrl = chat.participant.avatarUrl,
                    isOnline = chat.participant.isOnline,
                    lastSeen = chat.participant.lastSeen
                )
            }
    }

    override suspend fun sendMessage(chatId: ChatId, text: String) {
        dataSource.sendMessage(chatId, text)
    }
}