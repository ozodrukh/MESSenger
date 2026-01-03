package com.ozodrukh.llm.api

import com.ozodrukh.core.domain.model.Message
import java.util.UUID

val Message.isLLMProvided: Boolean
    get() = senderId == "llm"

fun aiMessage(message: String): Message {
    return Message(
        id = UUID.randomUUID().toString(),
        text = message,
        timestamp = System.currentTimeMillis(),
        senderId = "llm",
    )
}

val emptyAiMessage: Message = aiMessage("")