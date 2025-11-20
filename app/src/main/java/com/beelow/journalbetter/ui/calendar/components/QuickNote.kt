package com.beelow.journalbetter.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Composable
fun QuickNote(navController: NavController) {
    var quickNote by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedTextField(
            value = quickNote,
            onValueChange = { quickNote = it },
            label = { Text("Quick note") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(25)
        )
        FloatingActionButton(
            onClick = {
                val year = LocalDateTime.now().year.toString()
                val month = LocalDateTime.now().monthValue.toString()
                val day = LocalDateTime.now().dayOfMonth.toString()
                val date = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
                val encodedQuickNote =
                    URLEncoder.encode(quickNote, StandardCharsets.UTF_8.toString())
                navController.navigate("details/${date}?quickNote=$encodedQuickNote")
                quickNote = ""
            },
            modifier = Modifier.padding(start = 16.dp),
            shape = RoundedCornerShape(50)
        ) {
            Icon(Icons.Filled.Add, "Add journal entry")
        }
    }
}