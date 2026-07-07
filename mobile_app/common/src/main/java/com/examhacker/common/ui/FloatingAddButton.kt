package com.examhacker.common.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun FloatingAddButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(bottom = 8.dp),
        shape = CircleShape,
        containerColor = ColorPreset.PositivePrimary
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add_plus),
            contentDescription = null,
            tint = ColorPreset.BackgroundDefaultPrimary,
            modifier = Modifier.padding(Dimensions.FloatingActionButtonPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingAddButtonPreview() {
    FloatingAddButton(
        onClick = {}
    )
}