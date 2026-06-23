package com.examhacker.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.examhacker.ai_interactions.ui.AIGenerationScreen
import com.examhacker.authentication.ui.AuthenticationScreen
import com.examhacker.mobile.root.IRootComponent
import com.examhacker.mobile.root.RootComponent
import com.examhacker.quiz_edit.ui.QuizEditScreen
import com.examhacker.quiz_list.ui.QuizListScreen
import com.examhacker.quiz_solve.ui.QuizSolveScreen
import com.examhacker.resources.ExamHackerMobileTheme
import com.examhacker.settings.ui.SettingsScreen
import androidx.core.net.toUri
import com.examhacker.phone_unlock.service.UnlockOverlayService

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startOverlayServiceIfPermitted()
        } else {
            startOverlayServiceIfPermitted()
        }
    }

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            startOverlayServiceIfPermitted()
        } else {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val root = RootComponent(componentContext = defaultComponentContext())

        setContent {
//            ExamHackerMobileTheme {} TODO Add when theme is ready
            RootContent(root, Modifier.fillMaxSize())
        }

        requestAllPermissions()
        startOverlayServiceIfPermitted()
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            startOverlayServiceIfPermitted()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isOverlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun requestAllPermissions() {
        if (!isNotificationPermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            return
        }

        if (!isOverlayPermissionGranted()) {
            requestOverlayPermission()
            return
        }

        startOverlayServiceIfPermitted()
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    private fun startOverlayServiceIfPermitted() {
        if (!isOverlayPermissionGranted()) {
            return
        }

        startOverlayService()
    }

    private fun startOverlayService() {
        val intent = Intent(this, UnlockOverlayService::class.java)
        startForegroundService(intent)
    }

    private fun stopOverlayService() {
        val intent = Intent(this, UnlockOverlayService::class.java)
        stopService(intent)
    }
}


@Composable
private fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(stack = component.stack, modifier = modifier) {
        when (val child = it.instance) {
            is IRootComponent.Child.Authentication -> AuthenticationScreen(child.component)
            is IRootComponent.Child.AIInteractions -> AIGenerationScreen(child.component)
            is IRootComponent.Child.QuizList       -> QuizListScreen(child.component)
            is IRootComponent.Child.QuizEdit       -> QuizEditScreen(child.component)
            is IRootComponent.Child.QuizSolve      -> QuizSolveScreen(child.component)
            is IRootComponent.Child.Settings       -> SettingsScreen(child.component)
        }
    }
}