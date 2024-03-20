import engine.*
import engine.player.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

private const val ACTION_DELAY = 1000L

class GameEngine(
    players: List<Player>,
    private val cardDeck: CardDeck,
    private val settings: GameSettings,
) {

    private val _players = MutableStateFlow(players)
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _card = MutableStateFlow<Card?>(null)
    val card: StateFlow<Card?> = _card.asStateFlow()

    private val _winner = MutableStateFlow<Player?>(null)
    val winner: StateFlow<Player?> = _winner.asStateFlow()

    private val currentRound: Round = Round.initial(players.size)

    suspend fun startGame() {
        drawNewCard()
        delay(ACTION_DELAY)
        val currentPlayer = players.value.getCurrentPlayer()
        if (!currentPlayer.isHuman) {
            executeAiPlayerMoves()
        } else {
            // Human player's turn
        }
    }

    private suspend fun executeAiPlayerMoves() {
        while (!players.value.getCurrentPlayer().isHuman) {
            placeBetForAiPlayer(players.value.getCurrentPlayer())
            delay(ACTION_DELAY)
            progress()
        }
    }

    private fun placeBetForAiPlayer(currentAiPlayer: Player) {
        val highestBet = getHighestBetInCoins()
        val aiPlayerBet = currentAiPlayer.generateBet(card.value!!.points, highestBet)
        _players.value = players.value.placeBet(currentAiPlayer, aiPlayerBet)
    }

    suspend fun placeBetForHumanPlayer(bet: Bet) {
        val player: Player = players.value.getCurrentPlayer()
        val updatedPlayers = players.value.placeBet(player, bet)
        if (updatedPlayers.isNotEmpty()) {
            _players.value = updatedPlayers
            delay(ACTION_DELAY)
            progress()
            executeAiPlayerMoves()
        }
    }

    private suspend fun progress() {
        if (currentRound.haveAllPlayersPlayed()) {
            delay(ACTION_DELAY)
            // All players have played in this round, evaluate this round
            updateRoundWinningPlayer()
            // Is there a winner?
            val overallWinner = getOverallWinner()
            if (overallWinner != null) {
                delay(ACTION_DELAY)
                _winner.value = overallWinner
            } else {
                progressToNextRound()
            }
        } else {
            // Set next player who hasn't played yet in this round
            val nextPlayerIndex = currentRound.progressToNextPlayer()
            _players.value = players.value.updateCurrentPlayer(nextPlayerIndex)
        }
    }

    private fun getOverallWinner() = players.value.find { it.score >= settings.goalScore }

    private suspend fun progressToNextRound() {
        resetBets()
        resetCard()
        delay(ACTION_DELAY)

        setNewFirstPlayer()
        delay(ACTION_DELAY)

        drawNewCard()
    }

    private fun resetCard() {
        _card.value = null
    }

    private fun drawNewCard() {
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

    private fun getHighestBetInCoins(): Int {
        val bets = players.value.map(Player::bet)
        return if (bets.isNotEmpty()) {
            bets.maxOf {
                when (it) {
                    is CoinBet -> it.coins
                    else -> 0
                }
            }
        } else 0
    }

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
            val updatedPlayers = playersWithScore.allPlusOneCoin()
            currentRound.roundWinner = players.value.indexOf(roundWinner)
            _players.value = updatedPlayers
        }
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
