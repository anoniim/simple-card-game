package ui

import Game
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import engine.*

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
    val game = remember { Game("Maca") }
    val state = game.state.collectAsState()
    println(state)
    GameContent(game, state)
}

@Composable
fun GameContent(game: Game, state: State<ActiveGameState>) {
    MaterialTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))
            StartGameButton(game, state)
            Spacer(Modifier.height(32.dp))
            PlayerOverview(state)
            Spacer(Modifier.height(32.dp))
            CardView(state)
            Spacer(Modifier.height(32.dp))

            BettingSection(
                onPlayerBet = { game.placeBetForHumanPlayer(CoinBet(it)) },
                onPlayerPass = { game.placeBetForHumanPlayer(Pass) }
            )
        }
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
private fun StartGameButton(game: Game, state: State<ActiveGameState>) {
    var text by remember { mutableStateOf("Start game!") }
    var enabled by remember { mutableStateOf(true) }
    Button(enabled = enabled, onClick = {
        text = "Game started"
        game.startGame()
        println(state)
        enabled = false
    }) {
        Text(text)
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
        Modifier.height(256.dp)
            .width(194.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = card.displayValue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                fontSize = 180.sp
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
