package com.examhacker.question_phone_unlock.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.utility.AnswerVariant
import com.examhacker.question_phone_unlock.component.IUnlockOverlayComponent
import com.examhacker.resources.ColorPreset

@Composable
fun UnlockOverLayScreen(component: IUnlockOverlayComponent) {
    val model by component.model.subscribeAsState()


}

@Composable
fun UnlockOverlayUI(
    model: IUnlockOverlayComponent.Model,
    submitAnswer: (AnswerVariant) -> Unit,
    takeHint: () -> Unit,
    back: () -> Unit
) {
    Scaffold(
        containerColor = ColorPreset.White
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding).padding()
        ) {

        }
    }
}

