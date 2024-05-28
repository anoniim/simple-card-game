package ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(openNewGameMenu: () -> Unit, openLeaderboard: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource("img/menu.png"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        MenuBox(
            openNewGameMenu, openLeaderboard,
            Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MenuBox(
    startGame: () -> Unit,
    openLeaderboard: () -> Unit,
    alignmentModifier: Modifier
) {
    Column(
        modifier = alignmentModifier
            .fillMaxWidth(fraction = 0.4f)
            .clip(RoundedCornerShape(16.dp))
            .shadow(16.dp)
            .background(Color.Gray.copy(alpha = 0.9f))
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { startGame() },
        ) {
            Text("NEW GAME")
        }
        OutlinedButton(
            onClick = { openLeaderboard() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("LEADERBOARD")
        }
    }
}