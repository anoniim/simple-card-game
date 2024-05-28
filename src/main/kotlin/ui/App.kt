package ui

import GameEngine
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import engine.GamePrefs
import engine.player.Player
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get
import ui.screens.game.GameScreen
import ui.screens.leaderboard.LeaderboardScreen
import ui.screens.menu.MenuScreen
import ui.screens.winner.WinnerScreen
import ui.theme.AppTheme

sealed class NavigationState {
    data object MenuScreen : NavigationState()
    data object LeaderboardScreen : NavigationState()
    data class GameScreen(val game: GameEngine) : NavigationState()
    data class WinnerScreen(val winner: Player) : NavigationState()
}

@Composable
@Preview
fun App() {

    AppTheme {
        val prefs = remember { get<GamePrefs>(GamePrefs::class.java) }
        var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.MenuScreen) }
        val playerName = remember { mutableStateOf(prefs.getPlayerName()) }

        when (val currentNavigationState = navigationState) {
            is NavigationState.MenuScreen -> MenuScreen(playerName,
                startGame = {
                    // Save player name
                    prefs.setPlayerName(playerName.value)
                    // Start a new game
                    navigationState = NavigationState.GameScreen(newGame(playerName))
                },
                openLeaderboard = {
                    navigationState = NavigationState.LeaderboardScreen
                })

            is NavigationState.GameScreen -> GameScreen(currentNavigationState.game,
                startOver = {
                    navigationState = NavigationState.GameScreen(newGame(playerName))
                }, announceWinner = {
                    println("WINNER: ${it.winner.name}")
                    prefs.updateLeaderboard(it.leaderboard)
                    navigationState = NavigationState.WinnerScreen(it.winner)
                })

            is NavigationState.WinnerScreen -> WinnerScreen(currentNavigationState.winner.name,
                playAgain = {
                    navigationState = NavigationState.GameScreen(newGame(playerName))
                },
                openMenu = {
                    navigationState = NavigationState.MenuScreen
                })

            is NavigationState.LeaderboardScreen -> LeaderboardScreen(prefs.getLeaderboard().getDisplayRows(),
                playerName.value,
                closeLeaderboard = {
                    navigationState = NavigationState.MenuScreen
                })
        }
    }
}

private fun newGame(playerName: MutableState<String>) =
    get<GameEngine>(GameEngine::class.java) {
        parametersOf(playerName.value)
    }

