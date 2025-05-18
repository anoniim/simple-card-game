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
import ui.AppLocale
import ui.Strings

@Composable
fun LeaderboardScreen(displayRows: List<DisplayRow>, playerName: String, closeLeaderboard: () -> Unit) {
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
    playerName: String,
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
            HeaderText("leaderboard_name", Modifier.Companion.weight(2f).padding(horizontal = 16.dp))
            HeaderText("leaderboard_rating", Modifier.weight(1f))
            HeaderText("leaderboard_num_of_games", Modifier.weight(1f))
            HeaderText("leaderboard_win_ratio", Modifier.weight(1f))
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(displayRows) { index, rowData ->
                Row(
                    modifier = Modifier.background(if (index % 2 == 0) Color.LightGray else Color.Transparent)
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = rowData.name, modifier = Modifier.weight(2f)
                            .padding(horizontal = 16.dp),
                        fontWeight = if (rowData.name == playerName) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(text = rowData.rating.toString(), modifier = Modifier.weight(1f))
                    Text(text = rowData.games.toString(), modifier = Modifier.weight(1f))
                    Text(text = "${rowData.winRatio}%", modifier = Modifier.weight(1f))
                }
            }
        }

        OutlinedButton(
            onClick = { closeLeaderboard() },
            modifier = Modifier.fillMaxWidth(fraction = 0.5f)
                .padding(top = 8.dp),
        ) {
            Text(
                text = Strings["close", AppLocale.current].uppercase()
            )
        }
    }
}

@Composable
private fun HeaderText(stringKey: String, positionModifier: Modifier) {
    Text(
        text = Strings[stringKey, AppLocale.current],
        modifier = positionModifier.padding(vertical = 4.dp),
        fontWeight = FontWeight.Bold
    )
}