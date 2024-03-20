import engine.*
import engine.player.Player
import kotlin.math.ceil
import kotlin.random.Random

interface BettingStrategy {
    fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet
}

class PlusOneBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val requiredBet = highestBet + 1
        return if (requiredBet <= player.coins) {
            CoinBet(requiredBet)
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

class StandardBettingStrategy(
    private val takeFactor: Double,
) : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val idealBet = ceil(cardValue * takeFactor).toInt()
        val coins = player.coins
        if (highestBet == 0) {
            return if (idealBet <= coins) CoinBet(idealBet) else CoinBet(coins)
        }
        val requiredBet = highestBet + 1
        val take = requiredBet <= idealBet
        return if (requiredBet <= coins && take) {
            CoinBet(requiredBet)
        } else Pass
    }
}