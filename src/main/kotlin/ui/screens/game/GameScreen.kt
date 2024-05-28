package ui.screens.game

import GameEngine
import WinningState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.player.CoinBet
import engine.player.Pass
import kotlinx.coroutines.launch

@Composable
fun GameScreen(game: GameEngine, startOver: () -> Unit, announceWinner: (WinningState) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource("img/background.jpg"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
//        Button(onClick = {
//            startOver()
//            firstCardDrawn.value = false
//        }) {
//            Text("Start over")
//        }
        val players = game.players.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        PlayerSection(players, game.card.collectAsState(),
            onPlayerBet = { coroutineScope.launch { game.placeBetForHumanPlayer(CoinBet(it)) } },
            onPlayerPass = { coroutineScope.launch { game.placeBetForHumanPlayer(Pass) } }
        )
        GoalSection(
            game.goalScore, Modifier.align(Alignment.TopCenter)
                .offset(y = 8.dp)
        )
        val alignmentModifier = Modifier.align(Alignment.Center)
            .offset(y = (-70).dp)
        CardSection(game, coroutineScope, alignmentModifier)

        val winningState = game.winningState.collectAsState().value
        if (winningState != null) announceWinner(winningState)
    }
}

@Composable
fun GoalSection(goalScore: Int, alignModifier: Modifier) {
    Row(
        modifier = alignModifier.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
    ) {
        Text(
            text = "Goal: $goalScore ",
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 20.sp
        )
        Image(
            painter = painterResource("img/plant.png"),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}
