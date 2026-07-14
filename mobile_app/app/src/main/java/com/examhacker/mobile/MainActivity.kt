package com.examhacker.mobile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.examhacker.authentication.ui.AuthenticationScreen
import com.examhacker.common.utility.AndroidFilePicker
import com.examhacker.mobile.introduction_screen.IntroductionScreen
import com.examhacker.mobile.root.IRootComponent
import com.examhacker.mobile.root.RootComponent
import com.examhacker.quiz_solve.ui.QuizSolveScreen
import com.examhacker.settings.ui.SettingsScreen
import com.examhacker.mobile.util.PermissionHandler
import com.examhacker.phone_unlock.service.UnlockOverlayService
import com.examhacker.profile.ui.ProfileScreen
import com.examhacker.quiz_create.ui.QuizCreateScreen
import com.examhacker.quiz_edit.ui.QuizEditScreen
import com.examhacker.quiz_hub.ui.QuizHubScreen
import com.examhacker.quiz_info.ui.QuizInfoScreen
import com.examhacker.quiz_list.ui.QuizListScreen

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissionHandler = PermissionHandler(
            context = this,
            notificationLauncher = notificationPermissionLauncher
        )
        val filePicker = AndroidFilePicker(
            registry = activityResultRegistry,
            lifecycleOwner = this,
            contentResolver = contentResolver,
        )
        val root = RootComponent(
            componentContext = defaultComponentContext(),
            permissionHandler = permissionHandler,
            filePicker = filePicker,
            startOverlayService = ::startOverlayService
        )

        setContent {
//            ExamHackerMobileTheme {} TODO Add when theme is ready
            RootContent(root, Modifier.fillMaxSize())
        }
    }

    override fun onResume() {
        super.onResume()

        if (Settings.canDrawOverlays(this)) {
            startOverlayService()
        }
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
            is IRootComponent.Child.Introduction   -> IntroductionScreen(child.component)
            is IRootComponent.Child.Authentication -> AuthenticationScreen(child.component)
            is IRootComponent.Child.QuizList       -> QuizListScreen(child.component)
            is IRootComponent.Child.QuizEdit       -> QuizEditScreen(child.component)
            is IRootComponent.Child.QuizCreate     -> QuizCreateScreen(child.component)
            is IRootComponent.Child.QuizInfo       -> QuizInfoScreen(child.component)
            is IRootComponent.Child.QuizHub        -> QuizHubScreen(child.component)
            is IRootComponent.Child.QuizSolve      -> QuizSolveScreen(child.component)
            is IRootComponent.Child.Profile        -> ProfileScreen(child.component)
            is IRootComponent.Child.Settings       -> SettingsScreen(child.component)
        }
    }
}