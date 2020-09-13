package com.bruno.aybar.composechallenges.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview

// Set of Material typography styles to start with
val typography = Typography(
    subtitle1 = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h1 = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 40.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)

@Composable
fun CancelButton(
    onClick: ()->Unit,
    modifier: Modifier = Modifier.width(150.dp),
) {
    Button(
        onClick = onClick,
        backgroundColor = transparent,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = "Cancel",
            style = typography.button.merge(TextStyle(
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Black,
            ))
        )
    }
}

@Preview
@Composable
fun TypographyPreview() {
    ComposeChallengesTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.padding(16.dp)) {
                Text("Subtitle 1", style = typography.subtitle1)
                Spacer(Modifier.height(16.dp))
                Text("Body 1", style = typography.body1)
                Spacer(Modifier.height(16.dp))
                Text("Body 2", style = typography.body2)
                Spacer(Modifier.height(16.dp))
                Text("H1", style = typography.h1)
                Spacer(Modifier.height(16.dp))
                Text("Caption", style = typography.caption)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {}) { Text("Button") }
                Spacer(Modifier.height(16.dp))
                CancelButton(onClick = {})
            }
        }
    }

}