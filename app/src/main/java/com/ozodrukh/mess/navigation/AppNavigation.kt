package dev.ioio.estetique.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ozodrukh.auth.session.IsAuthenticated
import com.ozodrukh.feature.user.auth.AuthRoute
import com.ozodrukh.feature.user.auth.AuthScreen
import com.ozodrukh.feature_dialogs.DialogScreen
import com.ozodrukh.feature_dialogs.DialogsRoute
import org.koin.compose.koinInject
import timber.log.Timber

@Composable
fun AppNavigation(
    checkAuth: IsAuthenticated = koinInject()
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val isAuth = checkAuth.isAuthenticated()
        startDestination = if (isAuth) DialogsRoute.OpenDialogsList else AuthRoute.Authenticate
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
        ) {
            AuthScreen {
                navController.navigate(DialogsRoute.OpenDialogsList) {
                    popUpTo(AuthRoute.Authenticate) { inclusive = true }
                }
            }

            DialogScreen(
                openDialog = { chatId -> Timber.d("Open dialog: $chatId") },
                openMyProfile = { Timber.d("Open profile") }
            )
        }
    }
}