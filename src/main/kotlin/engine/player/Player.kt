package engine.player

import engine.Bet
import engine.CoinBet


data class Player(
    val id: PlayerId,
    val name: String,
    val isHuman: Boolean,
    val coins: Int,
    val score: Int,
    val bet: Bet? = null,
    val isFirstInThisRound: Boolean = false,
    val isCurrentPlayer: Boolean = false,
)

@JvmInline
value class PlayerId(val value: Int)

fun List<Player>.setFirstPlayer(firstPlayerIndex: Int): List<Player> {
    return toMutableList().apply {
        set(firstPlayerIndex, get(firstPlayerIndex).copy(isFirstInThisRound = true, isCurrentPlayer = true))
    }
}

fun List<Player>.getCurrentPlayer(): Player {
    return get(indexOfFirst(Player::isCurrentPlayer))
}

fun List<Player>.updateCurrentPlayer(nextPlayerIndex: Int): List<Player> {
    val currentPlayerIndex = indexOfFirst(Player::isCurrentPlayer)
    return toMutableList().apply {
        set(currentPlayerIndex, get(currentPlayerIndex).copy(isCurrentPlayer = false))
        set(nextPlayerIndex, get(nextPlayerIndex).copy(isCurrentPlayer = true))
    }
}

fun List<Player>.updateFirstPlayer(newFirstPlayerIndex: Int): List<Player> {
    val currentPlayerIndex = indexOfFirst(Player::isCurrentPlayer)
    val currentFirstPlayerIndex = indexOfFirst(Player::isFirstInThisRound)
    return toMutableList().apply {
        set(currentPlayerIndex, get(currentPlayerIndex).copy(isCurrentPlayer = false))
        set(currentFirstPlayerIndex, get(currentFirstPlayerIndex).copy(isFirstInThisRound = false))
        set(newFirstPlayerIndex, get(newFirstPlayerIndex).copy(isCurrentPlayer = true, isFirstInThisRound = true))
    }
}

fun List<Player>.updateScore(player: Player, updatedCoins: Int, updatedScore: Int) =
    toMutableList().apply {
        set(indexOf(player), player.copy(coins = updatedCoins, score = updatedScore))
    }

fun List<Player>.placeBet(player: Player, bet: Bet): MutableList<Player> {
    player.validate(bet)
    return toMutableList().apply {
        set(indexOf(player), player.copy(bet = bet))
    }
}

private fun Player.validate(bet: Bet) {
    if (bet is CoinBet && bet.coins > coins)
        throw IllegalStateException("Player $name doesn't have ${bet.coins} (has only ${coins})")
}
