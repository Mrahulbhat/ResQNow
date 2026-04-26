package com.resqnow.app

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.*

class EmergencyDetectionService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private val CHANNEL_ID = "EmergencyDetectionChannel"
    private val NOTIFICATION_ID = 1
    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false

    private val checkTimeRunnable = object : Runnable {
        override fun run() {
            checkTimeAndToggleListening()
            handler.postDelayed(this, 60000) // Check every minute
        }
    }

    private val shutdownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SHUTDOWN) {
                Log.d("ResQNow", "Device shutting down - triggering last location alert")
                triggerEmergencyResponse(isShutdown = true)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Service started"))
        initializeSpeechRecognizer()
        handler.post(checkTimeRunnable)
        
        val filter = IntentFilter(Intent.ACTION_SHUTDOWN)
        registerReceiver(shutdownReceiver, filter)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Emergency Detection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ResQNow Monitoring")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun checkTimeAndToggleListening() {
        val sharedPrefs = getSharedPreferences("ResQNowPrefs", Context.MODE_PRIVATE)
        val startTimeStr = sharedPrefs.getString("start_time", "09:00") ?: "09:00"
        val endTimeStr = sharedPrefs.getString("end_time", "20:00") ?: "20:00"

        if (isCurrentTimeWithinRange(startTimeStr, endTimeStr)) {
            if (!isListening) {
                Log.d("ResQNow", "Starting listening (within time range)")
                isListening = true
                updateNotification("Listening for emergency keywords...")
                startListening()
            }
        } else {
            if (isListening) {
                Log.d("ResQNow", "Stopping listening (outside time range)")
                isListening = false
                updateNotification("Monitoring paused (outside active hours)")
                speechRecognizer?.stopListening()
            }
        }
    }

    private fun isCurrentTimeWithinRange(start: String, end: String): Boolean {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val startParts = start.split(":")
        val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()

        val endParts = end.split(":")
        val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()

        return if (startMinutes <= endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            // Overlaps midnight
            currentMinutes >= startMinutes || currentMinutes <= endMinutes
        }
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("ResQNow", "Ready for speech")
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    Log.e("ResQNow", "Speech recognition error: $error")
                    if (isListening) {
                        when (error) {
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> speechRecognizer?.cancel()
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> Log.d("ResQNow", "Timeout, restarting...")
                            SpeechRecognizer.ERROR_NO_MATCH -> Log.d("ResQNow", "No match, restarting...")
                        }
                        // Retry after a short delay to avoid rapid looping
                        handler.postDelayed({ if (isListening) startListening() }, 1000)
                    }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.let {
                        for (match in it) {
                            Log.d("ResQNow", "Heard: $match")
                            if (isEmergencyKeyword(match)) {
                                triggerEmergencyResponse()
                                break
                            }
                        }
                    }
                    if (isListening) startListening()
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            // Some devices need this to keep listening
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("ResQNow", "Error starting listening", e)
        }
    }

    private fun isEmergencyKeyword(text: String): Boolean {
        val keywords = listOf("help", "bachao", "kaapadi", "emergency", "save me")
        return keywords.any { text.contains(it, ignoreCase = true) }
    }

    @SuppressLint("MissingPermission")
    private fun triggerEmergencyResponse(isShutdown: Boolean = false) {
        Log.d("ResQNow", "Emergency Response Triggered (Shutdown=$isShutdown)")
        
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                val locationUrl = if (location != null) {
                    "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                } else {
                    "Location not available"
                }
                val prefix = if (isShutdown) "[Shutdown Alert] " else ""
                sendEmergencySMS(prefix + locationUrl)
            }
            .addOnFailureListener {
                val prefix = if (isShutdown) "[Shutdown Alert] " else ""
                sendEmergencySMS(prefix + "Location could not be fetched")
            }
    }

    private fun sendEmergencySMS(locationInfo: String) {
        val sharedPrefs = getSharedPreferences("ResQNowPrefs", Context.MODE_PRIVATE)
        val isTestMode = sharedPrefs.getBoolean("test_mode", false)
        val contacts = sharedPrefs.getStringSet("emergency_contacts", emptySet()) ?: emptySet()
        
        val message = "EMERGENCY! I need help. My current location: $locationInfo"
        
        if (isTestMode) {
            Log.d("ResQNow", "[TEST MODE] Would send SMS: $message")
            handler.post {
                Toast.makeText(this, "[TEST MODE] SOS Triggered!", Toast.LENGTH_LONG).show()
            }
            return
        }

        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        for (contact in contacts) {
            try {
                smsManager.sendTextMessage(contact, null, message, null, null)
                Log.d("ResQNow", "SMS sent to $contact")
            } catch (e: Exception) {
                Log.e("ResQNow", "Failed to send SMS to $contact", e)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkTimeAndToggleListening()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isListening = false
        handler.removeCallbacks(checkTimeRunnable)
        speechRecognizer?.destroy()
        try {
            unregisterReceiver(shutdownReceiver)
        } catch (e: Exception) {
            // Ignore if not registered
        }
    }
}
