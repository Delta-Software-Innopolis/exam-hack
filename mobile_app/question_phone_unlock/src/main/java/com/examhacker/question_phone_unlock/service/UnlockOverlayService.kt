package com.examhacker.question_phone_unlock.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_USER_PRESENT
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.examhacker.question_phone_unlock.component.UnlockOverlayComponent
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.examhacker.question_phone_unlock.ui.UnlockOverLayScreen

class UnlockOverlayService : Service() {
    private val windowManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        registerReceiver(unlockReceiver, IntentFilter(ACTION_USER_PRESENT))
        return START_STICKY
    }

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USER_PRESENT) {
                showOverlay()
            }
        }
    }

    fun showOverlay() {
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
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.OPAQUE
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
}