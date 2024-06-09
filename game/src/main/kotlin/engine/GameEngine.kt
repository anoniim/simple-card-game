import engine.Card
import engine.CardDeck
import engine.GameSettings
import engine.player.*
import engine.rating.EloRatingSystem
import engine.rating.Leaderboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.solvetheriddle.cardgame.Sounds

private const val ACTION_DELAY = 1000L

class GameEngine(
    players: List<Player>,
    private val cardDeck: CardDeck,
    private val settings: GameSettings,
    private val ratingSystem: EloRatingSystem,
    private val sounds: Sounds,
    private val speedMode: SpeedMode = SpeedMode.NORMAL,
) {

    val goalScore: Int = settings.goalScore

    private val _players = MutableStateFlow(players)
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _card = MutableStateFlow<Card?>(null)
    val card: StateFlow<Card?> = _card.asStateFlow()

    private val _gameEndState = MutableStateFlow<GameEndState?>(null)
    val gameEndState: StateFlow<GameEndState?> = _gameEndState.asStateFlow()

    private val currentRound: Round = Round.initial(players.size)

    suspend fun startGame() {
        drawNewCard()
        pause()
        val currentPlayer = players.value.getCurrentPlayer()
        if (!currentPlayer.isHuman) {
            executeAiPlayerMoves()
        } else {
            // Human player's turn
            println("Human player's turn")
        }
    }

    private suspend fun executeAiPlayerMoves() {
        while (!players.value.getCurrentPlayer().isHuman) {
            println("AI player's turn")
            placeBetForAiPlayer(players.value.getCurrentPlayer())
            pause()
            progress()
        }
    }

    private fun placeBetForAiPlayer(currentAiPlayer: Player) {
        val aiPlayerBet = currentAiPlayer.generateBet(card.value!!.points, players.value)
        _players.value = players.value.placeBet(currentAiPlayer, aiPlayerBet)
        sounds.aiPlayerBet(aiPlayerBet)
    }

    suspend fun placeBetForHumanPlayer(bet: Bet) {
        val player: Player = players.value.getCurrentPlayer()
        val updatedPlayers = players.value.placeBet(player, bet)
        sounds.humanPlayerBet(bet, players.value)
        if (updatedPlayers.isNotEmpty()) {
            _players.value = updatedPlayers
            pause()
            progress()
            executeAiPlayerMoves()
        }
    }

    private suspend fun progress() {
        if (currentRound.haveAllPlayersPlayed()) {
            pause()
            // All players have played in this round, evaluate this round
            updateRoundWinningPlayer()
            pause()

            // Is there a winner?
            val overallWinner = getOverallWinner()
            if (overallWinner != null) {
                sounds.gameOver(overallWinner)
                val updatedLeaderboard = ratingSystem.updateRatings(players.value, overallWinner)
                _gameEndState.value = GameEndState(overallWinner, updatedLeaderboard)
            } else {
                addOneCoinToAllPlayers()
                progressToNextRound()
            }
        } else {
            // Set next player who hasn't played yet in this round
            val nextPlayerIndex = currentRound.progressToNextPlayer()
            _players.value = players.value.updateCurrentPlayer(nextPlayerIndex)
            sounds.idling(players.value.getCurrentPlayer())
        }
    }

    private fun getOverallWinner() = players.value.find { it.score >= settings.goalScore }

    private suspend fun progressToNextRound() {
        resetBets()
        resetCard()
        pause()

        setNewFirstPlayer()
        pause()

        drawNewCard()
    }

    private suspend fun pause() {
        when(speedMode) {
            SpeedMode.INSTANTANEOUS -> return
            SpeedMode.NORMAL -> delay(ACTION_DELAY)
            SpeedMode.FAST -> delay(ACTION_DELAY / 2)
        }
    }

    private fun resetCard() {
        _card.value = null
    }

    private fun drawNewCard() {
        sounds.drawCard()
        _card.value = cardDeck.drawCard()
    }

    private fun setNewFirstPlayer() {
        val lastWinnerIndex = currentRound.roundWinner
        val newFirstPlayerIndex = currentRound.progressToNextRound()
        _players.value = players.value.updateFirstPlayer(lastWinnerIndex, newFirstPlayerIndex)
    }

    private fun resetBets() {
        val playersWithoutBets = players.value.map { player -> player.copy(bet = null) }
        _players.value = playersWithoutBets
    }

    private fun getRoundWinner(): Player? = players.value.maxByOrNull(::betToCoins)

    private fun betToCoins(player: Player) = when (val bet = player.bet) {
        is CoinBet -> bet.coins
        else -> 0
    }

    private fun updateRoundWinningPlayer() {
        if (players.value.map(Player::bet).all { it is Pass }) {
            // No bets placed, no winner, no need to update scores
        } else {
            val roundWinner = getRoundWinner() ?: throw IllegalStateException("No bets placed")
            val winningBet = (roundWinner.bet as CoinBet).coins
            val winningPoints = card.value!!.points
            val updatedCoins = roundWinner.coins - winningBet
            val updatedScore = roundWinner.score + winningPoints
            val playersWithScore = players.value.updateScore(roundWinner, updatedCoins, updatedScore)
            currentRound.roundWinner = players.value.indexOf(roundWinner)
            _players.value = playersWithScore
            sounds.roundWinner(roundWinner, winningPoints)
        }
    }

    private fun addOneCoinToAllPlayers() {
        val updatedPlayers = players.value.allPlusOneCoin()
        _players.value = updatedPlayers
    }

    fun penalizeExit(): Leaderboard {
        return ratingSystem.penalizeExit(players.value.find { it.isHuman })
    }

    data class Round(
        private val playerCount: Int,
        private var firstPlayer: Int,
        private var currentPlayer: Int,
        var roundWinner: Int,
    ) {

        fun progressToNextPlayer(): Int {
            val nextPlayerIndex = currentPlayer.next()
            currentPlayer = nextPlayerIndex
            return nextPlayerIndex
        }

        /**
         * Progresses to the next round by setting a new first and current player.
         * Returns the new first player index.
         */
        fun progressToNextRound(): Int {
            val newFirstPlayer = currentPlayer.next().next()
            firstPlayer = newFirstPlayer
            currentPlayer = newFirstPlayer
            roundWinner = -1
            return newFirstPlayer
        }

        fun haveAllPlayersPlayed(): Boolean {
            val nextPlayer = currentPlayer.next()
            return nextPlayer == firstPlayer
        }

        private fun Int.next(): Int {
            return (this + 1) % playerCount
        }

        companion object {
            fun initial(playerCount: Int) = Round(
                playerCount,
                firstPlayer = 0,
                currentPlayer = 0,
                roundWinner = -1,
            )
        }
    }
}

fun List<Player>.getHighestBetInCoins(): Int {
    val bets = this.map(Player::bet)
    return bets.maxOfOrNull {
        when (it) {
            is CoinBet -> it.coins
            else -> 0
        }
    } ?: 0
}

class GameEndState(
    val winner: Player,
    val leaderboard: Leaderboard,
)

enum class SpeedMode {
    INSTANTANEOUS,
    NORMAL,
    FAST,
}