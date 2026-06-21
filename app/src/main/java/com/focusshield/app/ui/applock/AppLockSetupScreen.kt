package com.focusshield.app.ui.applock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.focusshield.app.data.local.entity.AppLockEntity
import com.focusshield.app.domain.repository.AppLockRepository
import com.focusshield.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockSetupScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activity = androidx.compose.ui.platform.LocalContext.current as? androidx.fragment.app.FragmentActivity
    val appLockRepo = (activity as? AppLockActivity)?.appLockRepository ?: (activity as? com.focusshield.app.ui.MainActivity)?.appLockRepository
    val userPrefsRepo = (activity as? AppLockActivity)?.userPreferencesRepository ?: (activity as? com.focusshield.app.ui.MainActivity)?.userPreferencesRepository

    val availableApps = mapOf(
        "com.instagram.android" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.google.android.youtube" to "YouTube",
        "com.facebook.katana" to "Facebook",
        "com.facebook.lite" to "Facebook Lite",
        "com.kwai.video" to "SnackVideo"
    )

    var pinText by remember { mutableStateOf("") }
    var confirmPinText by remember { mutableStateOf("") }
    var hasExistingPin by remember { mutableStateOf(false) }
    var locksList by remember { mutableStateOf<List<AppLockEntity>>(emptyList()) }
    var showPinDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (userPrefsRepo != null) {
            val pin = userPrefsRepo.pinHash.first()
            hasExistingPin = !pin.isNullOrEmpty()
        }
        if (appLockRepo != null) {
            appLockRepo.getAllLocks().collect { list ->
                locksList = list
            }
        }
    }

    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Lock Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Pin Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (hasExistingPin) "Security PIN Enabled" else "Security PIN Required",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (hasExistingPin) "Enter your PIN when launching restricted applications to gain access."
                               else "Create a 4-digit PIN to prevent accessing restricted apps without authentication.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showPinDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(if (hasExistingPin) "Change PIN" else "Set 4-Digit PIN")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lock Specific Apps",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Locked apps will require your Focus Shield PIN immediately upon launch.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableApps.toList()) { (pkg, name) ->
                    val isLocked = locksList.any { it.packageName == pkg && it.isLocked }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLocked) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isLocked) "Fully Locked" else "Unlocked",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isLocked) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                            
                            Switch(
                                checked = isLocked,
                                enabled = hasExistingPin,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch {
                                        if (appLockRepo != null) {
                                            if (checked) {
                                                appLockRepo.insertOrUpdate(
                                                    AppLockEntity(
                                                        packageName = pkg,
                                                        lockMethod = "PIN",
                                                        isLocked = true
                                                    )
                                                )
                                            } else {
                                                appLockRepo.delete(
                                                    AppLockEntity(
                                                        packageName = pkg,
                                                        lockMethod = "PIN",
                                                        isLocked = false
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pinText = ""
                confirmPinText = ""
            },
            title = { Text(if (hasExistingPin) "Change PIN" else "Set Security PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 4) pinText = it },
                        label = { Text("Enter 4-Digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = confirmPinText,
                        onValueChange = { if (it.length <= 4) confirmPinText = it },
                        label = { Text("Confirm 4-Digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinText.length == 4 && pinText == confirmPinText) {
                            coroutineScope.launch {
                                userPrefsRepo?.setPinHash(hashPin(pinText))
                                hasExistingPin = true
                                showPinDialog = false
                                pinText = ""
                                confirmPinText = ""
                                message = "PIN saved successfully!"
                            }
                        } else {
                            message = "PINs must match and be exactly 4 digits."
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    pinText = ""
                    confirmPinText = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
