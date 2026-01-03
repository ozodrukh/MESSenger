package com.ozodrukh.core.domain.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class ChatId(val value: String)

@Serializable
data class User(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0
)

@Serializable
data class Message(
    val id: String,
    val text: String,
    val timestamp: Long,
    val senderId: String // "me" or other user ID
)

data class Chat(
    val id: ChatId,
    val participant: User, 
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0
) {
    val lastMessage: Message?
        get() = messages.maxByOrNull { it.timestamp }
}
