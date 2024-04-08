package ui

import GameEngine
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
        modifier = alignmentModifier.fillMaxHeight(),
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        PlayerView(players[3])
        BettingSection(players, onPlayerBet, onPlayerPass)
    }
}

private const val BEANS = "\uD83E\uDED8"

@Composable
private fun PlayerView(
    player: Player,
) {
    val placedBetViewWidth = 80.dp
    val playerStatsWidth = 280.dp
    val bettingSectionWidth = 120.dp
    val playerBoxWidthWithBet = playerStatsWidth + placedBetViewWidth
    val playerBoxWidthWithBettingSection = playerStatsWidth + bettingSectionWidth
    val playerBoxWidth = when {
        player.isHuman && player.bet != null -> playerBoxWidthWithBettingSection
        player.bet != null -> playerBoxWidthWithBet
        else -> playerStatsWidth
    }
    Row(Modifier.width(playerBoxWidth).height(200.dp)) {
        val placedBetHorizontalOffset = 20.dp
        if (player.id.value % 3 == 0) {
            PlayerStats(playerStatsWidth, player)
            PlacedBetSection(Modifier.align(Alignment.CenterVertically), placedBetViewWidth, player, -placedBetHorizontalOffset)
        } else {
            PlacedBetSection(Modifier.align(Alignment.CenterVertically), placedBetViewWidth, player, placedBetHorizontalOffset)
            PlayerStats(playerStatsWidth, player)
        }
    }
}

@Composable
private fun PlayerStats(playerStatsWidth: Dp, player: Player) {
    Box(Modifier.width(playerStatsWidth).height(200.dp)) {
        // Player stats background
        Image(
            painter = painterResource("img/board_trans.png"),
            contentDescription = null,
            modifier = Modifier.width(playerStatsWidth).height(200.dp),
        )
        Column(
            modifier = Modifier.width(playerStatsWidth).height(200.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PlayerText(text = player.name, 30.sp)
            PlayerText(text = "⭐ ${player.score}")
            PlayerText(text = "$BEANS ${player.coins}")
        }
        Text(
            text = if (player.isFirstInThisRound) "1️⃣" else "",
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopStart).padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun PlacedBetSection(alignmentModifier: Modifier, placedBetViewWidth: Dp, player: Player, horizontalOffset: Dp) {
    val showPlacedBet = player.bet != null
    if (showPlacedBet) {
        val bettingSectionBackground = if (player.isRoundWinner) Color.Green else MaterialTheme.colorScheme.tertiaryContainer
        Box(
            alignmentModifier.width(placedBetViewWidth).height(50.dp)
                .offset(x = horizontalOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(bettingSectionBackground)
        ) {
            when (player.bet) {
                is CoinBet -> PlayerText(text = "$BEANS ${player.bet.coins}", fontSize = 25.sp, Modifier.align(Alignment.Center))
                is Pass -> PlayerText(text = "PASS", fontSize = 18.sp, Modifier.align(Alignment.Center))
                else -> {}
            }
        }
    }
}

@Composable
private fun PlayerText(text: String, fontSize: TextUnit = 30.sp, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.tertiary,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
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