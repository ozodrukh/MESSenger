package com.ozodrukh.llm.api

import com.ozodrukh.core.domain.model.Message

interface ChatSuggestion {
    suspend fun suggestNextMessage(history: List<Message>): Message
    suspend fun createSummary(history: List<Message>): Message
}