package ui

import Game
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import engine.*
import engine.player.Player
import engine.player.PlayerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class Screen() {
    data object Menu : Screen()
    data object Game : Screen()
}

@Composable
@Preview
fun App() {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }

    when (currentScreen) {
        is Screen.Menu -> MenuScreen { currentScreen = Screen.Game }
        is Screen.Game -> GameScreen { currentScreen = Screen.Game }
    }
}

@Composable
fun MenuScreen(onNavigate: () -> Unit) {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(
                onClick = onNavigate,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Start game!")
            }
        }
    }
}

@Composable
private fun GameScreen(onNavigate: () -> Unit) {
    MaterialTheme {
        val game = remember { createGame() }
        GameContent(game)
    }
}

private fun createGame(): Game {
    val settings = GameSettings.DEFAULT
    val playerFactory = PlayerFactory(settings)
    val playerName = "Maca"
    val players = playerFactory.createAiPlayer(settings.aiPlayerCount) +
            playerFactory.createHumanPlayer(settings.aiPlayerCount, playerName)
    return Game(players)
}

@Composable
fun GameContent(game: Game) {
    val state = game.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp))
        PlayerOverview(state)
        Spacer(Modifier.height(32.dp))
        CardSection(game, state, coroutineScope)
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
private fun CardSection(game: Game, state: State<ActiveGameState>, coroutineScope: CoroutineScope) {
    var firstCardDrawn by remember { mutableStateOf(false) }
    if (!firstCardDrawn) {
        DrawFirstCardButton {
            firstCardDrawn = true
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

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
