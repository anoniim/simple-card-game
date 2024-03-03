package engine

import engine.player.Player
import engine.player.updateScore

sealed class GameState

data class ActiveGameState(
    val players: List<Player>,
    val card: Card,
    val currentRound: Round = Round.initial(players.size),
) : GameState() {

    fun isCurrentPlayerHuman() = players[currentRound.currentPlayer].isHuman.also { println("isCurrentPlayerHuman: $it") }
    fun isPlayerFirst(playerIndex: Int) = currentRound.firstPlayer == playerIndex

    fun progressToNextPlayer(): ActiveGameState {
        return copy(currentRound = currentRound.progressToNextPlayer())
    }

    fun progressToNextRound(newCard: Card): ActiveGameState {
        val newRound = currentRound.progressToNextRound()
        // remove bets from previous round
        val playersWithoutBets = players.map { player -> player.copy(bet = null) }
        return copy(
            currentRound = newRound,
            card = newCard,
            players = playersWithoutBets,
        )
    }

    fun haveAllPlayersPlayed(): Boolean {
        return currentRound.haveAllPlayersPlayed()
    }

    private fun getRoundWinner(): Player? = players.maxByOrNull(::betToCoins)

    fun getHighestBetInCoins(): Int {
        val bets = players.map(Player::bet)
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

    fun evaluateRound(): ActiveGameState {
        return if (players.map(Player::bet).all { it is Pass }) {
            // No bets placed, no winner, no need to update scores
            this
        } else {
            val roundWinner = getRoundWinner() ?: throw IllegalStateException("No bets placed")
            val winningBet = (roundWinner.bet as CoinBet).coins
            val winningPoints = card.points
            val updatedCoins = roundWinner.coins - winningBet
            val updatedScore = roundWinner.score + winningPoints
            val updatedPlayers = players.updateScore(roundWinner, updatedCoins, updatedScore)
            copy(players = updatedPlayers)
        }
    }

    companion object {
        fun initialState(players: List<Player>, card: Card): ActiveGameState {
            return ActiveGameState(
                players = players,
                card = card,
            )
        }
    }

    data class Round(
        val playerCount: Int,
        val firstPlayer: Int,
        val currentPlayer: Int,
    ) {

        fun progressToNextPlayer(): Round {
            val nextPlayerIndex = currentPlayer.next()
            return copy(currentPlayer = nextPlayerIndex)
        }

        fun progressToNextRound(): Round {
            val newFirstPlayer = currentPlayer.next().next()
            return copy(
                firstPlayer = newFirstPlayer,
                currentPlayer = newFirstPlayer
            )
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
                currentPlayer = 0
            )
        }
    }
}