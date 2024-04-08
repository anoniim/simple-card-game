package ui.screens.game

import GameEngine
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import engine.CoinBet
import engine.Pass
import engine.player.Player
import kotlinx.coroutines.launch

@Composable
fun GameScreen(game: GameEngine, startOver: () -> Unit, announceWinner: (Player) -> Unit) {
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
        PlayerSection(players.value,
            onPlayerBet = { coroutineScope.launch { game.placeBetForHumanPlayer(CoinBet(it)) } },
            onPlayerPass = { coroutineScope.launch { game.placeBetForHumanPlayer(Pass) } }
        )
        CardSection(game, coroutineScope, Modifier.align(Alignment.Center))

        val winner = game.winner.collectAsState()
        if (winner.value != null) {
            announceWinner(game.winner.value!!)
        }
    }
}