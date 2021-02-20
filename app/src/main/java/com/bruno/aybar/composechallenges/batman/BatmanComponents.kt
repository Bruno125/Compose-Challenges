package com.bruno.aybar.composechallenges.batman

import androidx.compose.foundation.Image
import androidx.compose.material.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.R


@Composable
fun BatmanLogo(modifier: Modifier) {
    Image(
        bitmap = imageResource(id = R.drawable.batman_logo),
        contentDescription = "Batman logo",
        modifier = modifier
    )
}

@Composable
fun BatmanWelcomeHint(modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("WELCOME TO ", style = MaterialTheme.typography.h2)
        Text("GOTHAM CITY", style = MaterialTheme.typography.h1)
        Text("YOU NEED ACCESS TO ENTER THE CITY", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun BatmanPageButtons(modifier: Modifier) {
    Column(modifier) {
        BatmanButton("LOGIN", BatmanButtonIconPosition.End)
        Spacer(Modifier.height(8.dp))
        BatmanButton("SIGNUP", BatmanButtonIconPosition.Start)
    }
}

enum class BatmanButtonIconPosition { Start, End }

@Composable
fun BatmanButton(label: String, iconPosition: BatmanButtonIconPosition) {
    Button(
        onClick = { },
        contentPadding = PaddingValues(all = 0.dp),
        modifier = Modifier.size(width = 300.dp, height = 48.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = label,
                modifier = Modifier.align(Alignment.Center)
            )
            Image(
                bitmap = imageResource(id = R.drawable.batman_logo),
                contentDescription = null, // not needed because it's just decoration for button
                colorFilter = ColorFilter.tint(Color.Gray.copy(alpha = 0.5f)),
                modifier = Modifier
                    .size(width = 60.dp, height = 30.dp)
                    .graphicsLayer(rotationZ = when(iconPosition) {
                        BatmanButtonIconPosition.Start -> 30f
                        BatmanButtonIconPosition.End -> -30f
                    })
                    .align(
                        BiasAlignment(
                        verticalBias = 1.1f,
                        horizontalBias = when(iconPosition) {
                            BatmanButtonIconPosition.Start -> -1.1f
                            BatmanButtonIconPosition.End -> 1.1f
                        }),
                    ),
            )
        }
    }
}

@Composable
fun BatmanContent(modifier: Modifier, maxWidth: Dp, batmanSizeProgress: Float) {
    ConstraintLayout(modifier.size(maxWidth)) {
        val (backgroundRef, batmanRef) = createRefs()
        Image(
            bitmap = imageResource(id = R.drawable.batman_background),
            contentDescription = null, // not needed because background is not informative
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(backgroundRef) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
        )
        Image(
            bitmap = imageResource(id = R.drawable.batman_alone),
            contentDescription = null, // not needed because image is not informative
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(width = maxWidth * batmanSizeProgress, height = maxWidth)
                .constrainAs(batmanRef) {
                    bottom.linkTo(backgroundRef.bottom)
                    centerHorizontallyTo(parent)
                },
        )
    }
}
