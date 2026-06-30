package com.examhacker.mobile.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

interface IPermissionHandler {
    fun requestNotificationPermission()
    fun requestOverlayPermission()
    fun isNotificationGranted(): Boolean
    fun isOverlayGranted(): Boolean
}

class PermissionHandler(
    private val context: Context,
    private val notificationLauncher: ActivityResultLauncher<String>
) : IPermissionHandler {

    override fun requestNotificationPermission() {
        if (!isNotificationGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            return
        }
    }

    override fun requestOverlayPermission() {
        if (!isOverlayGranted()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            context.startActivity(intent)
        }
    }

    override fun isNotificationGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun isOverlayGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }
}