package com.beelow.journalbetter

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.beelow.journalbetter.navigation.JournalNavGraph
import com.beelow.journalbetter.ui.login.AuthViewModel
import com.beelow.journalbetter.ui.login.LoginScreen
import com.beelow.journalbetter.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()
            if (uiState.isLoggedIn) {
                AppTheme {
                    val navController = rememberNavController()
                    JournalNavGraph(
                        navController = navController,
                        onLogout = { authViewModel.logout() },
                    )
                }
            } else {
                AppTheme {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        val navController = rememberNavController()
        JournalNavGraph(
            navController = navController,
            onLogout = { }
        )
    }
}