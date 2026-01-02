package com.ozodrukh.feature_dialogs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ozodrukh.core.domain.model.ChatId
import com.ozodrukh.feature_dialogs.ui.MainScreen

object DialogsRoute {
    const val OpenDialogsList = "dialogs_list"
}

fun NavGraphBuilder.dialogScreen(openDialog: (ChatId) -> Unit, onLogout: () -> Unit) {
    composable(DialogsRoute.OpenDialogsList) {
        MainScreen(onDialogClick = openDialog, onLogout = onLogout)
    }
}