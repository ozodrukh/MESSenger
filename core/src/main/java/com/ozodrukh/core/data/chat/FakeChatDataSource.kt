package com.ozodrukh.core.data.chat

import com.ozodrukh.core.domain.model.Chat
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.core.domain.model.Message
import com.ozodrukh.core.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class FakeChatDataSource {

    private val _chats = MutableStateFlow<Map<ChatId, Chat>>(emptyMap())
    val chats: StateFlow<Map<ChatId, Chat>> = _chats.asStateFlow()

    init {
        val initialChats = listOf(
            createChat(
                "1",
                "James Bond",
                "https://static1.colliderimages.com/wordpress/wp-content/uploads/2024/06/out-of-all-of-james-bond-s-stunts-this-is-the-most-ridiculous.jpg",
                "Remember! The name's Bond. James Bond.",
                2,
                listOf(
                    newMessage("1", "Who are you?", true),
                    newMessage("1", "I'm James Bond, 007.", false),
                    newMessage("1", "What your mission status?", true),
                    newMessage("1", "Batman elimanted?", true),
                    newMessage("1", "Not yet", false),
                ),
            ),
            createChat(
                "2",
                "Sherlock Holmes",
                "https://avatars.mds.yandex.net/i?id=045396352f26db208ae6fbbcdd37c2c38f342c65-3826589-images-thumbs&n=13",
                "Elementary, my dear Watson.",
                0
            ),
            createChat(
                "3",
                "Tony Stark",
                "https://resizer.mail.ru/p/ad500b14-d0f2-538b-9483-1758d7b1934c/AQACOhaOuHR69PTE2UvAS9g0_Fznt6Ow-xziuDak56zewK3GlKSgi6sVYM70i7ds14AefyIwYRWNAaaLtXgNqDLLtUE.jpg",
                "I am Iron Man.",
                0
            ),
            createChat(
                "4",
                "Bruce Wayne",
                "https://m.media-amazon.com/images/M/MV5BZGIwOTk5MzQtZTg2My00MzY2LWFjMjYtOTFhMDcwZmExOGNmXkEyXkFqcGc@._V1_.jpg",
                "I'm Batman.",
                5
            ),
            createChat(
                "5",
                "Walter White",
                "https://i.pinimg.com/originals/58/f2/41/58f2418035f6cd1687a8a5cac191be07.jpg?nii=t",
                "I am the one who knocks!",
                0
            ),
            createChat(
                "6",
                "Wednesday Addams",
                "https://www.looper.com/img/gallery/netflixs-wednesday-the-biggest-differences-between-the-show-and-the-movies/wednesday-goes-from-a-supporting-character-to-the-main-protagonist-1668632413.jpg",
                "I find social media to be a soul-sucking void.",
                0
            )
        )
        _chats.value = initialChats.associateBy { it.id }
    }

    private fun createChat(
        id: String,
        name: String,
        avatar: String,
        lastMsgText: String,
        unread: Int,
        messages: List<Message> = emptyList(),
    ): Chat {
        val chatId = ChatId(id)
        val isOnline = id.hashCode() % 2 == 0
        val lastSeen = System.currentTimeMillis() - (1000 * 60 * (1..60).random()) // 1-60 mins ago
        val user = User(id, name, avatar, isOnline, lastSeen)

        val timestamp = System.currentTimeMillis()

        val msg = Message(
            id = UUID.randomUUID().toString(),
            text = lastMsgText,
            timestamp = timestamp,
            senderId = id
        )

        return Chat(chatId, user, messages + msg, unread)
    }

    fun getChatFlow(chatId: ChatId) = _chats.map { it[chatId] }

    private fun newMessage(chatId: String, text: String, byMe: Boolean): Message {
        return Message(
            id = UUID.randomUUID().toString(),
            text = text,
            timestamp = System.currentTimeMillis(),
            senderId = if (byMe) "me" else chatId
        )
    }

    suspend fun sendMessage(chatId: ChatId, text: String) {
        // Simulating network delay
        delay(300)

        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            text = text,
            timestamp = System.currentTimeMillis(),
            senderId = "me"
        )

        updateChat(chatId) { chat ->
            chat.copy(messages = chat.messages + newMessage)
        }

        // Auto-reply simulation
        simulateAutoReply(chatId, text)
    }

    private suspend fun simulateAutoReply(chatId: ChatId, originalText: String) {
        delay(1000)
        val reply = Message(
            id = UUID.randomUUID().toString(),
            text = "Echo: $originalText",
            timestamp = System.currentTimeMillis(),
            senderId = chatId.value // The other user
        )
        updateChat(chatId) { chat ->
            chat.copy(messages = chat.messages + reply)
        }
    }

    private fun updateChat(chatId: ChatId, transform: (Chat) -> Chat) {
        _chats.update { currentChats ->
            val chat = currentChats[chatId] ?: return@update currentChats
            currentChats + (chatId to transform(chat))
        }
    }
}
