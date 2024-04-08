package ui.screens.winner

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WinnerScreen(winnerName: String, playAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "The winner is",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = winnerName,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 80.sp,
        )
        Spacer(Modifier.height(72.dp))
        Button(
            onClick = { playAgain() },
        ) {
            Text("Play again!")
        }
    }

}
