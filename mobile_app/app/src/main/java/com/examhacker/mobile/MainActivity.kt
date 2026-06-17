package com.examhacker.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.examhacker.authentication.ui.AuthenticationScreen
import com.examhacker.mobile.root.IRootComponent
import com.examhacker.mobile.root.RootComponent
import com.examhacker.resources.ExamHackerMobileTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Always create the root component outside Compose on the main thread
        val root =
            RootComponent(
                componentContext = defaultComponentContext(),
            )

        setContent {
            ExamHackerMobileTheme {
                Text("RootComponent initialized")
            }
        }
    }

    @Composable
    private fun RootContent(component: RootComponent, modifier: Modifier = Modifier,) {
        Children(stack = component.stack, modifier = modifier,) {
            when (val child = it.instance) {

                is IRootComponent.Child.Authentication ->
                    AuthenticationScreen(component = child.component)

                is IRootComponent.Child.AIInteractions -> {}
                is IRootComponent.Child.QuizList -> {}
                is IRootComponent.Child.QuizEdit -> {}
                is IRootComponent.Child.QuizSolve -> {}
                is IRootComponent.Child.Settings -> {}
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExamHackerMobileTheme {
        Greeting("Android")
    }
}