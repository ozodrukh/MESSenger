package com.ozodrukh.feature_dialogs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ozodrukh.feature_dialogs.models.ChatId
import com.ozodrukh.feature_dialogs.ui.MainScreen

object DialogsRoute {
    const val OpenDialogsList = "dialogs_list"
}

fun NavGraphBuilder.DialogScreen(openDialog: (ChatId) -> Unit) {
    composable(DialogsRoute.OpenDialogsList) {
        MainScreen(onDialogClick = openDialog)
    }
}
