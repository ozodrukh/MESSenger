package com.ozodrukh.feature.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature.chat.data.ChatRepository
import com.ozodrukh.feature.chat.models.ChatUiState
import com.ozodrukh.llm.api.ChatSuggestion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.ozodrukh.core.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.ozodrukh.feature.chat.models.Message as UiMessage

class ChatViewModel(
    private val repository: ChatRepository,
    private val chatSuggestion: ChatSuggestion,
    private val chatId: ChatId
) : ViewModel() {

    private val _suggestion = MutableStateFlow<String?>(null)
    val suggestion: StateFlow<String?> = _suggestion.asStateFlow()

    private val _summary = MutableStateFlow<String?>(null)
    val summary: StateFlow<String?> = _summary.asStateFlow()

    val uiState: StateFlow<ChatUiState> = repository.getChatDetails(chatId)
        .map { ChatUiState.Success(it) as ChatUiState }
        .catch { emit(ChatUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatUiState.Loading
        )

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(chatId, text)
            _suggestion.value = null // Clear suggestion after sending
        }
    }

    fun generateSuggestion() {
        val currentState = uiState.value
        if (currentState is ChatUiState.Success) {
            viewModelScope.launch {
                val history = currentState.data.messages.map { uiMsg ->
                    Message(
                        id = uiMsg.id,
                        text = uiMsg.text,
                        timestamp = uiMsg.timestamp,
                        senderId = if (uiMsg.isMe) "me" else "other"
                    )
                }
                val suggestionMsg = chatSuggestion.suggestNextMessage(history)
                if (suggestionMsg.text.isNotBlank()) {
                    _suggestion.value = suggestionMsg.text
                }
            }
        }
    }

    fun generateSummary() {
        val currentState = uiState.value
        if (currentState is ChatUiState.Success) {
            viewModelScope.launch {
                val history = currentState.data.messages.map { uiMsg ->
                    Message(
                        id = uiMsg.id,
                        text = uiMsg.text,
                        timestamp = uiMsg.timestamp,
                        senderId = if (uiMsg.isMe) "me" else "other"
                    )
                }
                val summaryMsg = chatSuggestion.createSummary(history)
                if (summaryMsg.text.isNotBlank()) {
                    _summary.value = summaryMsg.text
                }
            }
        }
    }

    fun clearSummary() {
        _summary.value = null
    }
}