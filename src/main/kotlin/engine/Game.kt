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
        while (!state.value.isCurrentPlayerHuman()) {
            placeBetForAiPlayer()
            progress()
        }
    }

    private fun placeBetForAiPlayer() {
        val currentAiPlayerId = state.value.currentRound.currentPlayerId
        val coins = state.value.coins[currentAiPlayerId]!!
        val score = state.value.score[currentAiPlayerId]!!
        val highestBet = state.value.getHighestBetInCoins()
        val aiPlayerBet = betGenerator.generateBet(state.value.card.points, coins, score, highestBet)
        _state.value = placeBet(currentAiPlayerId, aiPlayerBet)
    }

    suspend fun placeBetForHumanPlayer(bet: Bet) {
        _state.value = placeBet(state.value.currentRound.currentPlayerId, bet)
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
        _state.value = if (state.value.haveAllPlayersPlayed()) {
            // All players have played in this round, evaluate this round
            _state.value = state.value.evaluateRound()
            val overallWinner = state.value.score.filter { it.value >= settings.goalScore }.keys.firstOrNull()
            if (overallWinner != null) {
                // Game over
                state.value.setWinner(players[overallWinner])
            } else {
                // Start next round
                state.value.progressToNextRound(cardDeck.drawCard())
            }
        } else {
            // Set next player who hasn't played yet in this round
            state.value.progressToNextPlayer()
        }
        delay(ACTION_DELAY)
    }
}
