package com.sophile.timetube

import android.content.Context

object Settings {

    private const val PREFS = "timetube"

    private const val KEY_TIMER = "timer_seconds"
    private const val KEY_WARNING = "warning_seconds"

    fun getTimerSeconds(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_TIMER, 300)

    fun setTimerSeconds(context: Context, seconds: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_TIMER, seconds)
            .apply()
    }

    fun getWarningSeconds(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_WARNING, 30)

    fun setWarningSeconds(context: Context, seconds: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_WARNING, seconds)
            .apply()
    }

    fun parseTime(text: String): Int? {
        val parts = text.trim().split(":")

        return when (parts.size) {
            1 -> parts[0].toIntOrNull()

            2 -> {
                val min = parts[0].toIntOrNull() ?: return null
                val sec = parts[1].toIntOrNull() ?: return null

                if (sec !in 0..59) return null

                min * 60 + sec
            }

            else -> null
        }
    }

    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}