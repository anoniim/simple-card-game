package ui.screens.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import engine.rating.DisplayRow

@Composable
fun LeaderboardScreen(displayRows: List<DisplayRow>, playerName: MutableState<String>, closeLeaderboard: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource("img/menu.png"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        StartGameBox(
            displayRows, playerName,
            closeLeaderboard,
            Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun StartGameBox(
    displayRows: List<DisplayRow>,
    playerName: MutableState<String>,
    closeLeaderboard: () -> Unit,
    alignmentModifier: Modifier
) {
    Column(
        modifier = alignmentModifier
            .fillMaxWidth(fraction = 0.7f)
            .clip(RoundedCornerShape(16.dp))
            .shadow(16.dp)
            .background(Color.Gray.copy(alpha = 0.9f))
            .padding(vertical = 24.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Row {
            HeaderText("Name", Modifier.Companion.weight(2f).padding(horizontal = 16.dp))
            HeaderText("# of games", Modifier.weight(1f))
            HeaderText("Win ratio", Modifier.weight(1f))
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(displayRows) { index, rowData ->
                Row(
                    modifier = Modifier.background(if (index % 2 == 0) Color.LightGray else Color.Transparent)
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = rowData.name, modifier = Modifier.weight(2f).padding(horizontal = 16.dp))
                    Text(text = rowData.games.toString(), modifier = Modifier.weight(1f))
                    Text(text = "${rowData.winRatio * 100}%", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeaderText(title: String, positionModifier: Modifier) {
    Text(
        text = title,
        modifier = positionModifier.padding(vertical = 4.dp),
        fontWeight = FontWeight.Bold
    )
}