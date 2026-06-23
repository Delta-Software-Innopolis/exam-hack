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
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.examhacker.phone_unlock.component.UnlockOverlayComponent
import com.examhacker.phone_unlock.ui.UnlockOverLayScreen
import com.examhacker.resources.R

class UnlockOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

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
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        registerReceiver(unlockReceiver, IntentFilter(ACTION_USER_PRESENT))
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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
        if (composeView != null && composeView?.parent != null) {
            Log.d("OverlayService", "Overlay already showing")
            return
        }

        val overlayComponent = UnlockOverlayComponent(
            componentContext = DefaultComponentContext(
                lifecycle = LifecycleRegistry()
            ),
            dismissOverlay = ::dismissOverlay
        )

        if (composeView == null) {
            composeView = ComposeView(this).apply {
                setContent {
                    UnlockOverLayScreen(overlayComponent)
                }
                // Set lifecycle owner for proper Compose behavior
//                ViewTreeLifecycleOwner.set(this, LifecycleOwnerImpl())
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        windowManager.addView(composeView, params)
    }

    private fun dismissOverlay() {
        composeView?.let { windowManager.removeView(it) }
        composeView = null
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "overlay_service_channel"
        private const val CHANNEL_NAME = "Overlay Service"
        private const val CHANNEL_DESCRIPTION = "Keeps overlay service running"
    }
}