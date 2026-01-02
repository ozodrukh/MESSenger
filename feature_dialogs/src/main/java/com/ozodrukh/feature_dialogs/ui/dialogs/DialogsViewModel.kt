package com.ozodrukh.feature_dialogs.ui.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodrukh.core.data.chat.FakeChatDataSource
import com.ozodrukh.core.domain.model.Chat
import com.ozodrukh.feature_dialogs.models.Dialog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DialogsViewModel(
    dataSource: FakeChatDataSource
) : ViewModel() {

    val dialogs: StateFlow<List<Dialog>> = dataSource.chats
        .map { chatMap -> 
            chatMap.values.sortedByDescending { it.lastMessage?.timestamp ?: 0 }
                .map { chat -> chat.toDialog() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private fun Chat.toDialog(): Dialog {
        val lastMsg = this.lastMessage
        return Dialog(
            id = this.id,
            name = this.participant.name,
            lastMessage = lastMsg?.text ?: "",
            avatarUrl = this.participant.avatarUrl,
            timestamp = lastMsg?.timestamp?.let { formatTime(it) } ?: "",
            unreadCount = this.unreadCount
        )
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}