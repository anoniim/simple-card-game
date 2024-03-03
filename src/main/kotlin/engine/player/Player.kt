package engine.player

import engine.Bet
import engine.CoinBet


data class Player(
    val id: PlayerId,
    val name: String,
    val isHuman: Boolean,
    val coins: Int,
    val score: Int,
    val bet: Bet?,
)

@JvmInline
value class PlayerId(val value: Int)

operator fun List<Player>.get(playerId: PlayerId) = get(playerId.value)

fun List<Player>.updateScore(player: Player, updatedCoins: Int, updatedScore: Int) =
    toMutableList().apply {
        set(indexOf(player), player.copy(coins = updatedCoins, score = updatedScore))
    }

fun List<Player>.updateBet(player: Player, bet: Bet): MutableList<Player> {
    player.validate(bet)
    return toMutableList().apply {
        set(indexOf(player), player.copy(bet = bet))
    }
}

private fun Player.validate(bet: Bet) {
    if (bet is CoinBet && bet.coins > coins)
        throw IllegalStateException("Player $name doesn't have ${bet.coins} (has only ${coins})")
}
