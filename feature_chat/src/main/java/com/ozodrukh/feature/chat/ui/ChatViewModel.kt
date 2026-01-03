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

    private var cachedSuggestion: String? = null

    private val _isAiSuggestionsEnabled = MutableStateFlow(true)
    val isAiSuggestionsEnabled: StateFlow<Boolean> = _isAiSuggestionsEnabled.asStateFlow()

    private val _isNewMessageReceived = MutableStateFlow(false)
    val isNewMessageReceived: StateFlow<Boolean> = _isNewMessageReceived.asStateFlow()

    private val _isGeneratingSuggestion = MutableStateFlow(false)
    val isGeneratingSuggestion: StateFlow<Boolean> = _isGeneratingSuggestion.asStateFlow()

    private val _summary = MutableStateFlow<String?>(null)
    val summary: StateFlow<String?> = _summary.asStateFlow()

    private val _showSummaryDialog = MutableStateFlow(false)
    val showSummaryDialog: StateFlow<Boolean> = _showSummaryDialog.asStateFlow()

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

    private var lastProcessedMessageId: String? = null
    private var summaryLastMessageId: String? = null

    val uiState: StateFlow<ChatUiState> = repository.getChatDetails(chatId)
        .map { chatDetails ->
            val lastMsg = chatDetails.messages.lastOrNull()
            if (lastMsg != null && !lastMsg.isMe && lastMsg.id != lastProcessedMessageId) {
                 _isNewMessageReceived.value = true
            }
            ChatUiState.Success(chatDetails) as ChatUiState 
        }
        .catch { emit(ChatUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatUiState.Loading
        )

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _suggestion.value = null

        viewModelScope.launch {
            repository.sendMessage(chatId, text)

            cachedSuggestion = null
            lastProcessedMessageId = null
            _isNewMessageReceived.value = false
        }
    }

    fun toggleAiSuggestions() {
        if (_suggestion.value == null && cachedSuggestion != null && isAiSuggestionsEnabled.value) {
            _suggestion.value = cachedSuggestion
            return
        } else if (isAiSuggestionsEnabled.value && cachedSuggestion == null) {
            generateSuggestion()
            return
        }


        _isAiSuggestionsEnabled.value = !_isAiSuggestionsEnabled.value

        if (!_isAiSuggestionsEnabled.value) {
            if (_suggestion.value != null) {
                cachedSuggestion = _suggestion.value
            }
            _suggestion.value = null
        } else {
            if (cachedSuggestion != null) {
                _suggestion.value = cachedSuggestion
            } else {
                generateSuggestion()
            }
        }
    }

    fun dismissSuggestion() {
        _suggestion.value = null
    }

    fun regenerateSuggestion() {
        lastProcessedMessageId = null
        generateSuggestion()
    }

    fun generateSuggestion() {
        _isNewMessageReceived.value = false
        if (!_isAiSuggestionsEnabled.value) return
        
        val currentState = uiState.value
        if (currentState is ChatUiState.Success) {
            val messages = currentState.data.messages
            if (messages.isEmpty()) return
            
            val lastMsgId = messages.last().id
            if (lastMsgId == lastProcessedMessageId) return

            viewModelScope.launch {
                _isGeneratingSuggestion.value = true
                val history = messages.takeLast(10).map { uiMsg ->
                    Message(
                        id = uiMsg.id,
                        text = uiMsg.text,
                        timestamp = uiMsg.timestamp,
                        senderId = if (uiMsg.isMe) "me" else "other"
                    )
                }

                lastProcessedMessageId = lastMsgId 
                
                try {
                    val suggestionMsg = chatSuggestion.suggestNextMessage(history)
                    if (suggestionMsg.text.isNotBlank()) {
                        _suggestion.value = suggestionMsg.text
                        cachedSuggestion = suggestionMsg.text
                    }
                } catch (e: Exception) {
                    // Reset if failed so we can try again
                    lastProcessedMessageId = null
                } finally {
                    _isGeneratingSuggestion.value = false
                }
            }
        }
    }

    fun generateSummary() {
        _showSummaryDialog.value = true

        val currentState = uiState.value
        if (currentState is ChatUiState.Success) {
            val messages = currentState.data.messages
            if (messages.isEmpty()) return

            val lastMsgId = messages.last().id

            // Check cache
            if (_summary.value != null && summaryLastMessageId == lastMsgId) {
                return
            }

            viewModelScope.launch {
                _isGeneratingSummary.value = true
                try {
                    val history = messages.map { uiMsg ->
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
                        summaryLastMessageId = lastMsgId
                    }
                } finally {
                    _isGeneratingSummary.value = false
                }
            }
        }
    }

    fun dismissSummary() {
        _showSummaryDialog.value = false
    }
}