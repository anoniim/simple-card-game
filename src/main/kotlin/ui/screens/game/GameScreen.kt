package ui.screens.game

import GameEngine
import GameEndState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.player.CoinBet
import engine.player.Pass
import engine.rating.Leaderboard
import kotlinx.coroutines.launch
import ui.AppLocale
import ui.Strings

@Composable
fun GameScreen(
    game: GameEngine,
    exitToMenu: (Leaderboard?) -> Unit,
    announceWinner: (GameEndState) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource("img/background.jpg"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val players = game.players.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        PlayerSection(players, game.card.collectAsState(),
            onPlayerBet = { coroutineScope.launch { game.placeBetForHumanPlayer(CoinBet(it)) } },
            onPlayerPass = { coroutineScope.launch { game.placeBetForHumanPlayer(Pass) } }
        )

        val firstCardDrawn = remember { mutableStateOf(false) }
        val showExitDialog = remember { mutableStateOf(false) }
        TopMenuSection(
            game,
            firstCardDrawn,
            showExitDialog,
            exitToMenu,
            Modifier.align(Alignment.TopCenter)
                .offset(y = 8.dp)
        )

        CardSection(
            game, firstCardDrawn, coroutineScope,
            Modifier.align(Alignment.Center)
                .offset(y = (-70).dp)
        )

        val gameEndState = game.gameEndState.collectAsState().value
        if (gameEndState != null) announceWinner(gameEndState)

        if (showExitDialog.value) {
            ExitDialog(
                Modifier.align(Alignment.Center),
                onExit = {
                    val updatedLeaderboard = game.penalizeExit()
                    exitToMenu(updatedLeaderboard)
                },
                onCancel = { showExitDialog.value = false }
            )
        }
    }
}

@Composable
fun TopMenuSection(
    game: GameEngine,
    firstCardDrawn: MutableState<Boolean>,
    showExitDialog: MutableState<Boolean>,
    exitToMenu: (Leaderboard?) -> Unit,
    positionModifier: Modifier
) {
    Row(
        modifier = positionModifier,
    ) {
        GoalSection(game.goalScore)
        Spacer(Modifier.width(8.dp))
        ExitIcon(firstCardDrawn, showExitDialog, exitToMenu)
    }
}

@Composable
private fun ExitIcon(firstCardDrawn: MutableState<Boolean>, showExitDialog: MutableState<Boolean>, exitToMenu: (Leaderboard?) -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                if (firstCardDrawn.value) {
                    showExitDialog.value = true
                } else exitToMenu(null)
            }
    ) {
        Text(
            text = "⨯",
            textAlign = TextAlign.Center,
            fontSize = 44.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun GoalSection(goalScore: Int) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
    ) {
        Text(
            text = "${Strings["goal", AppLocale.current]} $goalScore ",
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
