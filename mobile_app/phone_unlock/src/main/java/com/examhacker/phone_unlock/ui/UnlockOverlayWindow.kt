package com.examhacker.phone_unlock.ui

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.examhacker.phone_unlock.controller.UnlockOverlayController
import kotlinx.coroutines.CoroutineScope

class UnlockOverlayWindow(
    context: Context,
    controller: UnlockOverlayController,
    scope: CoroutineScope
) {
    private val windowManager: WindowManager =
        context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val overlayView: UnlockOverlayView =
        UnlockOverlayView(context, controller, scope)

    private var attached = false
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
    }

    fun show() {
        if (!attached) {
            windowManager.addView(overlayView.getView(), layoutParams)
            attached = true
        }
    }

    fun dismiss() {
        if (attached) {
            windowManager.removeViewImmediate(overlayView.getView())
            attached = false
        }
    }
}