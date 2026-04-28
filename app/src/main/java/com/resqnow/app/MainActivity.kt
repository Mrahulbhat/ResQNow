package com.resqnow.app

import android.Manifest
import android.app.ActivityManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resqnow.app.ui.theme.ResQNowTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResQNowTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") { ResQNowScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("details") { DetailsScreen(navController) }
                    composable("dashboard") { DashboardScreen(navController) }
                    composable("profile") { ProfileScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun ResQNowScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "🛡️", fontSize = 80.sp)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ResQNow",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Stay Safe • Stay Connected",
                    fontSize = 22.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your voice-powered emergency companion.",
                    fontSize = 16.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE11D48)
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "🛡️",
                fontSize = 60.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Login to your ResQNow account",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = Color(0xFF64748B)) },
                placeholder = { Text("user@example.com", color = Color(0xFFCBD5E1)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    disabledContainerColor = Color(0xFFF8FAFC),
                    focusedIndicatorColor = Color(0xFFE11D48),
                    unfocusedIndicatorColor = Color(0xFFE2E8F0),
                    focusedLabelColor = Color(0xFFE11D48),
                    unfocusedLabelColor = Color(0xFF64748B),
                    cursorColor = Color(0xFFE11D48),
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A)
                ),
                singleLine = true
            )

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color(0xFF64748B)) },
                placeholder = { Text("Enter your password", color = Color(0xFFCBD5E1)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    disabledContainerColor = Color(0xFFF8FAFC),
                    focusedIndicatorColor = Color(0xFFE11D48),
                    unfocusedIndicatorColor = Color(0xFFE2E8F0),
                    focusedLabelColor = Color(0xFFE11D48),
                    unfocusedLabelColor = Color(0xFF64748B),
                    cursorColor = Color(0xFFE11D48),
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A)
                ),
                singleLine = true
            )

            // Forgot Password Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot your password?",
                    fontSize = 13.sp,
                    color = Color(0xFFE11D48),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Login Button
            Button(
                onClick = {
                    navController.navigate("details") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE11D48),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Divider with OR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Color(0xFFE2E8F0)
                )
                Text(
                    text = "OR",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Google Login Button
            OutlinedButton(
                onClick = {
                    // TODO: Handle Google login
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF0F172A)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0)
                )
            ) {
                Text(
                    text = "🔍 Continue with Google",
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Link
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Sign Up",
                    fontSize = 13.sp,
                    color = Color(0xFFE11D48),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("ResQNowPrefs", Context.MODE_PRIVATE)

    val parentNumber = sharedPrefs.getString("parent_number", "") ?: ""
    
    var contacts by remember {
        val savedContacts = sharedPrefs.getStringSet("emergency_contacts", emptySet()) ?: emptySet()
        val allContacts = if (parentNumber.isNotBlank()) savedContacts + parentNumber else savedContacts
        mutableStateOf(allContacts.toList())
    }
    var newContact by remember { mutableStateOf("") }

    var startTime by remember {
        mutableStateOf(sharedPrefs.getString("start_time", "09:00") ?: "09:00")
    }
    var endTime by remember {
        mutableStateOf(sharedPrefs.getString("end_time", "20:00") ?: "20:00")
    }

    var isTestMode by remember {
        mutableStateOf(sharedPrefs.getBoolean("test_mode", false))
    }

    var isServiceRunning by remember {
        mutableStateOf(isServiceRunning(context, EmergencyDetectionService::class.java))
    }

    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(context, "All Permissions Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Some permissions were denied. App may not function correctly.", Toast.LENGTH_LONG).show()
        }
    }

    fun arePermissionsGranted(): Boolean {
        return permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ResQNow Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!arePermissionsGranted()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Permissions Required", style = MaterialTheme.typography.titleMedium)
                        Text("Please grant all permissions to enable monitoring.", style = MaterialTheme.typography.bodySmall)
                        Button(
                            onClick = { launcher.launch(permissionsToRequest) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Grant Permissions")
                        }
                    }
                }
            }

            // Time Selection
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Active Monitoring Hours", style = MaterialTheme.typography.titleMedium)
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Test Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Test Mode", style = MaterialTheme.typography.titleMedium)
                    Text("Simulate SOS without sending SMS", style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = isTestMode,
                    onCheckedChange = {
                        isTestMode = it
                        sharedPrefs.edit().putBoolean("test_mode", it).apply()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Contacts - Removed manual entry as per user request
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Monitoring Contacts",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(contacts) { contact ->
                    val isParent = contact == parentNumber
                    ListItem(
                        headlineContent = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(contact)
                                if (isParent) {
                                    Badge(
                                        containerColor = Color(0xFF10B981),
                                        contentColor = Color.White,
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text("Primary", modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }
                        },
                        trailingContent = {
                            if (!isParent) {
                                IconButton(onClick = {
                                    contacts = contacts - contact
                                    sharedPrefs.edit().putStringSet("emergency_contacts", contacts.toSet()).apply()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            } else {
                                IconButton(onClick = { navController.navigate("profile") }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit in Profile")
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!arePermissionsGranted()) {
                        launcher.launch(permissionsToRequest)
                        return@Button
                    }
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
                    containerColor = if (isServiceRunning) Color(0xFFE11D48) else Color(0xFF10B981)
                )
            ) {
                Icon(Icons.Default.Security, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isServiceRunning) "STOP MONITORING" else "START MONITORING")
            }
        }
    }
}

@Composable
fun DetailsScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("ResQNowPrefs", Context.MODE_PRIVATE)

    var name by remember { mutableStateOf(sharedPrefs.getString("user_name", "") ?: "") }
    var emergencyNumber by remember { mutableStateOf(sharedPrefs.getString("parent_number", "") ?: "") }
    var policeStation by remember { mutableStateOf(sharedPrefs.getString("police_station", "") ?: "") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Setup Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Add your details for emergency response",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )

            OutlinedTextField(
                value = emergencyNumber,
                onValueChange = { emergencyNumber = it },
                label = { Text("Emergency / Parent Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )

            OutlinedTextField(
                value = policeStation,
                onValueChange = { policeStation = it },
                label = { Text("Nearby Police Station") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank() || emergencyNumber.isBlank()) {
                        Toast.makeText(context, "Please fill Name and Emergency Number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    sharedPrefs.edit()
                        .putString("user_name", name)
                        .putString("parent_number", emergencyNumber)
                        .putString("police_station", policeStation)
                        .apply()
                    
                    navController.navigate("dashboard") {
                        popUpTo("details") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
            ) {
                Text("Save and Continue", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("ResQNowPrefs", Context.MODE_PRIVATE)

    val name = sharedPrefs.getString("user_name", "Not Set") ?: "Not Set"
    val parentNumber = sharedPrefs.getString("parent_number", "Not Set") ?: "Not Set"
    val policeStation = sharedPrefs.getString("police_station", "Not Set") ?: "Not Set"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileItem(label = "Name", value = name)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))
                    ProfileItem(label = "Emergency Number", value = parentNumber)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))
                    ProfileItem(label = "Nearby Police Station", value = policeStation)
                }
            }

            Button(
                onClick = { navController.navigate("details") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back to Dashboard")
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0F172A))
    }
}

fun showTimePicker(context: Context, currentTime: String, onTimeSelected: (String) -> Unit) {
    val parts = currentTime.split(":")
    val hour = if (parts.size == 2) parts[0].toIntOrNull() ?: 9 else 9
    val minute = if (parts.size == 2) parts[1].toIntOrNull() ?: 0 else 0

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