package com.beelow.journalbetter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.beelow.journalbetter.ui.calendar.CalendarScreen
import com.beelow.journalbetter.ui.dayDetails.DayDetailsScreen
import com.beelow.journalbetter.ui.entries.EntriesScreen
import com.beelow.journalbetter.ui.entryDetails.EntryDetailsScreen

@Composable
fun JournalNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {
            CalendarScreen(navController = navController)
        }

        composable(
            "dayDetails/{date}",
            arguments = listOf(navArgument("date") {
                type = NavType.StringType
                nullable = false
            })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
                ?.takeIf { it.isNotBlank() } ?: "0000-01-01"

            DayDetailsScreen(
                navController = navController,
                date = date
            )
        }

        composable(
            "entries/{date}?quickNote={quickNote}",
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("quickNote") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
                ?.takeIf { it.isNotBlank() } ?: "0000-01-01"
            val quickNote = backStackEntry.arguments?.getString("quickNote")

            EntriesScreen(
                date = date,
                quickNote = quickNote,
                navController = navController
            )
        }

        composable(
            "entryDetails/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId") ?: return@composable

            EntryDetailsScreen(
                navController = navController,
                entryId = entryId
            )
        }
    }
}