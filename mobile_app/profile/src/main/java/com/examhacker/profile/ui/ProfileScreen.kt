package com.examhacker.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.examhacker.resources.R
import com.examhacker.profile.component.IProfileComponent
import com.examhacker.resources.Dimensions
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.resources.ColorPreset

@Composable
fun ProfileScreen(component: IProfileComponent) {
    ProfileUI(
        username = stringResource(R.string.username_placeholder),
        avatar = painterResource(R.drawable.gitcat_refined),
        onLogoutClick = component::logout,
        onQuizHubClick = component::goToQuizHub,
        onQuizListClick = component::goToQuizList,
        onSettingsClick = component::goToSettings
    )
}

@Composable
private fun ProfileUI(
    username: String,
    avatar: Painter,
    onLogoutClick: () -> Unit,
    onQuizHubClick: () -> Unit,
    onQuizListClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ScreenTitle(
                text = stringResource(R.string.profile_screen_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Dimensions.ScreenPadding)
            )
        },
        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.PROFILE,
                onQuizHubClick = onQuizHubClick,
                onQuizListClick = onQuizListClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        containerColor = ColorPreset.BackgroundVariant,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding),
        ) {
            Spacer(Modifier.height(Dimensions.ProfileScreenTopSpacing))
            UserInfo(
                avatar = avatar,
                username = username
            )
            
            Spacer(Modifier.height(Dimensions.ScreenPadding))
            LogoutButton(onLogoutClick)
        }
    }
}

@Composable
private fun UserInfo(
    avatar: Painter,
    username: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.ProfileInfoSpacing),
        modifier = modifier
    ) {
        Image(
            painter = avatar,
            contentDescription = null,
            modifier = Modifier
                .size(Dimensions.ProfileImageSize)
                .clip(CircleShape)
        )

        Text(
            text = username,
            fontSize = Dimensions.UsernameFontSize,
            fontWeight = FontWeight.Bold,
            color = ColorPreset.Black
        )
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        contentPadding = PaddingValues(Dimensions.LogoutButtonPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.PositivePrimary,
            contentColor = ColorPreset.BackgroundVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_exit),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.logout_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileUI(
        username = stringResource(R.string.username_placeholder),
        avatar = painterResource(R.drawable.gitcat_refined),
        onLogoutClick = {},
        onQuizHubClick = {},
        onQuizListClick = {},
        onSettingsClick = {}
    )
}