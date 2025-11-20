package com.beelow.journalbetter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.beelow.journalbetter.ui.calendar.CalendarScreen
import com.beelow.journalbetter.ui.entries.EntriesScreen

@Composable
fun JournalNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {
            CalendarScreen(navController = navController)
        }
        composable(
            "details/{date}?quickNote={quickNote}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("quickNote") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val quickNote = backStackEntry.arguments?.getString("quickNote")
            EntriesScreen(date = date, quickNote = quickNote, navController = navController)
        }
    }
}