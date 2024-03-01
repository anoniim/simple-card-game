package ui

import Game
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.ActiveGameState
import engine.Bet
import engine.CoinBet
import engine.Pass
import engine.player.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GameScreen(game: Game, startOver: () -> Unit) {
    MaterialTheme {
        val firstCardDrawn = remember { mutableStateOf(false) }
//        Button(onClick = {
//            startOver()
//            firstCardDrawn.value = false
//        }) {
//            Text("Start over")
//        }
        GameContent(game, firstCardDrawn)
    }
}

@Composable
fun GameContent(game: Game, firstCardDrawn: MutableState<Boolean>) {
    val state = game.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp))
        PlayerOverview(state)
        Spacer(Modifier.height(32.dp))
        CardSection(game, state, firstCardDrawn, coroutineScope)
        Spacer(Modifier.height(32.dp))

        println(state.value)
        if (state.value.isHumanPlayerTurn()) {
            BettingSection(
                onPlayerBet = { coroutineScope.launch { game.placeBetForHumanPlayer(CoinBet(it)) } },
                onPlayerPass = { coroutineScope.launch { game.placeBetForHumanPlayer(Pass) } }
            )
        }
    }
}

@Composable
private fun CardSection(game: Game, state: State<ActiveGameState>, firstCardDrawn: MutableState<Boolean>, coroutineScope: CoroutineScope) {
    if (!firstCardDrawn.value) {
        DrawFirstCardButton {
            firstCardDrawn.value = true
            coroutineScope.launch { game.startGame() }
        }
    } else {
        CardView(state)
    }
}

@Composable
private fun BettingSection(
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    BetInputField(
        onBetConfirmed = onPlayerBet,
        playerPassed = onPlayerPass,
    )
}

@Composable
private fun DrawFirstCardButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Draw first card!")
    }
}

@Composable
private fun PlayerOverview(state: State<ActiveGameState>) {
    Row(Modifier.fillMaxWidth()) {
        val firstPlayerIndex = state.value.currentRound.firstPlayerIndex
        val bets = state.value.currentRound.bets
        state.value.players.forEachIndexed { index: Int, player: Player ->
            Player(Modifier.weight(1f), player, bets[player.id], index == firstPlayerIndex)
        }
    }
}

@Composable
private fun Player(modifier: Modifier, player: Player, bet: Bet?, isFirst: Boolean) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${player.name}${if (isFirst) "*" else ""}")
        Text(text = "Coins: ${player.coins}")
        Text(text = "Score: ${player.score}")
        Spacer(Modifier.height(16.dp))
        when (bet) {
            is CoinBet -> Text(text = "Current bet: ${bet.coins}")
            is Pass -> Text(text = "Pass")
            else -> Text(text = "-")
        }
    }
}

@Composable
fun CardView(state: State<ActiveGameState>) {
    val card = state.value.currentRound.card
    Card(
        Modifier.height(236.dp)
            .width(194.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = card.displayValue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                fontSize = 160.sp
            )
            Text(
                text = "Points: ${card.points}",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
                fontSize = 20.sp
            )
        }
    }
}