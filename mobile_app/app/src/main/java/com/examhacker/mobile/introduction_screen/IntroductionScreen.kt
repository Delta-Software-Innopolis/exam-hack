package com.examhacker.mobile.introduction_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.R
import com.examhacker.resources.Dimensions

@Composable
fun IntroductionScreen(component: IIntroductionComponent) {
    val model by component.model.subscribeAsState()

    IntroductionUI(
        model = model,
        requestNotification = component::requestNotificationPermission,
        requestOverlay = component::requestOverlayPermission,
        proceedToAuth = component::proceedToAuthentication
    )
}

@Composable
private fun IntroductionUI(
    model: IIntroductionComponent.Model,
    requestNotification: () -> Unit,
    requestOverlay: () -> Unit,
    proceedToAuth: () -> Unit
) {
    Scaffold(
        topBar = {
            ScreenTitle(
                text = "Welcome to ExamHacker!",
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(Dimensions.ScreenPadding)
            )
        },
        containerColor = ColorPreset.BackgroundDefaultPrimary,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding)
        ) {
            Instruction(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            ButtonSection(
                isNotificationGranted = model.isNotificationGranted,
                isOverlayGranted = model.isOverlayGranted,
                requestNotification = requestNotification,
                requestOverlay = requestOverlay,
                proceedToAuth = proceedToAuth,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Instruction(modifier: Modifier = Modifier) {
    val descriptionText = remember {
        "ExamHacker is an educational platform for quizzes that implements innovational approach to passive learning. The main function of the app are questions emerging on the screen when you unlock the phone. This functionality needs some permissions from user side to work correctly."
    }
    val instructionText = remember {
        "Please, grant the required permissions and proceed further."
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Image(
                painter = painterResource(R.drawable.examhacker_logo),
                contentDescription = "",
                modifier = Modifier.size(128.dp)
            )
            Spacer(Modifier.height(Dimensions.ScreenPadding))
        }

        item {
            Text(
                text = descriptionText,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = ColorPreset.Black
                )
            )
            Spacer(Modifier.height(Dimensions.ScreenPadding))
        }

        item {
            Text(
                text = instructionText,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = ColorPreset.Black
                )
            )
        }
    }
}

@Composable
private fun ButtonSection(
    isNotificationGranted: Boolean,
    isOverlayGranted: Boolean,
    requestNotification: () -> Unit,
    requestOverlay: () -> Unit,
    proceedToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
        modifier = modifier
    ) {
        PermissionButtons(
            isNotificationGranted = isNotificationGranted,
            isOverlayGranted = isOverlayGranted,
            requestNotification = requestNotification,
            requestOverlay = requestOverlay,
            modifier = Modifier.fillMaxWidth()
        )

        IntroButton(
            label = "Proceed to authentication",
            onClick = proceedToAuth,
            enabled = isNotificationGranted && isOverlayGranted,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PermissionButtons(
    isNotificationGranted: Boolean,
    isOverlayGranted: Boolean,
    requestNotification: () -> Unit,
    requestOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        IntroButton(
            label = "Grant notification permission",
            onClick = requestNotification,
            modifier = Modifier.weight(1f),
            indicator = {
                PermissionIndicator(
                    isGranted = isNotificationGranted,
                    modifier = Modifier.size(12.dp)
                )
            }
        )

        IntroButton(
            label = "Grant overlay permission",
            onClick = requestOverlay,
            modifier = Modifier.weight(1f),
            indicator = {
                PermissionIndicator(
                    isGranted = isOverlayGranted,
                    modifier = Modifier.size(12.dp)
                )
            }
        )
    }
}

@Composable
private fun IntroButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indicator: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.InputFieldRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary,
            contentColor = ColorPreset.Black,

            disabledContainerColor = ColorPreset.BorderSecondary,
            disabledContentColor = ColorPreset.TextDefaultSecondary
        ),
        contentPadding = PaddingValues(16.dp),
        border = BorderStroke(
            width = Dimensions.DefaultBorderWidth,
            color =
                if (enabled)
                    ColorPreset.BorderSecondary
                else
                    ColorPreset.TextDefaultSecondary
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.weight(1f)
            )

            indicator?.let {
                indicator()
            }
        }
    }
}

@Composable
private fun PermissionIndicator(
    isGranted: Boolean,
    modifier: Modifier
) {
    Box(
        modifier = modifier.background(
            shape = CircleShape,
            color =
                if (isGranted)
                    ColorPreset.IconPositiveTertiary
                else
                    ColorPreset.IconNegative
        )
    )
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
fun IntroductionUIPreview() {
    IntroductionUI(
        model = IIntroductionComponent.Model().copy(isOverlayGranted = true),
        requestNotification = {},
        requestOverlay = {},
        proceedToAuth = {}
    )
}