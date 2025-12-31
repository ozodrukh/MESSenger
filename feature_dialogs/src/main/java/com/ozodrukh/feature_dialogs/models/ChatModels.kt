package com.ozodrukh.feature_dialogs.models

import androidx.compose.runtime.Immutable

@JvmInline
value class ChatId(val value: String)

@Immutable
data class Dialog(
    val id: ChatId,
    val name: String,
    val lastMessage: String,
    val avatarUrl: String?,
    val timestamp: String,
    val unreadCount: Int = 0
)
