import engine.*
import engine.player.Player

class BetGenerator {

    fun generateBet(cardValue: Int, coins: Int, currentScore: Int, highestBet: Int): Bet {
        val desiredBet = highestBet + 1
        return if (desiredBet <= coins) {
            CoinBet(desiredBet)
        } else Pass
    }

}
