package com.ozodrukh.openrouter.impl

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openrouter.OpenRouterLLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.ozodrukh.core.domain.model.Message
import com.ozodrukh.llm.api.ChatSuggestion
import com.ozodrukh.llm.api.aiMessage
import com.ozodrukh.llm.api.emptyAiMessage

class ChatSuggestionsImpl(apiKey: String) : ChatSuggestion {
    private val openRouterClient = OpenRouterLLMClient(apiKey)

    override suspend fun suggestNextMessage(history: List<Message>): Message {
        val llmResponse = openRouterClient.execute(
            prompt = prompt("message-suggestion") {
                system("Provide next message for user based on message history")
                user(history.joinToString("\n") {
                    if (it.senderId == "me") {
                        "me: ${it.text}"
                    } else {
                        "other_peer: ${it.text}"
                    }
                })
            },
            model = gemini3Flash,
            tools = emptyList()
        )

        return llmResponse.firstOrNull()?.let { response -> aiMessage(response.content) } ?: emptyAiMessage
    }

    override suspend fun createSummary(history: List<Message>): Message {
        val llmResponse = openRouterClient.execute(
            prompt = prompt("message-suggestion") {
                system("You are a helpful assistant that summarizes messages from a conversation")
                user(history.joinToString("\n") {
                    if (it.senderId == "me") {
                        "me: ${it.text}"
                    } else {
                        "other_peer: ${it.text}"
                    }
                })
            },
            model = gemini3Flash,
            tools = emptyList()
        )

        return llmResponse.firstOrNull()?.let { response -> aiMessage(response.content) } ?: emptyAiMessage
    }

    companion object {
        val gemini3Flash = LLModel(
            provider = LLMProvider.OpenRouter,
            id = "google/gemini-3-flash-preview",
            capabilities = listOf(
                LLMCapability.Temperature,
                LLMCapability.Speculation,
                LLMCapability.Tools,
                LLMCapability.Completion,
                LLMCapability.Schema.JSON.Standard,
                LLMCapability.ToolChoice,
                LLMCapability.Vision.Image
            ),
            contextLength = 1_048_576,
            maxOutputTokens = 65_600,
        )
    }
}