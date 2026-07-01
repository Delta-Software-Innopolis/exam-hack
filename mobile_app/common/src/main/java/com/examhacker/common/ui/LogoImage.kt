package com.examhacker.common.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.examhacker.resources.R

@Composable
fun LogoImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.examhacker_logo),
        contentDescription = null,
        modifier = modifier
    )
}