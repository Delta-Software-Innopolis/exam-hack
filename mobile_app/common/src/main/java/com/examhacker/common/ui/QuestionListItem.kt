package com.examhacker.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.examhacker.resources.R
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun QuestionListItem(
    number: Int,
    questionDescription: String,
    onQuestionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onQuestionClick() },
        shape = RoundedCornerShape(Dimensions.QuestionCardRadius),
        colors = CardDefaults.cardColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary,
            disabledContainerColor = ColorPreset.BackgroundDefaultPrimary,

            contentColor = ColorPreset.SecondaryDimm,
            disabledContentColor = ColorPreset.SecondaryDimm
        )
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.QuestionCardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${number}. $questionDescription",
                fontSize = Dimensions.QuestionCardFontSize,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.ic_edit_pencil),
                contentDescription = ""
            )
        }
    }
}