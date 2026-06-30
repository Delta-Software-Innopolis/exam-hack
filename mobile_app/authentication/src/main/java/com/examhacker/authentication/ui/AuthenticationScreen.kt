package com.examhacker.authentication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.authentication.component.IAuthenticationComponent
import com.examhacker.authentication.component.ScreenMode
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun AuthenticationScreen(component: IAuthenticationComponent) {
    val model by component.model.subscribeAsState()

    AuthenticationUI(
        model = model,
        onSwitchScreenMode = component::switchScreenMode,
        onEmailChange = component::onEmailChange,
        onPasswordChange = component::onPasswordChange,
        onRepeatedPasswordChange = component::onRepeatedPasswordChange,
        onLogin = component::onLogin,
        onSignUp = component::onSignUp,
        back = component::back
    )
}

@Composable
private fun AuthenticationUI(
    model: IAuthenticationComponent.Model,
    onSwitchScreenMode: (ScreenMode) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatedPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onSignUp: () -> Unit,
    back: () -> Unit
) {
    BackHandler { back() }

    Scaffold(
        topBar = {
            ScreenTitle(
                text =
                    when(model.screenMode) {
                        ScreenMode.REGISTER -> stringResource(R.string.register_screen_title)
                        ScreenMode.LOGIN    -> stringResource(R.string.login_screen_title)
                        ScreenMode.DEMO_END -> stringResource(R.string.demo_end_title)
                    },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.Center
            )
        },
        containerColor = ColorPreset.BackgroundVariant
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding),
            contentAlignment = Alignment.Center
        ) {
            when (model.screenMode) {
                ScreenMode.REGISTER, ScreenMode.LOGIN ->
                    RegisterAndLoginUI(
                        model = model,
                        onSwitchMode = onSwitchScreenMode,
                        onEmailChange = onEmailChange,
                        onPasswordChange = onPasswordChange,
                        onRepeatedPasswordChange = onRepeatedPasswordChange,
                        onSignUp = onSignUp,
                        onLogin = onLogin
                    )

                ScreenMode.DEMO_END -> DemoEndUI()
            }
        }
    }
}

@Composable
private fun RegisterAndLoginUI(
    model: IAuthenticationComponent.Model,
    onSwitchMode: (ScreenMode) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatedPasswordChange: (String) -> Unit,
    onSignUp: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.AuthScreenSpacing)
    ) {
        PlaceholderImage(
            painter = painterResource(R.drawable.pretty_img_here_soon),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        InputFields(
            mode = model.screenMode,
            email = model.email,
            password = model.password,
            repeatedPassword = model.repeatedPassword,
            errors = model.errors,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onRepeatedPasswordChange = onRepeatedPasswordChange,
            modifier = Modifier.fillMaxWidth()
        )

        if (model.screenMode == ScreenMode.REGISTER) {
            SubmitAndSwitchModeButtons(
                onSubmitClick = onSignUp,
                submitLabel = stringResource(R.string.sign_up),
                onSwitchModeClick = { onSwitchMode(ScreenMode.LOGIN) },
                switchModeLabel = stringResource(R.string.switch_to_login_label),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            SubmitAndSwitchModeButtons(
                onSubmitClick = onLogin,
                submitLabel = stringResource(R.string.login),
                onSwitchModeClick = { onSwitchMode(ScreenMode.REGISTER) },
                switchModeLabel = stringResource(R.string.switch_to_register_label),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DemoEndUI() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlaceholderImage(
            painter = painterResource(R.drawable.amazing_things_soon),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        AuthButton(
            type = AuthButtonType.DEMO_END,
            label = stringResource(R.string.demo_end_button_label),
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PlaceholderImage(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(top = Dimensions.PlaceHolderImageTopPadding),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painter,
            contentDescription = null
        )
    }
}

@Composable
private fun InputFields(
    mode: ScreenMode,
    email: String,
    password: String,
    repeatedPassword: String,
    errors: IAuthenticationComponent.Errors,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatedPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.InputFieldsSpacing)
    ) {
        InputTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.email_input_label),
            error = errors.email,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        )

        InputTextField(
            value = password,
            onValueChange = onPasswordChange,
            label =
                if (mode == ScreenMode.REGISTER)
                    stringResource(R.string.password_register_label)
                else
                    stringResource(R.string.password_login_label),
            error = errors.password,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction =
                    if (mode == ScreenMode.REGISTER)
                        ImeAction.Next
                    else
                        ImeAction.Done
            )
        )

        if (mode == ScreenMode.REGISTER) {
            InputTextField(
                value = repeatedPassword,
                onValueChange = onRepeatedPasswordChange,
                label = stringResource(R.string.repeat_password_label),
                error = errors.repeatedPassword,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
    }
}

@Composable
fun SubmitAndSwitchModeButtons(
    onSubmitClick: () -> Unit,
    submitLabel: String,
    onSwitchModeClick: () -> Unit,
    switchModeLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.InputFieldsSpacing)
    ) {
        AuthButton(
            type = AuthButtonType.SUBMIT,
            label = submitLabel,
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        )

        AuthButton(
            type = AuthButtonType.SWITCH_MODE,
            label = switchModeLabel,
            onClick = onSwitchModeClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions()
) {
    val textStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = Dimensions.AuthLabelFontSize
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = textStyle
            )
        },
        textStyle = textStyle,
        singleLine = true,
        isError = error != null,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.InputFieldRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ColorPreset.Black,
            unfocusedTextColor = ColorPreset.Black,

            focusedContainerColor = ColorPreset.Transparent,
            unfocusedContainerColor = ColorPreset.Transparent,

            focusedBorderColor =
                if (error == null) ColorPreset.BorderDefault else ColorPreset.ErrorPrimary,

            unfocusedBorderColor =
                if (error == null) ColorPreset.BorderDefault else ColorPreset.ErrorPrimary,

            errorBorderColor = ColorPreset.ErrorPrimary,
            errorLabelColor = ColorPreset.ErrorPrimary,

            focusedLabelColor = ColorPreset.TextDefaultSecondary,
            unfocusedLabelColor = ColorPreset.TextDefaultSecondary
        )
    )

    if (error != null) {
        Text(
            text = error,
            color = ColorPreset.ErrorPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
private fun AuthButton(
    type: AuthButtonType,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when(type) {
        AuthButtonType.SUBMIT      -> ColorPreset.PositivePrimary
        AuthButtonType.SWITCH_MODE -> ColorPreset.BorderDefault
        AuthButtonType.DEMO_END    -> ColorPreset.BorderWarningTertiary
    }
    val labelColor = when(type) {
        AuthButtonType.SUBMIT, AuthButtonType.SWITCH_MODE -> ColorPreset.TextPositivePrimary
        AuthButtonType.DEMO_END                           -> ColorPreset.TextWarningTertiary
    }
    val backgroundColor = when(type) {
        AuthButtonType.SUBMIT      -> ColorPreset.BackgroundPositiveSecondary
        AuthButtonType.SWITCH_MODE -> ColorPreset.BackgroundDefaultSecondary
        AuthButtonType.DEMO_END    -> ColorPreset.BackgroundWarningSecondary
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.InputFieldRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = labelColor
        ),
        border = BorderStroke(
            width = Dimensions.DefaultBorderWidth,
            color = borderColor
        ),
        contentPadding = PaddingValues(vertical = Dimensions.AuthButtonPadding),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = Dimensions.AuthLabelFontSize
            ),
            modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally)
        )
    }
}

enum class AuthButtonType {
    SUBMIT,
    SWITCH_MODE,
    DEMO_END
}

@Preview(device = Devices.PIXEL)
@Composable
private fun AuthenticationUIPreview() {
    AuthenticationUI(
        model = IAuthenticationComponent.Model().copy(
            errors = IAuthenticationComponent.Errors(email = "ghfgh")
        ),
        onSwitchScreenMode = {},
        onEmailChange = {},
        onPasswordChange = {},
        onRepeatedPasswordChange = {},
        onLogin = {},
        onSignUp = {},
        back = {}
    )
}