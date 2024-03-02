package engine

import engine.player.Player
import engine.player.PlayerId

sealed class GameState

data class ActiveGameState(
    val coins: Map<PlayerId, Int>,
    val score: Map<PlayerId, Int>,
    val bets: Map<PlayerId, Bet?>,
    val card: Card,
    val winner: Player? = null,
) : GameState() {

    fun getPlayerInfo(playerId: PlayerId): PlayerInfo {
        return PlayerInfo(
            score.getValue(playerId),
            coins.getValue(playerId),
            bets.getValue(playerId),
        )
    }

    fun getHighestBet() = bets.maxByOrNull(::betToCoins)

    private fun betToCoins(it: Map.Entry<PlayerId, Bet?>) = when (val bet = it.value) {
        is CoinBet -> bet.coins
        else -> 0
    }

    companion object {
        fun initialState(settings: GameSettings, players: Map<PlayerId, Player>, card: Card): ActiveGameState {
            val coins = players.mapValues { settings.startingCoins }
            val score = players.mapValues { settings.startingPoints }
            val bets = players.mapValues { null }
            return ActiveGameState(
                coins = coins,
                score = score,
                card = card,
                bets = bets,
            )
        }
    }
}

class PlayerInfo(val score: Int, val coins: Int, val bet: Bet?)

data object MenuGameState : GameState() // TODO
data object GameOverState : GameState()