package ui

import GameEngine
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import engine.GamePrefs
import engine.player.Player
import org.koin.java.KoinJavaComponent.get
import ui.screens.game.GameScreen
import ui.screens.leaderboard.LeaderboardScreen
import ui.screens.menu.MenuScreen
import ui.screens.menu.NewGameMenuScreen
import ui.screens.winner.WinnerScreen
import ui.theme.AppTheme

sealed class NavigationState {
    data object MenuScreen : NavigationState()
    data object NewGameMenuScreen : NavigationState()
    data object LeaderboardScreen : NavigationState()
    data object GameScreen : NavigationState()
    data class WinnerScreen(val winner: Player) : NavigationState()
}

@Composable
@Preview
fun App() {

    AppTheme {
        var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.MenuScreen) }
        val prefs = remember { get<GamePrefs>(GamePrefs::class.java) }
        val playerName = remember { mutableStateOf(prefs.loadPlayerName()) }

        when (val currentNavigationState = navigationState) {
            is NavigationState.MenuScreen -> MenuScreen(
                openNewGameMenu = {
                    navigationState = NavigationState.NewGameMenuScreen
                },
                openLeaderboard = {
                    navigationState = NavigationState.LeaderboardScreen
                })

            is NavigationState.NewGameMenuScreen -> NewGameMenuScreen(playerName,
                startGame = {
                    prefs.savePlayerName(playerName.value)
                    navigationState = NavigationState.GameScreen
                },
                backToMainMenu = {
                    navigationState = NavigationState.MenuScreen
                })

            is NavigationState.GameScreen -> GameScreen(newGameEngine(),
                startOver = {
                    navigationState = NavigationState.GameScreen
                },
                announceWinner = {
                    println("WINNER: ${it.winner.name}")
                    prefs.saveLeaderboard(it.leaderboard)
                    navigationState = NavigationState.WinnerScreen(it.winner)
                })

            is NavigationState.WinnerScreen -> WinnerScreen(currentNavigationState.winner.name,
                playAgain = {
                    navigationState = NavigationState.GameScreen
                },
                openMenu = {
                    navigationState = NavigationState.MenuScreen
                })

            is NavigationState.LeaderboardScreen -> LeaderboardScreen(prefs.loadLeaderboard().getDisplayRows(),
                playerName.value,
                closeLeaderboard = {
                    navigationState = NavigationState.MenuScreen
                })
        }
    }
}

private fun newGameEngine() = get<GameEngine>(GameEngine::class.java)

