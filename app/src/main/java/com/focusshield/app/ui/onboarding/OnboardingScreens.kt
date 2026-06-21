package com.focusshield.app.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusshield.app.domain.usecase.PermissionStates
import com.focusshield.app.ui.theme.PrimaryDark
import com.focusshield.app.ui.theme.PrimaryLight
import com.focusshield.app.ui.theme.PrimarySurface

@Composable
fun OnboardingProgressHeader(currentStep: Int, totalSteps: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 0) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
        
        LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / totalSteps },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        
        Text(
            text = "${currentStep + 1}/$totalSteps",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryDark, PrimarySurface, Color(0xFF121212))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                // Logo Icon Placeholder
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Focus Shield Logo",
                    tint = PrimaryLight,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "FOCUS SHIELD",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reclaim your focus. Block short-form video addiction while leaving educational long-form content fully accessible.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "By continuing, you agree to our Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GoalSelectionScreen(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    val goals = listOf(
        Triple("REDUCE_ADDICTION", "Reduce Loop Addiction", "Break the habit of scrolling Reels, Shorts, and TikTok endlessly."),
        Triple("STUDY", "Study & Exams", "Boost your study output. Lock down addictive feeds during school hours."),
        Triple("WORK", "Deep Work & Productivity", "Stay focused during office hours. Prevent distraction leaks."),
        Triple("CUSTOM", "Custom Routine", "Create your own rules, schedule limits, and allowance timers.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "What is your primary goal?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "We will tailor your protection and warning escalation settings based on your focus objective.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(goals) { (key, title, desc) ->
                val isSelected = selectedGoal == key
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGoalSelected(key) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            colors = if (isSelected) listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary) else listOf(Color.Transparent, Color.Transparent)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onGoalSelected(key) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AppSelectionScreen(
    availableApps: Map<String, String>,
    selectedApps: Set<String>,
    onAppToggled: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Which apps do you scroll?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "Select the apps where you want Focus Shield to detect and restrict addictive short-form feeds.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableApps.toList()) { (pkg, name) ->
                val isSelected = selectedApps.contains(pkg)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAppToggled(pkg) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (name) {
                                "YouTube" -> Icons.Default.PlayCircle
                                "Instagram" -> Icons.Default.CameraAlt
                                "TikTok" -> Icons.Default.MusicNote
                                "Facebook", "Facebook Lite" -> Icons.Default.Web
                                else -> Icons.Default.Android
                            },
                            contentDescription = name,
                            tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onAppToggled(pkg) }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BlockingStyleScreen(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    val styles = listOf(
        Triple("SOFT", "Soft Guidance", "Gentle warning banners and vibration. Best if you just need a minor reminder to lock your focus."),
        Triple("MEDIUM", "Restricted Shield", "Live watch timers and blur screens. Gives you a strict allowance before locking Reels/Shorts."),
        Triple("HARD", "Hard Block", "Extreme block overlay. Fully locks down Reels/Shorts with reflection challenges or passcode/biometric unlocks.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Select your warning style",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "You can change this later in Settings. Choose how hard the app restricts your access.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(styles) { (key, title, desc) ->
                val isSelected = selectedStyle == key
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStyleSelected(key) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onStyleSelected(key) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AccessibilityDisclosureScreen(
    onAgree: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.AccessibilityNew,
                    contentDescription = "Accessibility",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Accessibility Permission Disclosure",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Focus Shield uses the Accessibility Service API in order to:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text("• ", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Read active screen elements in order to identify Reels, Shorts, video players, and feed containers.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text("• ", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Detect which applications are currently open and running in the foreground.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = "Privacy",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Focus Shield does NOT collect, store, or transmit any user screen data, input keystrokes, or personal credentials. All content analysis happens in real-time, offline, on your local device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Go Back")
                }
                Button(
                    onClick = onAgree,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Agree & Grant", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PermissionSetupScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val permissionStates by viewModel.permissionStates.collectAsState()
    
    // Register settings launcher to recheck permissions on return
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkPermissions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Configure permissions",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "Focus Shield requires the following permissions to actively monitor your usage and block loop feeds.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Accessibility Service (Mandatory)
            item {
                PermissionItem(
                    title = "Accessibility Service",
                    desc = "Used to analyze screens and block addictive Reels/Shorts containers.",
                    isGranted = permissionStates.hasAccessibility,
                    onRequest = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        settingsLauncher.launch(intent)
                    }
                )
            }

            // Step 2: Usage Access (Mandatory)
            item {
                PermissionItem(
                    title = "Usage Access",
                    desc = "Used to trace which apps are in the foreground and track daily time limits.",
                    isGranted = permissionStates.hasUsageStats,
                    onRequest = {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        settingsLauncher.launch(intent)
                    }
                )
            }

            // Step 3: Draw Over Other Apps / Overlays (Mandatory)
            item {
                PermissionItem(
                    title = "Overlay Permission",
                    desc = "Used to draw the full-screen warning/reflection shield over addictive apps.",
                    isGranted = permissionStates.hasOverlay,
                    onRequest = {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        settingsLauncher.launch(intent)
                    }
                )
            }

            // Step 4: Notification Permission (Tiramisu+)
            item {
                PermissionItem(
                    title = "Notifications",
                    desc = "Used to show persistent study status and floating allowance timers.",
                    isGranted = permissionStates.hasNotifications,
                    onRequest = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            settingsLauncher.launch(intent)
                        } else {
                            viewModel.checkPermissions()
                        }
                    }
                )
            }
        }

        Button(
            onClick = {
                viewModel.completeOnboarding(onSuccess = onComplete)
            },
            enabled = permissionStates.allGranted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (permissionStates.allGranted) "Complete Setup" else "Grant All to Finish",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    desc: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (isGranted) "Granted" else "Pending",
                tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isGranted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (!isGranted) {
                Button(
                    onClick = onRequest,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Grant", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingWizardScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    val totalSteps = 5 // Goal, Apps, Style, Disclosure, Permissions (Welcome screen does not show progress bar)
    
    val selectedGoal by viewModel.selectedGoal.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    val blockingStyle by viewModel.blockingStyle.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (currentStep > 0) {
            OnboardingProgressHeader(
                currentStep = currentStep - 1,
                totalSteps = totalSteps,
                onBack = { currentStep-- }
            )
        }
        
        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                0 -> WelcomeScreen(onNext = { currentStep++ })
                1 -> GoalSelectionScreen(
                    selectedGoal = selectedGoal,
                    onGoalSelected = { viewModel.setGoal(it) },
                    onNext = { currentStep++ }
                )
                2 -> AppSelectionScreen(
                    availableApps = viewModel.availableApps,
                    selectedApps = selectedApps,
                    onAppToggled = { viewModel.toggleAppSelection(it) },
                    onNext = { currentStep++ }
                )
                3 -> BlockingStyleScreen(
                    selectedStyle = blockingStyle,
                    onStyleSelected = { viewModel.setBlockingStyle(it) },
                    onNext = { currentStep++ }
                )
                4 -> AccessibilityDisclosureScreen(
                    onAgree = { currentStep++ },
                    onBack = { currentStep-- }
                )
                5 -> PermissionSetupScreen(
                    viewModel = viewModel,
                    onComplete = onOnboardingComplete
                )
            }
        }
    }
}
