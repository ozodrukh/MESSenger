package com.ozodrukh.feature.profile.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ozodrukh.feature.profile.domain.model.UserProfile
import com.ozodrukh.feature.profile.ui.screen.EditProfileScreen
import com.ozodrukh.feature.profile.ui.screen.ProfileScreen

object ProfileRoute {
    const val Viewer = "profile_viewer"
    const val Editor = "profile_editor"
}

fun NavGraphBuilder.profileScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    composable(ProfileRoute.Viewer) {
        ProfileScreen(
            onEditProfile = { profile ->
                navController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                navController.navigate(ProfileRoute.Editor)
            },
            onLogout = onLogout
        )
    }

    composable(ProfileRoute.Editor) {
        val previousBackStackEntry = navController.previousBackStackEntry
        val profile = previousBackStackEntry?.savedStateHandle?.get<UserProfile>("profile")

        if (profile != null) {
            EditProfileScreen(
                profile = profile,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
