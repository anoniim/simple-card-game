package ui.screens.game

import GameEngine
import WinningState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.player.CoinBet
import engine.player.Pass
import kotlinx.coroutines.launch
import ui.AppLocale
import ui.Strings

@Composable
fun GameScreen(
    game: GameEngine,
    startOver: () -> Unit,
    exitToMenu: () -> Unit,
    announceWinner: (WinningState) -> Unit
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

        TopMenuSection(
            game,
            startOver,
            exitToMenu,
            Modifier.align(Alignment.TopCenter)
                .offset(y = 8.dp)
        )

        val alignmentModifier = Modifier.align(Alignment.Center)
            .offset(y = (-70).dp)
        CardSection(game, coroutineScope, alignmentModifier)

        val winningState = game.winningState.collectAsState().value
        if (winningState != null) announceWinner(winningState)
    }
}

@Composable
fun TopMenuSection(
    game: GameEngine,
    startOver: () -> Unit,
    exitToMenu: () -> Unit,
    positionModifier: Modifier
) {
    Row(
        modifier = positionModifier,
    ) {
        GoalSection(game.goalScore)
//        Spacer(Modifier.width(8.dp))
//        RestartIcon(startOver)
        Spacer(Modifier.width(8.dp))
        ExitIcon(exitToMenu)
    }
}

@Composable
private fun ExitIcon(exitToMenu: () -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { exitToMenu() }
    ) {
        Text(
            text = "⨯",
            textAlign = TextAlign.Center,
            fontSize = 44.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

//@Composable
//private fun RestartIcon(startOver: () -> Unit) {
//    Box(
//        modifier = Modifier.clip(RoundedCornerShape(8.dp))
//            .background(MaterialTheme.colorScheme.secondaryContainer)
//            .clickable { startOver() }
//            .padding(10.dp)
//    ) {
//        Image(
//            painter = painterResource("img/restart.png"),
//            contentDescription = null,
//            modifier = Modifier.size(20.dp)
//        )
//    }
//}

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
