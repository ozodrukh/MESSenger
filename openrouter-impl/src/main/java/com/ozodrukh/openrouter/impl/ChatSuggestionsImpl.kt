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
                system("""
                    You are an AI acting as a ghostwriter for "me" in a chat conversation. 

                    Your task is to generate the **next immediate response** to "other_peer".

                    ### Instructions:
                    1.  **Style Matching**: Analyze the messages labeled "me" in the provided history. Mimic the following traits:
                        *   **Capitalization**: (e.g., do I use proper caps or all lowercase?)
                        *   **Punctuation**: (e.g., do I use periods at the end of sentences? Exclamation points? Ellipses?)
                        *   **Length**: (e.g., do I write long paragraphs or short bursts?)
                        *   **Tone**: (e.g., professional, casual, sarcastic, or direct?)
                    2.  **Context Logic**: Read the last message from "other_peer" and formulate a logical reply that moves the conversation forward or answers their question.
                    3.  **Formatting Constraints**: 
                        *   Output **ONLY** the raw text of the reply. 
                        *   Do NOT include "Me:", "Response:", or quotes.
                        *   Do NOT provide an explanation of why you wrote it.

                    ### (Optional) My Intent:
                    [If you have a specific goal, type it here (e.g., "Agree with them" or "Tell them I'm busy"). If not, leave blank.]
                """.trimIndent())
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

        return llmResponse.firstOrNull()?.let { response -> aiMessage(response.content) }
            ?: emptyAiMessage
    }

    override suspend fun createSummary(history: List<Message>): Message {
        val llmResponse = openRouterClient.execute(
            prompt = prompt("message-suggestion") {
                system(
                    """
                        You are an expert meeting secretary and project manager. I am providing a chat transcript between two people ("me" and "other_peer").

                        Your task is to analyze the conversation and generate a structured Markdown summary. 

                        Please follow these rules:
                        1. **Filter Noise**: Ignore casual small talk or greetings; focus only on substantial information.
                        2. **Assign Ownership**: For action items, clearly state WHO is responsible for the task based on the context.
                        3. **Format**: Use the specific Markdown structure outlined below.

                        ### Output Format:

                        # Conversation Summary
                        **Overview**: [A 1-2 sentence high-level summary of the entire discussion]

                        ## 🔑 Key Topics
                        *   **[Topic Name]**: Brief explanation of what was discussed.
                        *   **[Topic Name]**: Brief explanation of what was discussed.

                        ## 🚀 Action Items
                        - [ ] **[Who]**: [Task description]
                        - [ ] **[Who]**: [Task description]
                    """.trimIndent()
                )
                user(
                    "**Chat History:**\n" +
                    history.joinToString("\n") {
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

        return llmResponse.firstOrNull()?.let { response -> aiMessage(response.content) }
            ?: emptyAiMessage
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