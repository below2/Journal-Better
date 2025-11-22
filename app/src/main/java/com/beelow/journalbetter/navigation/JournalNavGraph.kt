package com.beelow.journalbetter.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.beelow.journalbetter.ui.calendar.CalendarScreen
import com.beelow.journalbetter.ui.entries.EntriesScreen
import com.beelow.journalbetter.ui.entries.EntryDetailsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JournalNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    NavHost(navController = navController, startDestination = "calendar") {
        // Calendar
        composable("calendar") {
            CalendarScreen(
                navController = navController,
                onLogout = onLogout
            )
        }
        // Entries
        composable(
            "details/{date}?quickNote={quickNote}",
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
            var date = backStackEntry.arguments?.getString("date")
            val quickNote = backStackEntry.arguments?.getString("quickNote")
            if (date.isNullOrBlank()) {
                date = "0000-01-01"
            }

            EntriesScreen(date = date, quickNote = quickNote, navController = navController)
        }
        // Entry Details
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