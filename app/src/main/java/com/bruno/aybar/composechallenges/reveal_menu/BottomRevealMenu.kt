package com.bruno.aybar.composechallenges.reveal_menu

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Spring.DampingRatioHighBouncy
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.R

private val DATA = (1..100).map { "Item $it" }

@Composable
fun BottomRevealMenu() {
    var isMenuOpened by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { CustomTopBar() },
        floatingActionButton = {
            FloatingAddButton(
                isOpened = isMenuOpened,
                onClick = { isMenuOpened = !isMenuOpened },
            )
        }
    ) { scaffoldPaddings ->
        Box(Modifier.background(DarkPurple).padding(scaffoldPaddings)) {
            if(isMenuOpened) {
                SideMenuOptions(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-100).dp)
                )
                SearchBar(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 100.dp, bottom = 16.dp)
                )
            }
            val contentOffset = animateDpAsState(if(isMenuOpened) (-100).dp else 0.dp)
            val corner = animateDpAsState(if(isMenuOpened) 24.dp else 0.dp)
            MainContent(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(contentOffset.value, contentOffset.value)
                    .clip(RoundedCornerShape(corner.value))
            )

        }
    }
}

@Composable
private fun CustomTopBar() {
    TopAppBar(
        title = { Text("Bottom Reveal Example App") },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back button")
            }
        },
        backgroundColor = MaterialTheme.colors.primaryVariant
    )
}

@Composable
private fun FloatingAddButton(isOpened: Boolean, onClick: ()->Unit) {
    FloatingActionButton(onClick = onClick) {
        val rotation = animateFloatAsState(
            targetValue = if(isOpened) -45f else 0f,
            animationSpec = spring(DampingRatioHighBouncy)
        )
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add item button",
            modifier = Modifier.rotate(rotation.value)
        )
    }
}

@Composable
private fun MainContent(modifier: Modifier = Modifier) {
    LazyColumn(modifier.background(MaterialTheme.colors.background)) {
        items(DATA) { item ->
            SimpleItemCard(item)
        }
    }
}

@Composable
private fun SimpleItemCard(item: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_cloud),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Gray, CircleShape)
                    .padding(4.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(item)
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(54.dp)
            .background(Color.Gray, RoundedCornerShape(percent = 50)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
    }
}

@Composable
private fun SideMenuOptions(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MenuOption(R.drawable.ic_video_library, contentDescription = "Go to Video Library")
        MenuOption(R.drawable.ic_image, contentDescription = "Go to Pictures")
        MenuOption(R.drawable.ic_camera, contentDescription = "Open Camera")
    }
}

@Composable
private fun MenuOption(@DrawableRes icon: Int, contentDescription: String) {
    IconButton(
        modifier = Modifier
            .size(56.dp)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp)),
        onClick = { }
    ) {
        Icon(painterResource(icon), contentDescription, tint = Color.White)
    }
}

private val LightPurple = Color(0xFF6200EE)
private val Purple = Color(0xFF3700B3)
private val DarkPurple = Color(0xFF25073B)
private val Pink = Color(0xFFE32677)

private val DarkColorPalette = darkColors(
    primary = LightPurple,
    primaryVariant = Purple,
    secondary = Pink,
    onSecondary = Color.White,
    onPrimary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = LightPurple,
    primaryVariant = Purple,
    secondary = Pink,
    background = Color.LightGray,
    surface = Color.White,
    onSecondary = Color.White,
)

@Composable
fun BottomRevealChallengeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    MaterialTheme(
        colors = colors,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun BottomRevealMenuPreview() {
    BottomRevealChallengeTheme {
        BottomRevealMenu()
    }
}
