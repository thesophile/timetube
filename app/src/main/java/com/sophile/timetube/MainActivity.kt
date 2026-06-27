package com.sophile.timetube

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {

    companion object {
        var serviceRunning by mutableStateOf(false)
    }

    private val requestNotificationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceRunning = ServiceStatus.isRunning(this)

        setContent {
            TimeTubeScreen()
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestNotificationPermission.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        serviceRunning = ServiceStatus.isRunning(this)
    }
}

@Composable
fun TimeTubeScreen() {

    val context = LocalContext.current
    val running = MainActivity.serviceRunning

    var timerText by remember {
        mutableStateOf(Settings.formatTime(Settings.getTimerSeconds(context)))
    }

    var warningText by remember {
        mutableStateOf(Settings.formatTime(Settings.getWarningSeconds(context)))
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "TimeTube",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = timerText,
            onValueChange = { timerText = it },
            label = { Text("Timer (m:ss or seconds)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )

        OutlinedTextField(
            value = warningText,
            onValueChange = { warningText = it },
            label = { Text("Warning before end") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )

        Button(onClick = {

            val timer = Settings.parseTime(timerText)
            val warning = Settings.parseTime(warningText)

            when {
                timer == null || timer < 1 -> {
                    error = "Invalid timer."
                }

                warning == null || warning >= timer -> {
                    error = "Warning must be less than timer."
                }

                else -> {
                    Settings.setTimerSeconds(context, timer)
                    Settings.setWarningSeconds(context, warning)
                    error = null
                }
            }

        }) {
            Text("Save")
        }

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(onClick = {
            context.startActivity(Intent(ACTION_ACCESSIBILITY_SETTINGS))
        }) {
            Text("Accessibility Settings")
        }


        Text(
            text = if (running)
                "🟢 Accessibility Service: Running"
            else
                "🔴 Accessibility Service: Not running"
        )
    }
}