import engine.ActiveGameState
import kotlinx.coroutines.flow.MutableStateFlow

private const val STARTING_COINS = 10
private const val STARTING_POINTS = 0
private const val NUM_OF_CARD_DECKS = 1
private const val AI_PLAYER_COUNT = 3
private const val GOAL_SCORE = 30

class Game(
    private val playerName: String,
) {

    private val aiPlayerCount = AI_PLAYER_COUNT
    private val cardDeck = CardDeck(NUM_OF_CARD_DECKS)
    private val humanPlayerId = PlayerId(AI_PLAYER_COUNT)

    val state = MutableStateFlow(initialGameState())

    private val players = state.value.players

    private fun initialGameState(): ActiveGameState {
        val allPlayers = generatePlayers()
        val firstPlayerId = allPlayers.first().id.value
        return ActiveGameState(allPlayers, generateFirstRound(), firstPlayerId)
    }

    private fun generatePlayers(): List<Player> {
        return createAiPlayer() + createHumanPlayer()
    }

    private fun createAiPlayer(): List<Player> {
        val names = aiPlayerNames.shuffled().take(aiPlayerCount)
        return List(aiPlayerCount) { Player(PlayerId(it), names[it], STARTING_COINS, STARTING_POINTS) }
    }

    private fun createHumanPlayer() = Player(
        PlayerId(aiPlayerCount), // Human is the last player in the first round
        playerName, STARTING_COINS, STARTING_POINTS
    )

    private fun generateFirstRound(): Round {
        return generateNewRound(0)
    }

    fun startGame() {
        executeAiPlayerMoves()
    }

    private fun executeAiPlayerMoves() {
        while (players[state.value.currentPlayerIndex].id != humanPlayerId) {
            placeBetForAiPlayer()
            updateGameState()
        }
    }

    private fun placeBetForAiPlayer() {
        val currentAiPlayer = players[state.value.currentPlayerIndex]
        val aiPlayerBet = generateBet(currentAiPlayer)
        placeBet(currentAiPlayer.id, aiPlayerBet)
    }

    private fun generateBet(aiPlayer: Player): Bet {
        val currentRound = state.value.currentRound
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
        state.value.currentRound.bets[playerId] = bet
    }

    private fun validateBet(playerId: PlayerId, bet: Bet) {
        if (bet is CoinBet) {
            val playerCoins = players.getById(playerId).coins
            if (bet.coins > playerCoins)
                throw IllegalStateException("Player (ID: $playerId) doesn't have ${bet.coins} (has only $playerCoins)")
        }
    }

    private fun updateGameState() {
        val firstPlayerIndex = state.value.currentRound.firstPlayerIndex
        val nextPlayerIndex = state.value.currentPlayerIndex.nextPlayerIndex()
        state.value = if (nextPlayerIndex != firstPlayerIndex) {
            // Set next player who hasn't played yet in this round
            progressToNextPlayer(nextPlayerIndex)
        } else {
            // All players have played in this round, evaluate this round
            evaluateRound()
            val winner = players.find { it.score >= GOAL_SCORE }
            if (winner != null) setWinner(winner) else progressToNextRound(firstPlayerIndex)
        }
    }

    private fun setWinner(winner: Player?) = state.value.copy(winner = winner)

    private fun evaluateRound() {
        val currentRound = state.value.currentRound
        val winningPlayer = players.getById(currentRound.winnerId())
        val winningPoints = currentRound.card.points
        winningPlayer.coins -= currentRound.highestBet()
        winningPlayer.score += winningPoints
    }

    private fun List<Player>.getById(playerId: PlayerId): Player {
        return find { it.id == playerId } ?: throw IllegalArgumentException("Player ID $playerId not found")
    }

    private fun progressToNextRound(firstPlayerIndex: Int): ActiveGameState {
        val newFirstPlayerIndex = firstPlayerIndex.nextPlayerIndex()
        val newRound = generateNewRound(newFirstPlayerIndex)
        return state.value.copy(currentRound = newRound, currentPlayerIndex = newFirstPlayerIndex)
    }

    private fun progressToNextPlayer(nextPlayerIndex: Int) = state.value.copy(currentPlayerIndex = nextPlayerIndex)

    private fun Int.nextPlayerIndex(): Int {
        return (this + 1) % players.size
    }

    private fun generateNewRound(firstPlayerIndex: Int): Round {
        return Round(firstPlayerIndex = firstPlayerIndex, cardDeck.drawCard(), HashMap())
    }
}

class Round(
    val firstPlayerIndex: Int,
    val card: Card,
    val bets: MutableMap<PlayerId, Bet?>,
) {
    fun highestBet(): Int {
        return if (bets.isNotEmpty()) {
            bets.maxOf {
                when (val bet = it.value) {
                    is CoinBet -> bet.coins
                    else -> 0
                }
            }
        } else 0
    }

    fun winnerId() = bets.maxByOrNull {
        when (val bet = it.value) {
            is CoinBet -> bet.coins
            else -> 0
        }
    }?.key ?: throw IllegalStateException("Request for the winner while no bets have been placed")
}

@JvmInline
value class PlayerId(val value: Int)

sealed class Bet
class CoinBet(val coins: Int) : Bet()
data object Pass : Bet()

class Player(
    val id: PlayerId,
    val name: String,
    var coins: Int,
    var score: Int,
)

private val aiPlayerNames = listOf(
    "John",
    "Thomas",
    "Debbie",
    "Camila",
    "Bob",
    "Lucy",
    "Diana",
    "Charlie",
    "Meghan",
    "Cedric",
    "Mike",
    "Bart",
    "Lisa",
)

class CardDeck(
    numOfDecks: Int
) {
    private val deck = generateDeck(numOfDecks).shuffled().toMutableList()

    private fun generateDeck(numOfDecks: Int): List<Card> {
        return List(numOfDecks) { Card.entries }.flatten()
    }

    fun drawCard(): Card {
        return deck.removeAt(0)
    }
}

enum class Card(
    val displayValue: String,
    val points: Int,
) {
    CARD_1("1", 1),
    CARD_2("2", 2),
    CARD_3("3", 3),
    CARD_4("4", 4),
    CARD_5("5", 5),
    CARD_6("6", 6),
    CARD_7("7", 7),
    CARD_8("8", 8),
    CARD_9("9", 9),
    CARD_10("10", 10),
    CARD_J("J", 11),
    CARD_Q("Q", 12),
    CARD_K("K", 13),
}