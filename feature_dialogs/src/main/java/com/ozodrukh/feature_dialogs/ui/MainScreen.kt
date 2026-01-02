package com.ozodrukh.feature_dialogs.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ozodrukh.feature_dialogs.models.ChatId
import com.ozodrukh.feature_dialogs.ui.dialogs.DialogsListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onDialogClick: (ChatId) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "chats" } == true,
                    onClick = {
                        navController.navigate("chats") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "chats",
            modifier = Modifier.padding(padding)
        ) {
            composable("chats") {
                DialogsListScreen(onDialogClick = onDialogClick)
            }
            composable("profile") {
                ProfileScreen()
            }
        }
    }
}
