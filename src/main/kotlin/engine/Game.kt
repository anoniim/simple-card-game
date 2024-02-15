import engine.*
import engine.Player.Companion.createAiPlayer
import engine.Player.Companion.createHumanPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

const val STARTING_COINS = 10
const val STARTING_POINTS = 0
const val NUM_OF_CARD_DECKS = 1
const val AI_PLAYER_COUNT = 3
const val GOAL_SCORE = 30

class Game(
    private val playerName: String,
) {

    private val aiPlayerCount = AI_PLAYER_COUNT
    private val cardDeck = CardDeck(NUM_OF_CARD_DECKS)
    private val humanPlayerId = PlayerId(AI_PLAYER_COUNT)

    private val _state = MutableStateFlow(initialGameState())
    val state: Flow<ActiveGameState> = _state.asStateFlow()
        .onEach { delay(3000) }

    private val players = _state.value.players

    private fun initialGameState(): ActiveGameState {
        val allPlayers = generatePlayers()
        val firstPlayerId = allPlayers.first().id.value
        return ActiveGameState(allPlayers, generateFirstRound(), firstPlayerId)
    }

    private fun generatePlayers(): List<Player> {
        return createAiPlayer(aiPlayerCount) + createHumanPlayer(aiPlayerCount, playerName) // Human is the last player in the first round
    }

    private fun generateFirstRound(): Round {
        return generateNewRound(0)
    }

    fun startGame() {
        executeAiPlayerMoves()
    }

    private fun executeAiPlayerMoves() {
        while (players[_state.value.currentPlayerIndex].id != humanPlayerId) {
            placeBetForAiPlayer()
            updateGameState()
        }
    }

    private fun placeBetForAiPlayer() {
        val currentAiPlayer = players[_state.value.currentPlayerIndex]
        val aiPlayerBet = generateBet(currentAiPlayer)
        placeBet(currentAiPlayer.id, aiPlayerBet)
    }

    private fun generateBet(aiPlayer: Player): Bet {
        val currentRound = _state.value.currentRound
        val cardValue = currentRound.card.points
        val currentScore = aiPlayer.score
        val coins = aiPlayer.coins
        val highestBet = currentRound.highestBet()
        val desiredBet = highestBet + 1
        return if (desiredBet <= coins) {
            CoinBet(desiredBet)
        } else Pass
    }

    fun placeBetForHumanPlayer(bet: Bet) {
        placeBet(humanPlayerId, bet)
        updateGameState()
        executeAiPlayerMoves()
    }

    private fun placeBet(playerId: PlayerId, bet: Bet) {
        validateBet(playerId, bet)
        _state.value.currentRound.bets[playerId] = bet
    }

    private fun validateBet(playerId: PlayerId, bet: Bet) {
        if (bet is CoinBet) {
            val playerCoins = players.getById(playerId).coins
            if (bet.coins > playerCoins)
                throw IllegalStateException("engine.Player (ID: $playerId) doesn't have ${bet.coins} (has only $playerCoins)")
        }
    }

    private fun updateGameState() {
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
