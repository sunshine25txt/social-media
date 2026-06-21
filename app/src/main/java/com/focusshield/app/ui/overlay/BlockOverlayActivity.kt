package com.focusshield.app.ui.overlay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusshield.app.data.local.entity.PauseEventEntity
import com.focusshield.app.domain.repository.BlockedEventRepository
import com.focusshield.app.domain.repository.PauseRepository
import com.focusshield.app.ui.theme.FocusShieldTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BlockOverlayActivity : ComponentActivity() {

    @Inject
    lateinit var pauseRepository: PauseRepository

    @Inject
    lateinit var blockedEventRepository: BlockedEventRepository

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val blockedPackage = intent.getStringExtra("BLOCKED_PACKAGE") ?: "Unknown App"
        val blockedSurface = intent.getStringExtra("BLOCKED_SURFACE") ?: "Feed"
        
        setContent {
            FocusShieldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.92f)
                ) {
                    var showPauseDialog by remember { mutableStateOf(false) }
                    var totalBlocksToday by remember { mutableStateOf(0) }
                    
                    LaunchedEffect(Unit) {
                        val todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000
                        val events = blockedEventRepository.getEventsToday(todayStart).first()
                        totalBlocksToday = events.size
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Shield Locked",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Focus Shield Active",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We blocked access to $blockedSurface on ${getAppLabel(blockedPackage)}.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$totalBlocksToday Blocks Today",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Estimated ${totalBlocksToday * 30 / 60} minutes saved from scrolling.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    goHome()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Close App & Stay Focused",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            OutlinedButton(
                                onClick = { showPauseDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Request Temporary Pause",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    if (showPauseDialog) {
                        PauseRequestDialog(
                            onDismiss = { showPauseDialog = false },
                            onSubmit = { duration, reason ->
                                showPauseDialog = false
                                savePause(duration, reason, blockedPackage)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun getAppLabel(packageName: String): String {
        return when (packageName) {
            "com.instagram.android" -> "Instagram"
            "com.zhiliaoapp.musically" -> "TikTok"
            "com.google.android.youtube" -> "YouTube"
            "com.facebook.katana" -> "Facebook"
            "com.facebook.lite" -> "Facebook Lite"
            "com.kwai.video" -> "SnackVideo"
            else -> packageName.split(".").lastOrNull()?.replaceFirstChar { it.uppercase() } ?: packageName
        }
    }

    private fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun savePause(durationMinutes: Int, reason: String, packageName: String) {
        activityScope.launch {
            pauseRepository.insert(
                PauseEventEntity(
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = durationMinutes,
                    reason = reason,
                    appPackage = packageName
                )
            )
            // Pause saved successfully! Re-launch the blocked app now that it's allowed
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(launchIntent)
            }
            finish()
        }
    }
}

@Composable
fun PauseRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var selectedDuration by remember { mutableIntStateOf(5) }
    var selectedReason by remember { mutableStateOf("EDUCATIONAL") }
    
    val durations = listOf(5, 10, 15)
    val reasons = listOf(
        "EDUCATIONAL" to "Learning/Educational Content",
        "WORK" to "Work Related",
        "EMERGENCY" to "Emergency/Essential Communication",
        "OTHER" to "Other Justification"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pause Shield Request")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Select Pause Duration:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durations.forEach { mins ->
                            val isSelected = selectedDuration == mins
                            Button(
                                onClick = { selectedDuration = mins },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("$mins min")
                            }
                        }
                    }
                }
                
                Column {
                    Text("Justification Reason:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    reasons.forEach { (key, label) ->
                        val isSelected = selectedReason == key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = key }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedReason = key }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedDuration, selectedReason) }
            ) {
                Text("Grant Temporary Pause")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
