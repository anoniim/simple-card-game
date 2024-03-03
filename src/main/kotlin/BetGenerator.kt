import engine.*
import engine.player.Player

class BetGenerator {

    fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val desiredBet = highestBet + 1
        return if (desiredBet <= player.coins) {
            CoinBet(desiredBet)
        } else Pass
    }

}
