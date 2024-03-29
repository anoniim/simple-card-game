package ui

import GameEngine
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import appModule
import engine.GamePrefs
import engine.player.Player
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get
import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

sealed class NavigationState {
    data object MenuScreen : NavigationState()
    data class GameScreen(val game: GameEngine) : NavigationState()
    data class WinnerScreen(val winner: Player) : NavigationState()
}

@Composable
@Preview
fun App() {
    val prefs = get<GamePrefs>(GamePrefs::class.java)
    var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.MenuScreen) }
    val playerName = remember { mutableStateOf(prefs.getPlayerName()) }

    when (val currentNavigationState = navigationState) {
        is NavigationState.MenuScreen -> MenuScreen(playerName,
            startGame = {
                // Save player name
                prefs.setPlayerName(playerName.value)
                // Start a new game
                navigationState = NavigationState.GameScreen(newGame(playerName))
            })

        is NavigationState.GameScreen -> GameScreen(currentNavigationState.game,
            startOver = {
                navigationState = NavigationState.GameScreen(newGame(playerName))
            }, announceWinner = {
                println("WINNER: ${it.name}")
                navigationState = NavigationState.WinnerScreen(it)
            })

        is NavigationState.WinnerScreen -> WinnerScreen(currentNavigationState.winner.name,
            playAgain = {
                navigationState = NavigationState.GameScreen(newGame(playerName))
            })
    }
}

private fun newGame(playerName: MutableState<String>) =
    get<GameEngine>(GameEngine::class.java) { parametersOf(playerName.value) }

fun main() = application {
    startKoin {
        modules(appModule)
    }
    Window(
        title = "Simple card game",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
