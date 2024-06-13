package net.solvetheriddle.cardgame

import GameEndState
import GameEngine
import SpeedMode
import engine.Card
import engine.CardDeck
import engine.GameDifficulty
import engine.GameSettings
import engine.player.*
import engine.rating.EloRatingSystem
import engine.rating.Leaderboard
import getHighestBetInCoins
import kotlinx.coroutines.runBlocking
import mocks.NoOpSoundPlayer

private const val NO_ACTION = -1
private const val ACTION_PASS = 0

class TrainingEnvironment {

    @Suppress("MemberVisibilityCanBePrivate")  // Called by Py4J
    val modelName = "CP1"
    @Suppress("unused") // Called by Py4J
    val gameStateArraySize = 13

    private lateinit var game: GameEngine // Initialized in reset()
    private lateinit var validActions: List<Bet> // Initialized in reset()


    @Suppress("unused") // Called by Py4J
    fun reset(): List<Int> {
        game = createNewGameEngine(modelName)
        return runBlocking {
            game.startGame()
            game.getGameState()
        }.toStateArray()
    }

    @Suppress("unused") // Called by Py4J
    fun getAllActions(): List<Int> = List(15) { it }


    @Suppress("unused") // Called by Py4J
    fun getValidActions(): List<Int> = validActions.map {
        when (it) {
            is Pass -> ACTION_PASS
            is CoinBet -> it.coins
        }
    }

    @Suppress("unused") // Called by Py4J
    fun step(actionIndex: Int): Triple<List<Int>, Int, Boolean> {
        val newState = runBlocking {
            game.placeBetForHumanPlayer(validActions[actionIndex])
            game.getGameState()
        }
        updateActionSpace()
        val gameStateArray = game.getGameState().toStateArray()
        val reward = calculateReward(newState)
        val gameOver = newState.gameEndState != null
        return Triple(gameStateArray, reward, gameOver) // TODO create object for this return type
    }

    private fun calculateReward(newState: GameState): Int {
        val lastRound = game.getLog().last()
        val playerWonLastRound = lastRound.roundWinner?.isHuman == true

        // Reward for winning losing the game
        if (newState.gameEndState != null) return if (playerWonLastRound) 100 else -100

        // Reward for winning losing the round
        return if(playerWonLastRound) lastRound.cardValue + 1 else 1
    }

    private fun updateActionSpace() {
        val players = game.players
        val humanPlayer = players.value.find { it.isHuman } ?: throw IllegalStateException("Human player not found")
        val highestBetInCoins = players.value.getHighestBetInCoins()
        val coinBids = IntRange(highestBetInCoins + 1, humanPlayer.coins).map { CoinBet(it) }
        validActions = listOf(Pass) + coinBids
    }

    private fun GameEngine.getGameState(): GameState {
        return GameState(goalScore, players.value, card.value, gameEndState.value)
    }

    private fun createNewGameEngine(modelName: String): GameEngine {
        val settings = GameSettings.forDifficulty(GameDifficulty.EASY)
        val cardDeck = CardDeck(settings.numOfCardDecks)
        val ratingSystem = EloRatingSystem(Leaderboard(emptyMap()))
        val sounds = Sounds(NoOpSoundPlayer())
        val players = PlayerFactory(settings).createPlayers(modelName)
        return GameEngine(
            players, cardDeck, settings, ratingSystem, sounds,
            speedMode = SpeedMode.INSTANTANEOUS
        )
    }

//    @Suppress("unused") // Called by Py4J
//    fun start() {
//        println("Let's do some ML training!")
//        runBlocking {
//            val game = createNewGameEngine("CP1")
//            val gameStateFlow = game.getGameStateFlow()
//
//            println("Setting collection")
//            val stateCollectionJob = launch {
//                gameStateFlow.collect { gameState ->
//                    println("Game state: $gameState")
//                    step(gameState, game)
//                }
//            }
//            println("Starting game")
//            game.startGame()
//
//            while (game.gameEndState.value == null) {
//                println("Game is not over yet")
//                delay(10)
//            }
//
//            println("Game is over NOW")
//            stateCollectionJob.cancel()
//        }
//        println("done")
//    }

//private fun GameEngine.getGameStateFlow(): Flow<GameState> {
//    val cardFlow = card
//    val playersFlow = players
//    val gameEndStateFlow = gameEndState
//
//    return combine(cardFlow, playersFlow, gameEndStateFlow) { card, players, gameEndState ->
//        GameState(players, card, gameEndState)
//    }
//}
//
//private suspend fun step(gameState: GameState, game: GameEngine) {
//    val player = gameState.players.find { it.isHuman } ?: throw IllegalStateException("Human player not found")
//    if (player.isHuman && player.isCurrentPlayer) {
//        println("Human player's turn")
//        val bet = player.generateBet(gameState.card!!.points, gameState.players)
//        println("Human player's bet: $bet")
//        game.placeBetForHumanPlayer(bet)
//    }
//}
}

private data class GameState(
    val goalScore: Int,
    val players: List<Player>,
    val card: Card?,
    val gameEndState: GameEndState?
) {
    fun toStateArray(): List<Int> {
        val humanPlayer = players.find { it.isHuman } ?: throw IllegalStateException("Human player not found")
        val opponents = players.filter { !it.isHuman }

        val startingPlayerIndex = players.indexOfFirst { it.isFirstInThisRound }
        val cardPoints = card?.points ?: ACTION_PASS
        val pointsMissing = goalScore - humanPlayer.score
        val coins = humanPlayer.coins

        return listOf(startingPlayerIndex, cardPoints, pointsMissing, coins) + getOpponentStates(opponents)
    }

    private fun getOpponentStates(opponents: List<Player>) = opponents.flatMap { opponent ->
        listOf(
            goalScore - opponent.score,
            opponent.coins,
            when (val bet = opponent.bet) {
                is Pass -> ACTION_PASS
                is CoinBet -> bet.coins
                else -> NO_ACTION
            }
        )
    }
}
