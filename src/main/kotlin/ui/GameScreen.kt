package ui

import GameEngine
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.Card
import engine.CoinBet
import engine.Pass
import engine.player.Player
import getHighestBetInCoins
import kotlinx.coroutines.CoroutineScope
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
        PlayerOverview(players.value,
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

@Composable
private fun CardSection(game: GameEngine, coroutineScope: CoroutineScope, alignmentModifier: Modifier) {
    val cardState = game.card.collectAsState()
    val firstCardDrawn = remember { mutableStateOf(false) }
    val cardSizeModifier = Modifier.height(236.dp).width(194.dp)
    val card = cardState.value
    if (!firstCardDrawn.value) {
        DrawFirstCardButton(
            alignmentModifier = alignmentModifier,
            cardSizeModifier = cardSizeModifier,
        ) {
            firstCardDrawn.value = true
            coroutineScope.launch { game.startGame() }
        }
    } else if (card != null) {
        CardView(alignmentModifier.then(cardSizeModifier), card)
    } else {
        Spacer(modifier = cardSizeModifier)
    }
}

@Composable
private fun DrawFirstCardButton(alignmentModifier: Modifier, cardSizeModifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = alignmentModifier.fillMaxHeight()
            .shadow(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = cardSizeModifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Draw first card!")
        }
    }
}

@Composable
private fun PlayerOverview(
    players: List<Player>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    // Arrange the four players in the four corners of the screen
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerView(players[0])
            PlayerView(players[1])
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HumanPlayerView(players, onPlayerBet, onPlayerPass)
            PlayerView(players[2])
        }
    }
}

@Composable
fun HumanPlayerView(
    players: List<Player>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    Row(verticalAlignment = Alignment.Bottom) {
        PlayerView(players[3])
        BettingSection(players, onPlayerBet, onPlayerPass)
    }
}

@Composable
private fun PlayerView(
    player: Player,
) {
    Box(Modifier.width(280.dp).height(200.dp)) {
        Image(
            painter = painterResource("img/board_trans.png"),
            contentDescription = null,
            modifier = Modifier.width(280.dp).height(200.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (player.isFirstInThisRound) Text(text = "1️⃣") else Text(text = "")
            val isRoundWinner = player.isRoundWinner
            PlayerText(text = player.name, 22.sp, isRoundWinner)
            PlayerText(text = "Coins: ${player.coins}", isRoundWinner = isRoundWinner)
            PlayerText(text = "Score: ${player.score}", isRoundWinner = isRoundWinner)
            Spacer(Modifier.height(16.dp))
            when (player.bet) {
                is CoinBet -> PlayerText(text = "Current bet: ${player.bet.coins}", isRoundWinner = isRoundWinner)
                is Pass -> PlayerText(text = "Pass", isRoundWinner = isRoundWinner)
                else -> PlayerText(text = "-", isRoundWinner = isRoundWinner)
            }
        }
    }
}

@Composable
private fun PlayerText(text: String, fontSize: TextUnit = 18.sp, isRoundWinner: Boolean) {
    val textColor = if (isRoundWinner) Color.Green else MaterialTheme.colorScheme.errorContainer
    Text(
        text = text,
        fontSize = fontSize,
        color = textColor,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun BettingSection(
    players: List<Player>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    println(players)
    // Show betting section only if it's human player's turn
    val humanPlayer = players.find { it.isCurrentPlayer && it.isHuman }
    if (humanPlayer != null && humanPlayer.bet == null) {
        val highestBet = getHighestBetInCoins(players)
        val maxBet = humanPlayer.coins
        BetInputField(
            rememberBetInputStateHolder(minBet = highestBet + 1, maxBet),
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
fun CardView(modifier: Modifier, card: Card) {
    Card(
        modifier = modifier
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