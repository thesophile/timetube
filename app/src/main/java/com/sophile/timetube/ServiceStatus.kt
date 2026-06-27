package com.sophile.timetube

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager

object ServiceStatus {

    fun isRunning(context: Context): Boolean {

        val manager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                    as AccessibilityManager

        val services = manager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )

        return services.any {
            it.resolveInfo.serviceInfo.packageName == context.packageName
        }
    }
}