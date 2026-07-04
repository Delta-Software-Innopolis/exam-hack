package com.examhacker.authentication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.authentication.component.IAuthenticationComponent
import com.examhacker.authentication.component.ScreenMode
import com.examhacker.common.ui.LogoImage
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
    Scaffold(
        topBar = {
            ScreenTitle(
                text =
                    when(model.screenMode) {
                        ScreenMode.REGISTER -> stringResource(R.string.register_screen_title)
                        ScreenMode.LOGIN    -> stringResource(R.string.login_screen_title)
                    },
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                horizontalArrangement = Arrangement.Center
            )
        },
        containerColor = ColorPreset.BackgroundVariant
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            LogoImage(Modifier.size(242.dp).weight(1f))
            Spacer(Modifier.height(62.dp))

            InputFormWithButtons(
                model = model,
                onSwitchMode = onSwitchScreenMode,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onRepeatedPasswordChange = onRepeatedPasswordChange,
                onSignUp = onSignUp,
                onLogin = onLogin,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    BackHandler { back() }
}

@Composable
private fun InputFormWithButtons(
    model: IAuthenticationComponent.Model,
    onSwitchMode: (ScreenMode) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatedPasswordChange: (String) -> Unit,
    onSignUp: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding)
    ) {
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
            isPassword = true,
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
                isPassword = true,
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
        Button(
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.InputFieldRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorPreset.PositivePrimary,
                contentColor = ColorPreset.BackgroundDefaultPrimary
            ),
            contentPadding = PaddingValues(vertical = Dimensions.ScreenPadding),
        ) {
            Text(
                text = submitLabel,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = Dimensions.InputLabelFontSize
                )
            )
        }

        OutlinedButton(
            onClick = onSwitchModeClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.InputFieldRadius),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ColorPreset.BackgroundVariant,
                contentColor = ColorPreset.SecondaryDimm
            ),
            border = BorderStroke(
                width = Dimensions.DefaultBorderWidth,
                color = ColorPreset.SecondaryDimm
            ),
            contentPadding = PaddingValues(vertical = Dimensions.ScreenPadding),
        ) {
            Text(
                text = switchModeLabel,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = Dimensions.InputLabelFontSize
                )
            )
        }
    }
}

@Composable
private fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions()
) {
    val textStyle = remember {
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = Dimensions.InputLabelFontSize
        )
    }

    var isHidden by remember { mutableStateOf(true) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
            shape = RoundedCornerShape(Dimensions.InputFieldRadius),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ColorPreset.Black,
                unfocusedTextColor = ColorPreset.Black,

                focusedContainerColor = ColorPreset.Transparent,
                unfocusedContainerColor = ColorPreset.Transparent,

                focusedBorderColor =
                    if (error == null)
                        ColorPreset.BorderDefault
                    else
                        ColorPreset.ErrorPrimary,
                unfocusedBorderColor =
                    if (error == null)
                        ColorPreset.BorderDefault
                    else
                        ColorPreset.ErrorPrimary,
                errorBorderColor = ColorPreset.ErrorPrimary,

                errorLabelColor = ColorPreset.ErrorPrimary,
                focusedLabelColor = ColorPreset.TextDefaultSecondary,
                unfocusedLabelColor = ColorPreset.TextDefaultSecondary
            ),
            trailingIcon = {
                if (isPassword) {
                    IconButton(
                        onClick = { isHidden = !isHidden }
                    ) {
                        Icon(
                            painter =
                                if (isHidden)
                                    painterResource(R.drawable.ic_eye)
                                else
                                    painterResource(R.drawable.ic_eye_crossed),
                            contentDescription = null
                        )
                    }
                }
            },
            visualTransformation =
                if (isHidden && isPassword)
                    PasswordVisualTransformation(mask = '*')
                else
                    VisualTransformation.None
        )

        if (error != null) {
            Text(
                text = error,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorPreset.ErrorPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(device = Devices.DEFAULT)
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