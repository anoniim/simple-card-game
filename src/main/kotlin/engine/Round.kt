package engine

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