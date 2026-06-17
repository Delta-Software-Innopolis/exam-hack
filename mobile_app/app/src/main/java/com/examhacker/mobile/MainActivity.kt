package com.examhacker.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val root = RootComponent(componentContext = defaultComponentContext())

        setContent {
//            ExamHackerMobileTheme {} TODO Add when theme is ready
            RootContent(root, Modifier.fillMaxSize())
        }
    }

    @Composable
    private fun RootContent(component: RootComponent, modifier: Modifier = Modifier,) {
        Children(stack = component.stack, modifier = modifier,) {
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
}