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
fun GameScreen(game: Game, startOver: () -> Unit, announceWinner: (Player) -> Unit) {
    MaterialTheme {
        val firstCardDrawn = remember { mutableStateOf(false) }
//        Button(onClick = {
//            startOver()
//            firstCardDrawn.value = false
//        }) {
//            Text("Start over")
//        }
        GameContent(game, firstCardDrawn)

        val winner = game.winner.collectAsState()
        if (winner.value != null) {
            announceWinner(game.winner.value!!)
        }
    }
}

@Composable
fun GameContent(game: Game, firstCardDrawn: MutableState<Boolean>) {
    val state = game.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp))
        PlayerOverview(game, state)
        Spacer(Modifier.height(32.dp))
        CardSection(game, state, firstCardDrawn, coroutineScope)
        Spacer(Modifier.height(32.dp))

        if (state.value.isCurrentPlayerHuman()) {
            BettingSection(
                onPlayerBet = { coroutineScope.launch { game.placeBetForHumanPlayer(CoinBet(it)) } },
                onPlayerPass = { coroutineScope.launch { game.placeBetForHumanPlayer(Pass) } }
            )
        }
    }
}

@Composable
private fun CardSection(game: Game, state: State<ActiveGameState>, firstCardDrawn: MutableState<Boolean>, coroutineScope: CoroutineScope) {
    val sizeModifier = Modifier.height(236.dp)
        .width(194.dp)
    if (!firstCardDrawn.value) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DrawFirstCardButton(
                modifier = sizeModifier,
            ) {
                firstCardDrawn.value = true
                coroutineScope.launch { game.startGame() }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
    } else {
        CardView(sizeModifier, state)
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
private fun DrawFirstCardButton(modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Draw first card!")
    }
}

@Composable
private fun PlayerOverview(game: Game, state: State<ActiveGameState>) {
    Row(Modifier.fillMaxWidth()) {
        val players = game.players
        players.forEachIndexed { index, player ->
            Player(
                Modifier.weight(1f),
                player.name,
                player.coins,
                player.score,
                player.bet,
                state.value.isPlayerFirst(index)
            )
        }
    }
}

@Composable
private fun Player(
    modifier: Modifier,
    playerName: String,
    coins: Int,
    score: Int,
    bet: Bet?,
    isFirst: Boolean
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$playerName${if (isFirst) "*" else ""}")
        Text(text = "Coins: $coins")
        Text(text = "Score: $score")
        Spacer(Modifier.height(16.dp))
        when (bet) {
            is CoinBet -> Text(text = "Current bet: ${bet.coins}")
            is Pass -> Text(text = "Pass")
            else -> Text(text = "-")
        }
    }
}

@Composable
fun CardView(sizeModifier: Modifier, state: State<ActiveGameState>) {
    val card = state.value.card
    Card(
        modifier = sizeModifier
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