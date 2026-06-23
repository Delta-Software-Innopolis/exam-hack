package com.examhacker.quiz_edit.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examhacker.resources.ColorPreset

@Composable
fun QuizGenerateScreen() {

    Scaffold(
        containerColor = ColorPreset.Background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .statusBarsPadding()
        ) {

            QuizProgressHeader(
                currentStep = 2
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Use AI to generate questions",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Upload files (presentations, books, notes)",
                color = ColorPreset.Black,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "AI will generate questions",
                color = ColorPreset.Black,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Edit them as you want",
                color = ColorPreset.Black,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Uploaded files",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    ColorPreset.LightGray
                )
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Nothing here yet",
                        color = ColorPreset.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    ColorPreset.LightGray
                )
            ) {

                Text(
                    text = "Add files",
                    color = ColorPreset.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        ColorPreset.LightGray
                    )
                ) {

                    Text(
                        text = "Skip",
                        color = ColorPreset.Black
                    )
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        ColorPreset.Turquoise
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ColorPreset.Mint,
                        contentColor = ColorPreset.Black
                    )
                ) {

                    Text(
                        text = "Generate Questions",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(
    widthDp = 393,
    heightDp = 836,
    showBackground = true
)
@Composable
private fun QuizGenerateScreenPreview() {

    QuizGenerateScreen()
}