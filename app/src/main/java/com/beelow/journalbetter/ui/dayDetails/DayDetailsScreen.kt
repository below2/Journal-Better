package com.beelow.journalbetter.ui.dayDetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.beelow.journalbetter.ui.dayDetails.components.DateDisplayCard
import com.beelow.journalbetter.ui.dayDetails.components.DetailsActionRow
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailsScreen(
    navController: NavController,
    date: String,
    viewModel: DayDetailsViewModel = viewModel()
) {
    // Parse the date string to a LocalDate object
    val localDate = try {
        LocalDate.parse(date)
    } catch (e: DateTimeParseException) {
        LocalDate.now()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overview") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp), // Good outer padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Calendar date
            DateDisplayCard(
                date = localDate,
                modifier = Modifier.weight(0.55f) // Takes up about half the screen height vertically
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            Column(
                modifier = Modifier.weight(0.45f)
            ) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                DetailsActionRow(
                    icon = Icons.AutoMirrored.Filled.List, // Or Icons.Default.Edit
                    label = "Journal Entries",
                    onClick = {
                        navController.navigate("entries/$date")
                    }
                )
            }
        }
    }
}