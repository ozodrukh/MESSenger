package dev.ioio.estetique.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ozodrukh.auth.session.AuthEvent
import com.ozodrukh.auth.session.IsAuthenticated
import com.ozodrukh.auth.session.SessionManager
import com.ozodrukh.feature.chat.chatScreen
import com.ozodrukh.feature.chat.navigateToChat
import com.ozodrukh.feature.user.auth.AuthRoute
import com.ozodrukh.feature.user.auth.authScreen
import com.ozodrukh.feature_dialogs.dialogScreen
import com.ozodrukh.feature_dialogs.DialogsRoute
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    checkAuth: IsAuthenticated = koinInject(),
    sessionManager: SessionManager = koinInject()
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val isAuth = checkAuth.isAuthenticated()
        startDestination = if (isAuth) DialogsRoute.OpenDialogsList else AuthRoute.Authenticate

        sessionManager.events.collect { event ->
            when (event) {
                AuthEvent.SessionExpired -> {
                    navController.navigate(AuthRoute.Authenticate) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
        ) {
            authScreen {
                navController.navigate(DialogsRoute.OpenDialogsList) {
                    popUpTo(AuthRoute.Authenticate) { inclusive = true }
                }
            }

            dialogScreen(
                openDialog = { chatId ->
                    navController.navigateToChat(chatId.value)
                },
                onLogout = {
                    navController.navigate(AuthRoute.Authenticate)
                }
            )

            chatScreen(onBackClick = {
                navController.popBackStack()
            })
        }
    }
}