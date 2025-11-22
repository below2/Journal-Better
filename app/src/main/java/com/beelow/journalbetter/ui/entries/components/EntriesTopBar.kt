package com.beelow.journalbetter.ui.entries.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.beelow.journalbetter.ui.entries.BulkOperation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesTopBar(
    title: String,
    inSelectMode: Boolean,
    isMenuExpanded: Boolean,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onBulkOperation: (BulkOperation) -> Unit,
    onApplySelection: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            // TopAppBar icon
            IconButton(onClick = {
                if (inSelectMode) {
                    onApplySelection()
                } else {
                    onMenuClick()
                }
            }) {
                Icon(
                    if (inSelectMode) Icons.Filled.Check else Icons.Filled.Menu,
                    contentDescription = if (inSelectMode) "Apply" else "Menu"
                )
            }
            // TopAppBar dropdown
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = { onBulkOperation(BulkOperation.DELETE) }
                )
                DropdownMenuItem(
                    text = { Text("Hide") },
                    onClick = { onBulkOperation(BulkOperation.HIDE) }
                )
            }
        }
    )
}