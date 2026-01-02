package com.ozodrukh.mess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ozodrukh.core.theme.MessengerTheme
import com.ozodrukh.feature.user.auth.AuthRoute
import com.ozodrukh.feature.user.auth.AuthScreen
import dev.ioio.estetique.navigation.AppNavigation
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Box(modifier = Modifier) {
                    AppNavigation()
                }
            }
        }
    }
}