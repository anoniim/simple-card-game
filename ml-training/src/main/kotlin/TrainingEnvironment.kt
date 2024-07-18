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

private val gameDifficulty = GameDifficulty.MEDIUM

private const val ACTION_SPACE_SIZE = 15
private const val INVALID_ACTION = -1
private const val NO_ACTION = -1
private const val ACTION_PASS = 0

private const val gameEndRewardMultiplier = 100
private const val ratioBonus = 5

class TrainingEnvironment {

    @Suppress("MemberVisibilityCanBePrivate")  // Called by Py4J
    val modelName = "CP1"
    @Suppress("unused") // Called by Py4J
    val gameStateArraySize = 13

    private lateinit var game: GameEngine // Initialized in reset()
    private lateinit var validBids: IntRange // Initialized in reset()

    @Suppress("unused") // Called by Py4J
    fun reset(): List<Int> {
        game = createNewGameEngine(modelName)
        return runBlocking {
            game.startGame()
            updateActionSpace()
            game.getGameState()
        }.toStateArray()
    }

    @Suppress("unused") // Called by Py4J
    fun getActionSpaceSize() = ACTION_SPACE_SIZE

    @Suppress("unused") // Called by Py4J
    fun getActions() = List(ACTION_SPACE_SIZE) { it }.map { action ->
        when (action) {
            0 -> ACTION_PASS
            in validBids -> action
            else -> INVALID_ACTION
        }
    }

    @Suppress("unused") // Called by Py4J
    fun step(action: Int): List<Any> {
        val newState = runBlocking {
            game.placeBetForHumanPlayer(action.toBid())
            game.getGameState()
        }
        updateActionSpace()
        val gameStateArray = game.getGameState().toStateArray()
        val reward = calculateReward(newState)
        val gameOver = newState.gameEndState != null
        return listOf(gameStateArray, reward, gameOver) // TODO create object for this return type
    }

    private fun calculateReward(newState: GameState): Float {
        val lastRound = game.getLog().last()
        val playerWonLastRound = lastRound.roundWinner?.isHuman == true

        // Reward for winning/losing the game
        if (newState.gameEndState != null) {
            val gameEndReward = gameEndRewardMultiplier * newState.goalScore.toFloat()
            return if (playerWonLastRound) gameEndReward else -gameEndReward
        }

        // Reward for winning/losing the round
        return if (playerWonLastRound) {
            val cardValue = lastRound.cardValue.toFloat()
            val cardPrice = (lastRound.roundWinner?.bet as CoinBet).coins.toFloat()
            val valuePriceRatio = cardValue / cardPrice
            cardValue + ratioBonus * valuePriceRatio
        } else {
            // Penalize for losing the round (low reward)
            0.5f
        }
    }

    private fun updateActionSpace() { // TODO Can this generate a list with -1 as a valid action?
        val players = game.players
        val humanPlayer = players.value.find { it.isHuman } ?: throw IllegalStateException("Human player not found")
        val highestBidInCoins = players.value.getHighestBetInCoins()
        val lowestPossibleBid = highestBidInCoins + 1
        validBids = if (lowestPossibleBid < humanPlayer.coins) IntRange.EMPTY else IntRange(lowestPossibleBid, humanPlayer.coins)
    }

    private fun GameEngine.getGameState(): GameState {
        return GameState(goalScore, players.value, card.value, gameEndState.value)
    }

    private fun createNewGameEngine(modelName: String): GameEngine {
        val settings = GameSettings.forDifficulty(gameDifficulty)
        val cardDeck = CardDeck(settings.numOfCardDecks)
        val ratingSystem = EloRatingSystem(Leaderboard(emptyMap()))
        val sounds = Sounds(NoOpSoundPlayer())
        val players = PlayerFactory(settings).createPlayers(modelName)
        return GameEngine(
            players, cardDeck, settings, ratingSystem, sounds,
            speedMode = SpeedMode.INSTANTANEOUS
        )
    }

    private fun Int.toBid() = when (this) {
        INVALID_ACTION -> throw IllegalArgumentException("Tried to bid invalid action ($this)")
        ACTION_PASS -> Pass
        else ->  CoinBet(this)
    }
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
