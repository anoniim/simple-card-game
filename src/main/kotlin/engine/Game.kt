import engine.*
import engine.player.Player
import engine.player.updateBet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


private const val ACTION_DELAY = 1000L

class Game(
    val players: List<Player>,
    private val cardDeck: CardDeck,
    private val betGenerator: BetGenerator,
    private val settings: GameSettings,
) {

    private val _state = MutableStateFlow(initialGameState(players))
    val state: StateFlow<ActiveGameState> = _state.asStateFlow()

    private val _winner = MutableStateFlow<Player?>(null)
    val winner: StateFlow<Player?> = _winner.asStateFlow()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            state.collect { println("State changed: $it") }
        }
    }

    private fun initialGameState(players: List<Player>) = ActiveGameState.initialState(
        players,
        cardDeck.drawCard()
    )

    suspend fun startGame() {
        delay(ACTION_DELAY)
        executeAiPlayerMoves()
    }

    private suspend fun executeAiPlayerMoves() {
        while (!state.value.isCurrentPlayerHuman()) {
            placeBetForAiPlayer()
            progress()
        }
    }

    private fun placeBetForAiPlayer() {
        val currentAiPlayer = players[state.value.currentRound.currentPlayer]
        val highestBet = state.value.getHighestBetInCoins()
        val aiPlayerBet = betGenerator.generateBet(state.value.card.points, currentAiPlayer, highestBet)
        _state.value = placeBet(currentAiPlayer, aiPlayerBet)
    }

    suspend fun placeBetForHumanPlayer(bet: Bet) {
        _state.value = placeBet(players[state.value.currentRound.currentPlayer], bet)
        progress()
        executeAiPlayerMoves()
    }

    private fun placeBet(player: Player, bet: Bet): ActiveGameState {
        return state.value.copy(players = players.updateBet(player, bet))
    }

    private suspend fun progress() {
        delay(ACTION_DELAY)
        if (state.value.haveAllPlayersPlayed()) {
            // All players have played in this round, evaluate this round
            _state.value = state.value.evaluateRound()
            val overallWinner = players.find { it.score >= settings.goalScore }
            if (overallWinner != null) {
                _winner.value = overallWinner
            } else {
                // Start next round
                delay(ACTION_DELAY)
                _state.value = state.value.progressToNextRound(cardDeck.drawCard())
                delay(ACTION_DELAY)
            }
        } else {
            // Set next player who hasn't played yet in this round
            _state.value = state.value.progressToNextPlayer()
        }
    }
}
