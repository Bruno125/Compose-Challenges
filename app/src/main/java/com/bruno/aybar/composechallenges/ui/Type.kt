package com.bruno.aybar.composechallenges.ui

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.utils.animationSequence

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

val buttonHeight = 60.dp

@Composable
fun CancelButton(
    onClick: ()->Unit,
    widthMultiplier: Float = 1f,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .preferredWidth(150.dp * widthMultiplier)
            .preferredHeight(buttonHeight),
    ) {
        Text(text = "Cancel")
    }
}

@Composable
fun BackupButton(onClick: ()->Unit, modifier: Modifier = Modifier) {
    val backupButtonSizeMultiplier = animatedFloat(initVal = 1f)
    Button(
        onClick = {
            backupButtonSizeMultiplier.animationSequence(
                targetValues = listOf(0.95f,1f),
                anim = TweenSpec(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
            )
            onClick()
        },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier
            .preferredWidth(240.dp * backupButtonSizeMultiplier.value)
            .preferredHeight(buttonHeight * backupButtonSizeMultiplier.value),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Create Backup")
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
                BackupButton(onClick = {})
            }
        }
    }

}