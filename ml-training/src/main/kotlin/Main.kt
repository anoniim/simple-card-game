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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mocks.NoOpSoundPlayer

fun main() {
    println("Let's do some ML training!")
    val game = createNewGameEngine("CP1")
    runBlocking {
        val gameStateFlow = game.getGameStateFlow()

        println("Setting collection")
        val stateCollectionJob = launch {
            gameStateFlow.collect { gameState ->
                println("Game state: $gameState")
                step(gameState, game)
            }
        }
        println("Starting game")
        game.startGame()

        while (game.gameEndState.value == null) {
            println("Game is not over yet")
            delay(10)
        }

        println("Game is over NOW")
        stateCollectionJob.cancel()
    }
    println("done")
}

private fun GameEngine.getGameStateFlow(): Flow<GameState> {
    val cardFlow = card
    val playersFlow = players
    val gameEndStateFlow = gameEndState

    return combine(cardFlow, playersFlow, gameEndStateFlow) { card, players, gameEndState ->
        GameState(players, card, gameEndState)
    }
}

private suspend fun step(gameState: GameState, game: GameEngine) {
    val player = gameState.players.find { it.isHuman } ?: throw IllegalStateException("Human player not found")
    if (player.isHuman && player.isCurrentPlayer) {
        println("Human player's turn")
        val bet = player.generateBet(gameState.card!!.points, gameState.players)
        println("Human player's bet: $bet")
        game.placeBetForHumanPlayer(bet)
    }
}

private fun createNewGameEngine(modelName: String): GameEngine {
    val settings = GameSettings.forDifficulty(GameDifficulty.EASY)
    val cardDeck = CardDeck(settings.numOfCardDecks)
    val ratingSystem = EloRatingSystem(Leaderboard(emptyMap()))
    val sounds = Sounds(NoOpSoundPlayer())
    val players = PlayerFactory(settings).createPlayers(modelName)
    return GameEngine(players, cardDeck, settings, ratingSystem, sounds,
        speedMode = SpeedMode.INSTANTANEOUS)
}

private data class GameState(
    val players: List<Player>,
    val card: Card?,
    val gameEndState: GameEndState?
)