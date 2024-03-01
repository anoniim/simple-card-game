import engine.*
import engine.player.Player
import engine.player.PlayerId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class Game(
    private val players: List<Player>,
) {
    private val cardDeck = CardDeck(NUM_OF_CARD_DECKS) // TODO Move to Koin module
    private val betGenerator = BetGenerator() // TODO Move to Koin module

    private val _state = MutableStateFlow(initialGameState(players))
    val state: StateFlow<ActiveGameState> = _state.asStateFlow()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            state.collect { println("State changed: $it") }
        }
    }

    private fun initialGameState(players: List<Player>): ActiveGameState {
        val firstPlayerId = players.first().id.value
        return ActiveGameState(players, generateFirstRound(), firstPlayerId)
    }

    private fun generateFirstRound(): Round {
        return generateNewRound(0)
    }

    suspend fun startGame() {
        executeAiPlayerMoves()
    }

    private suspend fun executeAiPlayerMoves() {
        while (players[_state.value.currentPlayerIndex].isNotHuman) {
            placeBetForAiPlayer()
            updateGameState()
        }
    }

    private fun placeBetForAiPlayer() {
        val currentAiPlayer = players[_state.value.currentPlayerIndex]
        val aiPlayerBet = betGenerator.generateBet(currentAiPlayer, _state.value.currentRound)
        placeBet(currentAiPlayer.id, aiPlayerBet)
    }

    suspend fun placeBetForHumanPlayer(bet: Bet) {
        placeBet(players[_state.value.currentPlayerIndex].id, bet)
        updateGameState()
        executeAiPlayerMoves()
    }

    private fun placeBet(playerId: PlayerId, bet: Bet) {
        validateBet(playerId, bet)
        _state.value.currentRound.bets[playerId] = bet
//        _state.value = state.value.copy().apply { currentRound.bets[playerId] = bet }
    }

    private fun validateBet(playerId: PlayerId, bet: Bet) {
        if (bet is CoinBet) {
            val playerCoins = players.getById(playerId).coins
            if (bet.coins > playerCoins)
                throw IllegalStateException("engine.Player (ID: $playerId) doesn't have ${bet.coins} (has only $playerCoins)")
        }
    }

    private suspend fun updateGameState() {
        val firstPlayerIndex = _state.value.currentRound.firstPlayerIndex
        val nextPlayerIndex = _state.value.currentPlayerIndex.nextPlayerIndex()
        _state.value = if (nextPlayerIndex != firstPlayerIndex) {
            // Set next player who hasn't played yet in this round
            progressToNextPlayer(nextPlayerIndex)
        } else {
            // All players have played in this round, evaluate this round
            evaluateRound()
            val winner = players.find { it.score >= GOAL_SCORE }
            if (winner != null) setWinner(winner) else progressToNextRound(firstPlayerIndex)
        }
        delay(1000)
    }

    private fun setWinner(winner: Player?) = _state.value.copy(winner = winner)

    private fun evaluateRound() {
        val currentRound = _state.value.currentRound
        val winningPlayer = players.getById(currentRound.winnerId())
        val winningPoints = currentRound.card.points
        winningPlayer.coins -= currentRound.highestBet()
        winningPlayer.score += winningPoints
    }

    private fun List<Player>.getById(playerId: PlayerId): Player {
        return find { it.id == playerId } ?: throw IllegalArgumentException("engine.Player ID $playerId not found")
    }

    private fun progressToNextRound(firstPlayerIndex: Int): ActiveGameState {
        val newFirstPlayerIndex = firstPlayerIndex.nextPlayerIndex()
        val newRound = generateNewRound(newFirstPlayerIndex)
        return _state.value.copy(currentRound = newRound, currentPlayerIndex = newFirstPlayerIndex)
    }

    private fun progressToNextPlayer(nextPlayerIndex: Int) = _state.value.copy(currentPlayerIndex = nextPlayerIndex)

    private fun Int.nextPlayerIndex(): Int {
        return (this + 1) % players.size
    }

    private fun generateNewRound(firstPlayerIndex: Int): Round {
        return Round(firstPlayerIndex = firstPlayerIndex, cardDeck.drawCard(), HashMap())
    }
}
