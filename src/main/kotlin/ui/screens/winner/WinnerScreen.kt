package ui.screens.winner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.AppLocale
import ui.Strings

@Composable
fun WinnerScreen(winnerName: String, playAgain: () -> Unit, openMenu: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painterResource("img/winner_background.png"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = Strings["winner_label", AppLocale.current],
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = winnerName.uppercase(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = { playAgain() },
            ) {
                Text(Strings["play_again", AppLocale.current].uppercase())
            }

            OutlinedButton(
                onClick = { openMenu() },
                Modifier.padding(4.dp)
            ) {
                Text(Strings["menu", AppLocale.current].uppercase())
            }
        }
    }
}
