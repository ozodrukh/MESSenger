package com.ozodrukh.feature_dialogs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ozodrukh.feature_dialogs.models.ChatId
import com.ozodrukh.feature_dialogs.ui.DialogsScreen

object DialogsRoute {
    const val OpenDialogsList = "dialogs_list"
}

fun NavGraphBuilder.DialogScreen(openDialog: (ChatId) -> Unit, openMyProfile: () -> Unit) {
    composable(DialogsRoute.OpenDialogsList) {
        DialogsScreen(onDialogClick = openDialog, onProfileClick = openMyProfile)
    }
}
