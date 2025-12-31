package com.ozodrukh.feature_dialogs.data

import com.ozodrukh.feature_dialogs.models.ChatId
import com.ozodrukh.feature_dialogs.models.Dialog

object MockData {
    val dialogs = listOf(
        Dialog(
            id = ChatId("1"),
            name = "James Bond",
            lastMessage = "The name's Bond. James Bond.",
            avatarUrl = "https://i.pravatar.cc/150?u=1",
            timestamp = "14:07",
            unreadCount = 2
        ),
        Dialog(
            id = ChatId("2"),
            name = "Sherlock Holmes",
            lastMessage = "Elementary, my dear Watson.",
            avatarUrl = "https://i.pravatar.cc/150?u=2",
            timestamp = "13:45"
        ),
        Dialog(
            id = ChatId("3"),
            name = "Tony Stark",
            lastMessage = "I am Iron Man.",
            avatarUrl = "https://i.pravatar.cc/150?u=3",
            timestamp = "Yesterday"
        ),
        Dialog(
            id = ChatId("4"),
            name = "Bruce Wayne",
            lastMessage = "I'm Batman.",
            avatarUrl = "https://i.pravatar.cc/150?u=4",
            timestamp = "Yesterday",
            unreadCount = 5
        ),
        Dialog(
            id = ChatId("5"),
            name = "Walter White",
            lastMessage = "I am the one who knocks!",
            avatarUrl = "https://i.pravatar.cc/150?u=5",
            timestamp = "Monday"
        ),
        Dialog(
            id = ChatId("6"),
            name = "Wednesday Addams",
            lastMessage = "I find social media to be a soul-sucking void.",
            avatarUrl = "https://i.pravatar.cc/150?u=6",
            timestamp = "Dec 28"
        )
    )
}
