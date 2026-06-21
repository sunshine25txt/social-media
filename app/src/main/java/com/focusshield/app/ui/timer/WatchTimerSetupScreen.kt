package com.focusshield.app.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WatchTimerSetupScreen(
    viewModel: TimerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    var selectedDuration by remember { mutableFloatStateOf(15f) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Watch Timer Setup",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Configure your watch allowance window.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            if (isTimerRunning) {
                Text("Timer is currently running!", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.stopTimer(context) }) {
                    Text("Stop Timer")
                }
            } else {
                Text("Set allowance: ${selectedDuration.toInt()} minutes")
                Slider(
                    value = selectedDuration,
                    onValueChange = { selectedDuration = it },
                    valueRange = 5f..60f,
                    steps = 10,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.startTimer(context, selectedDuration.toInt()) }) {
                    Text("Start Timer")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}
