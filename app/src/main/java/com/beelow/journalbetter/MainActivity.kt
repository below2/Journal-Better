package com.beelow.journalbetter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.beelow.journalbetter.navigation.JournalNavGraph
import com.beelow.journalbetter.ui.theme.JournalBetterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JournalBetterTheme {
                val navController = rememberNavController()
                JournalNavGraph(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JournalBetterTheme {
        val navController = rememberNavController()
        JournalNavGraph(navController = navController)
    }
}