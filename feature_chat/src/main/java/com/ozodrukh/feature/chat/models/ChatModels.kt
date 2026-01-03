package com.ozodrukh.feature.chat.models

import androidx.compose.runtime.Immutable
import com.ozodrukh.core.domain.model.ChatId
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Message(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean
)

@Immutable
data class ChatDetails(
    val messages: List<Message>,
    val chatName: String,
    val chatAvatarUrl: String?,
    val isOnline: Boolean,
    val lastSeen: Long
)

@Immutable
sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val data: ChatDetails) : ChatUiState
    data class Error(val message: String) : ChatUiState
}