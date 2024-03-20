import engine.*
import engine.player.Player
import kotlin.random.Random

interface BettingStrategy {
    fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet
}

class PlusOneBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val desiredBet = highestBet + 1
        return if (desiredBet <= player.coins) {
            CoinBet(desiredBet)
        } else Pass
    }
}

class RandomBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val lowestPossibleBet = highestBet + 1
        val highestPossibleBet = player.coins
        return if (lowestPossibleBet < highestPossibleBet) {
            val randomBet = Random.nextInt(lowestPossibleBet, highestPossibleBet + 1)
            CoinBet(randomBet)
        } else Pass
    }
}

class ManualBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        return Pass
    }

}