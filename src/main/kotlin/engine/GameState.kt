package engine

import engine.player.Player
import engine.player.PlayerId

sealed class GameState

data class ActiveGameState(
    val players: Map<PlayerId, Player>,
    val coins: Map<PlayerId, Int>,
    val score: Map<PlayerId, Int>,
    val bets: Map<PlayerId, Bet?>,
    val card: Card,
    val winner: Player? = null,
    val currentRound: Round = Round.initial(players.keys.toList())
) : GameState() {

    fun isCurrentPlayerHuman() = players[currentRound.currentPlayerId]!!.isHuman.also { println("isCurrentPlayerHuman: $it") }
    fun isPlayerFirst(playerId: PlayerId) = currentRound.firstPlayerId == playerId

    fun progressToNextPlayer(): ActiveGameState {
        return copy(currentRound = currentRound.progressToNextPlayer())
    }

    fun progressToNextRound(newCard: Card): ActiveGameState {
        val newRound = currentRound.progressToNextRound()
        return copy(
            currentRound = newRound,
            card = newCard,
            bets = HashMap(),
        )
    }

    fun haveAllPlayersPlayed(): Boolean {
        return currentRound.haveAllPlayersPlayed()
    }

    fun getPlayerInfo(playerId: PlayerId): PlayerInfo {
        return PlayerInfo(
            score.getValue(playerId),
            coins.getValue(playerId),
            bets.getValue(playerId),
        )
    }

    private fun getHighestBet() = bets.maxByOrNull(::betToCoins)

    fun getHighestBetInCoins(): Int {
        return if (bets.isNotEmpty()) {
            bets.maxOf {
                when (val bet = it.value) {
                    is CoinBet -> bet.coins
                    else -> 0
                }
            }
        } else 0
    }

    private fun betToCoins(it: Map.Entry<PlayerId, Bet?>) = when (val bet = it.value) {
        is CoinBet -> bet.coins
        else -> 0
    }

    fun setWinner(winner: Player?) = copy(winner = winner)

    fun evaluateRound(): ActiveGameState {
        return if (bets.values.all { it is Pass }) {
            // No bets placed, no winner, no need to update scores
            this
        } else {
            val highestBetInfo = getHighestBet() ?: throw IllegalStateException("No bets placed")
            val playerId = highestBetInfo.key
            val highestBet = (highestBetInfo.value as CoinBet).coins
            val winningPoints = card.points
            val updatedCoins = coins.toMutableMap().apply { this[playerId] = this[playerId]!! - highestBet }
            val updatedScore = score.toMutableMap().apply { this[playerId] = this[playerId]!! + winningPoints }
            copy(
                coins = updatedCoins,
                score = updatedScore,
            )
        }
    }

    companion object {
        fun initialState(settings: GameSettings, players: Map<PlayerId, Player>, card: Card): ActiveGameState {
            val coins = players.mapValues { settings.startingCoins }
            val score = players.mapValues { settings.startingPoints }
            val bets = players.mapValues { null }
            return ActiveGameState(
                players = players,
                coins = coins,
                score = score,
                card = card,
                bets = bets,
            )
        }
    }

    data class Round(
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


class PlayerInfo(val score: Int, val coins: Int, val bet: Bet?)

data object MenuGameState : GameState() // TODO
data object GameOverState : GameState()