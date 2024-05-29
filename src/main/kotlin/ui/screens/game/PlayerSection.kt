package ui.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.Card
import engine.player.CoinBet
import engine.player.Pass
import engine.player.Player
import ui.AppLocale
import ui.Strings

internal const val BEANS_SYMBOL = "\uD83E\uDED8"

@Composable
internal fun PlayerSection(
    playersState: State<List<Player>>,
    card: State<Card?>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    val players = playersState.value
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
            HumanPlayerView(players, card, onPlayerBet, onPlayerPass)
            PlayerView(players[2])
        }
    }
}

@Composable
fun HumanPlayerView(
    players: List<Player>,
    card: State<Card?>,
    onPlayerBet: (Int) -> Unit,
    onPlayerPass: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PlayerView(players[3])
        BettingSection(players, card, onPlayerBet, onPlayerPass)
    }
}

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
            PlayerBoard(playerStatsWidth, player)
            PlacedBetSection(Modifier.align(Alignment.CenterVertically), placedBetViewWidth, player, -placedBetHorizontalOffset)
        } else {
            PlacedBetSection(Modifier.align(Alignment.CenterVertically), placedBetViewWidth, player, placedBetHorizontalOffset)
            PlayerBoard(playerStatsWidth, player)
        }
    }
}

@Composable
private fun PlayerBoard(playerStatsWidth: Dp, player: Player) {
    Box(Modifier.width(playerStatsWidth).height(200.dp)) {
        // Player stats background
        Image(
            painter = painterResource("img/board_trans.png"),
            contentDescription = null,
            modifier = Modifier.width(playerStatsWidth).height(200.dp),
        )
        Column(
            modifier = Modifier.width(playerStatsWidth).height(200.dp)
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            PlayerName(player)
            Spacer(Modifier.height(8.dp))
            PlayerStats(player)
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
private fun PlayerName(player: Player) {
    Text(
        text = player.name.uppercase(),
        fontSize = 30.sp,
        color = MaterialTheme.colorScheme.primary,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            shadow = Shadow(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                blurRadius = 2f
            )
        ),
        fontWeight = FontWeight.Bold,
        maxLines = 1,
    )
}

@Composable
private fun PlayerStats(player: Player) {
    Column(
        Modifier.width(115.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Image(painterResource("img/plant.png"), contentDescription = null, modifier = Modifier.size(30.dp))
            PlayerText(text = "${player.score}")
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = BEANS_SYMBOL, fontSize = 25.sp)
            PlayerText(text = "${player.coins}")
        }

    }
}

@Composable
fun PlacedBetSection(alignmentModifier: Modifier, placedBetViewWidth: Dp, player: Player, horizontalOffset: Dp) {
    val showPlacedBet = player.bet != null
    if (showPlacedBet) {
        val bettingSectionBackground = if (player.isRoundWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer
        Box(
            alignmentModifier.width(placedBetViewWidth).height(50.dp)
                .offset(x = horizontalOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(bettingSectionBackground)
        ) {
            when (player.bet) {
                is CoinBet -> PlayerText(
                    text = "$BEANS_SYMBOL ${player.bet.coins}",
                    fontSize = 25.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                is Pass -> PlayerText(
                    text = Strings["pass", AppLocale.current].uppercase(),
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {}
            }
        }
    }
}

@Composable
private fun PlayerText(
    text: String,
    fontSize: TextUnit = 25.sp,
    color: Color = MaterialTheme.colorScheme.tertiary,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
        maxLines = 1,
    )
}