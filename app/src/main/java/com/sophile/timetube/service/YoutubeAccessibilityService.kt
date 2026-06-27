package com.sophile.timetube.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.*

import com.sophile.timetube.Settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sophile.timetube.MainActivity

class YoutubeAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null
    private val CHANNEL_ID = "timetube_warning"
    private val NOTIFICATION_ID = 1

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "TimeTube",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun cancelWarningNotification() {
        NotificationManagerCompat.from(this)
            .cancel(NOTIFICATION_ID)
    }

    private fun showWarningNotification() {

        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("TimeTube")
            .setContentText("YouTube will close soon.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(NOTIFICATION_ID, builder.build())
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            return

        val pkg = event.packageName?.toString() ?: return

        if (pkg == "com.google.android.youtube") {

            if (timerJob == null) {
                Log.d("TimeTube", "Starting timer")

                timerJob = scope.launch {
                    val timerSeconds = Settings.getTimerSeconds(applicationContext)
                    val warningSeconds = Settings.getWarningSeconds(applicationContext)

                    delay((timerSeconds - warningSeconds) * 1000L)
                    showWarningNotification()

                    delay(warningSeconds * 1000L)
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    cancelWarningNotification()

                    Log.d("TimeTube", "Timer finished")
                    timerJob = null
                }
            }

        } else {

            timerJob?.cancel()
            timerJob = null
            cancelWarningNotification()

        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}