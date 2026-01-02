package com.ozodrukh.feature.user.auth

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ozodrukh.feature.user.auth.screens.OtpScreen
import com.ozodrukh.feature.user.auth.screens.SignInScreen
import com.ozodrukh.feature.user.auth.screens.SignUpScreen
import org.koin.compose.viewmodel.koinViewModel

object AuthRoute {
    const val Authenticate = "auth_screen"
}

fun NavGraphBuilder.authScreen(onAuthSuccess: () -> Unit) {
    composable(AuthRoute.Authenticate) {
        AuthNavigation(onAuthSuccess)
    }
}

@Composable
fun AuthNavigation(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.NavigateToOtp -> {
                    navController.navigate("otp")
                }

                is AuthUiEvent.NavigateToHome -> {
                    onAuthSuccess()
                }

                is AuthUiEvent.NavigateToSignUp -> {
                    navController.navigate("sign_up") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                }

                is AuthUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "sign_in"
    ) {
        composable("sign_in") {
            SignInScreen(viewModel = viewModel)
        }
        composable("otp") {
            OtpScreen(viewModel = viewModel)
        }
        composable("sign_up") {
            SignUpScreen(viewModel = viewModel)
        }
    }
}
