package ui

import GameEngine
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
import engine.Bet
import engine.Card
import engine.CoinBet
import engine.Pass
import engine.player.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GameScreen(game: GameEngine, startOver: () -> Unit, announceWinner: (Player) -> Unit) {
    MaterialTheme {
//        Button(onClick = {
//            startOver()
//            firstCardDrawn.value = false
//        }) {
//            Text("Start over")
//        }
        GameContent(game)

        val winner = game.winner.collectAsState()
        if (winner.value != null) {
            announceWinner(game.winner.value!!)
        }
    }
}

@Composable
fun GameContent(game: GameEngine) {
    val players = game.players.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        PlayerOverview(players.value)
        Spacer(Modifier.height(32.dp))
        CardSection(game, coroutineScope)
        Spacer(Modifier.height(32.dp))

        BettingSection(players,
            onPlayerBet = { coroutineScope.launch { game.placeBetForHumanPlayer(CoinBet(it)) } },
            onPlayerPass = { coroutineScope.launch { game.placeBetForHumanPlayer(Pass) } }
        )
    }
}

@Composable
private fun CardSection(game: GameEngine, coroutineScope: CoroutineScope) {
    val cardState = game.card.collectAsState()
    val firstCardDrawn = remember { mutableStateOf(false) }
    val sizeModifier = Modifier.height(236.dp)
        .width(194.dp)
    val card = cardState.value
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
    } else if (card != null) {
        CardView(sizeModifier, card)
    }
}

@Composable
private fun BettingSection(
    players: State<List<Player>>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    println(players.value)
    // Show betting section only if it's human player's turn
    val humanPlayer = players.value.find { it.isCurrentPlayer && it.isHuman }
    if (humanPlayer != null && humanPlayer.bet == null) {
        BetInputField(
            onBetConfirmed = {
                onPlayerBet(it)
            },
            playerPassed = {
                onPlayerPass()
            },
        )
    }
}

@Composable
private fun DrawFirstCardButton(modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Draw first card!")
    }
}

@Composable
private fun PlayerOverview(players: List<Player>) {
    Row(Modifier.fillMaxWidth()) {
        players.forEach { player ->
            Player(
                Modifier.weight(1f),
                player.name,
                player.coins,
                player.score,
                player.bet,
                player.isFirstInThisRound
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
        if (isFirst) Text(text = "*") else Text(text = "")
        Text(text = playerName)
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
fun CardView(sizeModifier: Modifier, card: Card) {
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