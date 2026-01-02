package com.ozodrukh.feature_dialogs.models

import androidx.compose.runtime.Immutable
import com.ozodrukh.core.domain.model.ChatId

@Immutable
data class Dialog(
    val id: ChatId,
    val name: String,
    val lastMessage: String,
    val avatarUrl: String?,
    val timestamp: String,
    val unreadCount: Int = 0
)