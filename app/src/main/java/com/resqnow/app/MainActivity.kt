package com.resqnow.app

import android.Manifest
import android.app.ActivityManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.resqnow.app.ui.theme.ResQNowTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResQNowTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("ResQNowPrefs", Context.MODE_PRIVATE)
    
    var contacts by remember { 
        mutableStateOf(sharedPrefs.getStringSet("emergency_contacts", emptySet())?.toList() ?: emptyList()) 
    }
    var newContact by remember { mutableStateOf("") }
    
    var startTime by remember { 
        mutableStateOf(sharedPrefs.getString("start_time", "09:00") ?: "09:00") 
    }
    var endTime by remember { 
        mutableStateOf(sharedPrefs.getString("end_time", "20:00") ?: "20:00") 
    }

    var isServiceRunning by remember { 
        mutableStateOf(isServiceRunning(context, EmergencyDetectionService::class.java)) 
    }

    val permissionsToRequest = mutableListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.SEND_SMS
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(context, "Permissions Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Some permissions were denied.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "ResQNow", style = MaterialTheme.typography.headlineLarge)
            Text(text = "Hands-free Emergency Detection", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { launcher.launch(permissionsToRequest) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check/Grant Permissions")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    showTimePicker(context, startTime) { newTime ->
                        startTime = newTime
                        sharedPrefs.edit().putString("start_time", newTime).apply()
                    }
                }) {
                    Text("Start: $startTime")
                }
                
                TextButton(onClick = {
                    showTimePicker(context, endTime) { newTime ->
                        endTime = newTime
                        sharedPrefs.edit().putString("end_time", newTime).apply()
                    }
                }) {
                    Text("End: $endTime")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Contacts
            OutlinedTextField(
                value = newContact,
                onValueChange = { newContact = it },
                label = { Text("Add Emergency Contact Number") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = {
                    if (newContact.isNotBlank()) {
                        contacts = contacts + newContact
                        sharedPrefs.edit().putStringSet("emergency_contacts", contacts.toSet()).apply()
                        newContact = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Contact")
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(contacts) { contact ->
                    ListItem(
                        headlineContent = { Text(contact) },
                        trailingContent = {
                            IconButton(onClick = {
                                contacts = contacts - contact
                                sharedPrefs.edit().putStringSet("emergency_contacts", contacts.toSet()).apply()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, EmergencyDetectionService::class.java)
                    if (isServiceRunning) {
                        context.stopService(intent)
                        isServiceRunning = false
                    } else {
                        if (contacts.isEmpty()) {
                            Toast.makeText(context, "Please add at least one contact", Toast.LENGTH_SHORT).show()
                        } else {
                            ContextCompat.startForegroundService(context, intent)
                            isServiceRunning = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isServiceRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isServiceRunning) "STOP MONITORING" else "START MONITORING")
            }
        }
    }
}

fun showTimePicker(context: Context, currentTime: String, onTimeSelected: (String) -> Unit) {
    val parts = currentTime.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()

    TimePickerDialog(context, { _, h, m ->
        onTimeSelected(String.format(Locale.getDefault(), "%02d:%02d", h, m))
    }, hour, minute, true).show()
}

@Suppress("DEPRECATION")
fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}
