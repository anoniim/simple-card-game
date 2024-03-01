package ui

import Game
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import appModule
import engine.GamePrefs
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get

sealed class NavigationState {
    data object MenuScreen : NavigationState()
    data class GameScreen(val game: Game) : NavigationState()
}

@Composable
@Preview
fun App() {
    val prefs = get<GamePrefs>(GamePrefs::class.java)
    var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.MenuScreen) }
    val playerName = remember { mutableStateOf(prefs.getPlayerName()) }

    when (val currentNavigationState = navigationState) {
        is NavigationState.MenuScreen -> MenuScreen(playerName, startGame = {
            // Save player name
            prefs.setPlayerName(playerName.value)
            // Start a new game
            navigationState = NavigationState.GameScreen(newGame(playerName))
        })

        is NavigationState.GameScreen -> GameScreen(currentNavigationState.game, startOver = {
            // Restart game
            navigationState = NavigationState.GameScreen(newGame(playerName))
        })
    }
}

private fun newGame(playerName: MutableState<String>) =
    get<Game>(Game::class.java) { parametersOf(playerName.value) }

fun main() = application {
    startKoin {
        modules(appModule)
    }
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
