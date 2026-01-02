package com.ozodrukh.feature.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature.chat.ui.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoute(val chatId: String)

fun NavController.navigateToChat(chatId: String) {
    navigate(ChatRoute(chatId))
}

fun NavGraphBuilder.chatScreen(
    onBackClick: () -> Unit
) {
    composable<ChatRoute> { backStackEntry ->
        val route: ChatRoute = backStackEntry.toRoute()
        ChatScreen(
            chatId = ChatId(route.chatId),
            onBackClick = onBackClick
        )
    }
}