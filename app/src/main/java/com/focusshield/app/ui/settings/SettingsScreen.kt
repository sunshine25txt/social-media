package com.focusshield.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToAppLockSetup: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Playful Interruptions", style = MaterialTheme.typography.titleMedium)
                Text("Show a character when blocked", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = uiState.playfulInterruptions,
                onCheckedChange = { viewModel.togglePlayfulInterruptions(it) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Strict Mode", style = MaterialTheme.typography.titleMedium)
                Text("Require exercise/reflection before dismissing", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = uiState.strictMode,
                onCheckedChange = { viewModel.toggleStrictMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Blocking Style", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("SOFT", "MEDIUM", "HARD").forEach { style ->
                FilterChip(
                    selected = uiState.blockingStyle == style,
                    onClick = { viewModel.setBlockingStyle(style) },
                    label = { Text(style) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToAppLockSetup,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Setup App Lock PIN")
        }
    }
}
