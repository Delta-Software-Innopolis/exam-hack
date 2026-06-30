package com.examhacker.phone_unlock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_USER_PRESENT
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.examhacker.phone_unlock.controller.UnlockOverlayController
import com.examhacker.phone_unlock.ui.UnlockOverlayWindow
import com.examhacker.resources.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class UnlockOverlayService : Service() {
    private lateinit var controller: UnlockOverlayController
    private lateinit var unlockOverlayWindow: UnlockOverlayWindow
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USER_PRESENT) {
                Log.d("Unlock", "ACTION_USER_PRESENT detecte")
                showOverlay()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        controller = UnlockOverlayController(
//            repository = ...,
        dismissOverlay = ::dismissOverlay,
        scope = serviceScope
        )

        unlockOverlayWindow = UnlockOverlayWindow(
            context = this,
            controller = controller,
            scope = serviceScope
        )

        registerReceiver(unlockReceiver, IntentFilter(ACTION_USER_PRESENT))
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        unregisterReceiver(unlockReceiver)
        serviceScope.cancel()
        unlockOverlayWindow.dismiss()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW // Low importance - no sound/visual disruption
        ).apply {
            description = CHANNEL_DESCRIPTION
            setShowBadge(false)
            enableVibration(false)
            enableLights(false)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Overlay Service")
            .setContentText("Running in background")
            .setSmallIcon(R.drawable.ic_book_open)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun showOverlay() {
        controller.reset()
        unlockOverlayWindow.show()
    }

    private fun dismissOverlay() {
        unlockOverlayWindow.dismiss()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "overlay_service_channel"
        private const val CHANNEL_NAME = "Overlay Service"
        private const val CHANNEL_DESCRIPTION = "Keeps overlay service running"
    }
}