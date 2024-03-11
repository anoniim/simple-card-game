import engine.*
import engine.player.Player
import kotlin.random.Random

interface BetGenerator {
    fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet
}

class PlusOneBetGenerator : BetGenerator {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val desiredBet = highestBet + 1
        return if (desiredBet <= player.coins) {
            CoinBet(desiredBet)
        } else Pass
    }
}

class RandomBetGenerator : BetGenerator {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val lowestPossibleBet = highestBet + 1
        val highestPossibleBet = player.coins
        return if (lowestPossibleBet < highestPossibleBet) {
            val randomBet = Random.nextInt(lowestPossibleBet, highestPossibleBet + 1)
            CoinBet(randomBet)
        } else Pass
    }
}
