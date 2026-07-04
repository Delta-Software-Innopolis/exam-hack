package com.examhacker.common.ui

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.examhacker.resources.ColorPreset
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun AppNavigationBar(
    selectedState: AppNavigationState,
    onQuizListClick: () -> Unit = {},
    onQuizHubClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .background(ColorPreset.BackgroundDefaultPrimary)
            .padding(
                vertical = Dimensions.AppNavigationPaddingVertical,
                horizontal = Dimensions.AppNavigationPaddingHorizontal
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        AppNavigationButton(
            text = stringResource(R.string.quiz_hub_navigation_label),
            iconSelected =  painterResource(R.drawable.ic_search_magnifying_glass_colored),
            iconDefault = painterResource(R.drawable.ic_search_magnifying_glass),
            selected = selectedState == AppNavigationState.QUIZ_HUB,
            onClick = onQuizHubClick,
            modifier = Modifier.weight(1f)
        )

        AppNavigationButton(
            text = stringResource(R.string.quiz_list_navigation_label),
            iconSelected = painterResource(R.drawable.ic_book_open_colored),
            iconDefault = painterResource(R.drawable.ic_book_open),
            selected = selectedState == AppNavigationState.QUIZ_LIST,
            onClick = onQuizListClick,
            modifier = Modifier.weight(1f)
        )

        AppNavigationButton(
            text = stringResource(R.string.profile_navigation_label),
            iconSelected = painterResource(R.drawable.ic_profile_colored),
            iconDefault = painterResource(R.drawable.ic_profile),
            selected = selectedState == AppNavigationState.PROFILE,
            onClick = onProfileClick,
            modifier = Modifier.weight(1f)
        )

        AppNavigationButton(
            text = stringResource(R.string.settings_navigation_label),
            iconSelected = painterResource(R.drawable.ic_settings_gear_colored),
            iconDefault = painterResource(R.drawable.ic_settings_gear),
            selected = selectedState == AppNavigationState.SETTINGS,
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AppNavigationButton(
    text: String,
    iconSelected: Painter,
    iconDefault: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.NavigationButtonPadding),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter =
                if (selected)
                    iconSelected
                else
                    iconDefault,
            contentDescription = "",
            modifier = Modifier.size(Dimensions.NavigationIconSize)
        )

        Text(
            text = text,
            fontSize = Dimensions.AppNavigationLabelFontSize,
            fontWeight = FontWeight.Normal,
            color =
                if (selected)
                    ColorPreset.PositivePrimary
                else
                    ColorPreset.Secondary
        )
    }
}

enum class AppNavigationState {
    QUIZ_HUB,
    QUIZ_LIST,
    PROFILE,
    SETTINGS
}

@Preview
@Composable
fun AppNavigationBarPreview(
    @PreviewParameter(AppNavigationStateProvider::class) selectedState: AppNavigationState
) {
    AppNavigationBar(
        selectedState = selectedState,
        onQuizListClick = {},
        onQuizHubClick = {},
        onProfileClick = {},
        onSettingsClick = {},
        modifier = Modifier.fillMaxWidth()
    )
}

class AppNavigationStateProvider: PreviewParameterProvider<AppNavigationState> {
    override val values =
        sequenceOf(
            AppNavigationState.QUIZ_HUB,
            AppNavigationState.QUIZ_LIST,
            AppNavigationState.PROFILE,
            AppNavigationState.SETTINGS
        )
}