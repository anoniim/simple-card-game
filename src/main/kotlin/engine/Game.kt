import engine.*
import engine.player.Player
import engine.player.PlayerId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


private const val ACTION_DELAY = 1000L

class Game(
    val players: Map<PlayerId, Player>,
    private val cardDeck: CardDeck,
    private val betGenerator: BetGenerator,
    private val settings: GameSettings,
) {

    private val _state = MutableStateFlow(initialGameState(players))
    val state: StateFlow<ActiveGameState> = _state.asStateFlow()

    private var currentRound = Round.initial(players.keys.toList())
    fun isCurrentPlayerHuman() = players[currentRound.currentPlayerId]!!.isHuman
    fun isPlayerFirst(playerId: PlayerId) = currentRound.firstPlayerId == playerId

    init {
        GlobalScope.launch(Dispatchers.IO) {
            state.collect { println("State changed: $it") }
        }
    }

    private fun initialGameState(players: Map<PlayerId, Player>) = ActiveGameState.initialState(
        settings,
        players,
        cardDeck.drawCard()
    )

    suspend fun startGame() {
        executeAiPlayerMoves()
    }

    private suspend fun executeAiPlayerMoves() {
        while (players[currentRound.currentPlayerId]!!.isNotHuman) {
            placeBetForAiPlayer()
            progress()
        }
    }

    private fun placeBetForAiPlayer() {
        val currentAiPlayerId = currentRound.currentPlayerId
        val coins = state.value.coins[currentAiPlayerId]!!
        val score = state.value.score[currentAiPlayerId]!!
        val highestBet = (state.value.getHighestBet()?.value as CoinBet?)?.coins ?: 0
        val aiPlayerBet = betGenerator.generateBet(state.value.card.points, coins, score, highestBet)
        _state.value = placeBet(currentAiPlayerId, aiPlayerBet)
    }

    suspend fun placeBetForHumanPlayer(bet: Bet) {
        _state.value = placeBet(currentRound.currentPlayerId, bet)
        progress()
        executeAiPlayerMoves()
    }

    private fun placeBet(playerId: PlayerId, bet: Bet): ActiveGameState {
        validateBet(playerId, bet)
        val updatedBets = state.value.bets.toMutableMap().apply { this[playerId] = bet }
        return state.value.copy(bets = updatedBets)
    }

    private fun validateBet(playerId: PlayerId, bet: Bet) {
        if (bet is CoinBet) {
            val playerCoins = state.value.coins[playerId]!!
            if (bet.coins > playerCoins)
                throw IllegalStateException("engine.Player (ID: $playerId) doesn't have ${bet.coins} (has only $playerCoins)")
        }
    }

    private suspend fun progress() {
        delay(ACTION_DELAY)
        if (currentRound.haveAllPlayersPlayed()) {
            // All players have played in this round, evaluate this round
            _state.value = evaluateRound()
            val overallWinner = state.value.score.filter { it.value >= settings.goalScore }.keys.firstOrNull()
            _state.value = if (overallWinner != null) setWinner(players[overallWinner]) else progressToNextRound()
        } else {
            // Set next player who hasn't played yet in this round
            currentRound = currentRound.progressToNextPlayer()
            _state.value = state.value
        }
        delay(ACTION_DELAY)
    }

    private fun setWinner(winner: Player?) = _state.value.copy(winner = winner)

    private fun evaluateRound(): ActiveGameState {
        val highestBetInfo = state.value.getHighestBet() ?: throw IllegalStateException("No bets placed")
        val playerId = highestBetInfo.key
        val highestBet = (highestBetInfo.value as CoinBet).coins
        val winningPoints = state.value.card.points
        val updatedCoins = state.value.coins.toMutableMap().apply { this[playerId] = this[playerId]!! - highestBet }
        val updatedScore = state.value.score.toMutableMap().apply { this[playerId] = this[playerId]!! + winningPoints }
        return state.value.copy(
            coins = updatedCoins,
            score = updatedScore,
        )
    }

    private fun List<Player>.getById(playerId: PlayerId): Player {
        return find { it.id == playerId } ?: throw IllegalArgumentException("Player ID $playerId not found")
    }

    private fun progressToNextRound(): ActiveGameState {
        return state.value.copy(
            card = cardDeck.drawCard(),
            bets = HashMap(),
        )
    }

    data class Round private constructor(
        private val playerIds: List<PlayerId>,
        val firstPlayerId: PlayerId,
        val currentPlayerId: PlayerId,
    ) {

        private val currentPlayerIndex = playerIds.indexOf(currentPlayerId)

        fun progressToNextPlayer(): Round {
            val nextPlayerIndex = currentPlayerIndex.next()
            return copy(currentPlayerId = playerIds[nextPlayerIndex])
        }

        fun progressToNextRound(): Round {
            val newFirstPlayerIndex = currentPlayerIndex.next().next()
            return copy(
                firstPlayerId = playerIds[newFirstPlayerIndex],
                currentPlayerId = playerIds[newFirstPlayerIndex]
            )
        }

        fun haveAllPlayersPlayed(): Boolean {
            val nextPlayerIndex = currentPlayerIndex.next()
            return playerIds[nextPlayerIndex] == firstPlayerId
        }

        private fun Int.next(): Int {
            return (this + 1) % playerIds.size
        }

        companion object {
            fun initial(playerIds: List<PlayerId>) = Round(
                playerIds,
                firstPlayerId = playerIds.first(),
                currentPlayerId = playerIds.first()
            )
        }
    }
}
