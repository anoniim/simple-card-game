package net.solvetheriddle.cardgame

import GameEndState
import GameEngine
import SpeedMode
import engine.Card
import engine.CardDeck
import engine.GameDifficulty
import engine.GameSettings
import engine.player.Player
import engine.player.PlayerFactory
import engine.rating.EloRatingSystem
import engine.rating.Leaderboard
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mocks.NoOpSoundPlayer

fun main() {
    println("Let's do some ML training!")
    val game = createNewGameEngine("CP1")
    runBlocking {
        val cardFlow = game.card
        val playersFlow = game.players
        val gameEndStateFlow = game.gameEndState
        val gameStateFlow = combine(cardFlow, playersFlow, gameEndStateFlow) { card, players, gameEndState ->
            GameState(players, card, gameEndState)
        }
        println("Setting collection")
        launch {
            gameStateFlow.collect { gameState ->
                println("Game state: $gameState")
            }
        }
        println("Starting game")
        game.startGame()

    }
    println("done")
}

private fun createNewGameEngine(modelName: String): GameEngine {
    val settings = GameSettings.forDifficulty(GameDifficulty.EASY)
    val cardDeck = CardDeck(settings.numOfCardDecks)
    val ratingSystem = EloRatingSystem(Leaderboard(emptyMap()))
    val sounds = Sounds(NoOpSoundPlayer())
    val players = PlayerFactory(settings).createPlayers(modelName)
    return GameEngine(players, cardDeck, settings, ratingSystem, sounds,
        speedMode = SpeedMode.FAST)
}

private data class GameState(
    val players: List<Player>,
    val card: Card?,
    val gameEndState: GameEndState?
)