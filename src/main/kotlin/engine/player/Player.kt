package engine.player

import engine.BettingStrategy
import engine.Bet
import engine.CoinBet
import engine.Pass

data class Player(
    val id: PlayerId,
    val name: String,
    val isHuman: Boolean,
    val coins: Int,
    val score: Int,
    val bet: Bet? = null,
    val isFirstInThisRound: Boolean = false,
    val isCurrentPlayer: Boolean = false,
    val isRoundWinner: Boolean = false,
    val bettingStrategy: BettingStrategy,
) {
    fun generateBet(points: Int, players: List<Player>): Bet {
        return bettingStrategy.generateBet(points, players, this)
    }
}


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

fun List<Player>.updateFirstPlayer(lastWinnerIndex: Int, newFirstPlayerIndex: Int): List<Player> {
    val currentPlayerIndex = indexOfFirst(Player::isCurrentPlayer)
    val currentFirstPlayerIndex = indexOfFirst(Player::isFirstInThisRound)
    return toMutableList().apply {
        if (lastWinnerIndex >= 0) {
            set(lastWinnerIndex, get(lastWinnerIndex).copy(isRoundWinner = false))
        }
        set(currentPlayerIndex, get(currentPlayerIndex).copy(isCurrentPlayer = false))
        set(currentFirstPlayerIndex, get(currentFirstPlayerIndex).copy(isFirstInThisRound = false))
        set(newFirstPlayerIndex, get(newFirstPlayerIndex).copy(isCurrentPlayer = true, isFirstInThisRound = true))
    }
}

fun List<Player>.updateScore(player: Player, updatedCoins: Int, updatedScore: Int): MutableList<Player> {
    return toMutableList().apply {
        set(indexOf(player), player.copy(coins = updatedCoins, score = updatedScore, isRoundWinner = true))
    }
}

fun List<Player>.allPlusOneCoin(): MutableList<Player> {
    return toMutableList().apply {
        forEach { player ->
            set(indexOf(player), player.copy(coins = player.coins + 1))
        }
    }
}

fun List<Player>.placeBet(player: Player, bet: Bet): List<Player> {
    return if (player.isValid(bet)) {
        toMutableList().apply {
            set(indexOf(player), player.copy(bet = bet))
        }
    } else {
        println("! Player ${player.name} tried to bet $bet but has only ${player.coins})")
        emptyList()
    }
}

private fun Player.isValid(bet: Bet): Boolean {
    return (bet is Pass) || (bet is CoinBet && bet.coins <= coins)
}
