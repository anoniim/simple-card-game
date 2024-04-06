package engine

import engine.player.Player
import kotlin.math.ceil
import kotlin.random.Random

interface BettingStrategy {
    fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet
}

class PlusOneBettingStrategy(
    private val goalScore: Int
) : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && player.coins > highestBet) {
            return CoinBet(player.coins)
        }
        val requiredBet = highestBet + 1
        return if (requiredBet <= player.coins) {
            CoinBet(requiredBet)
        } else Pass
    }
}

class RandomBettingStrategy(
    private val goalScore: Int
) : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val lowestPossibleBet = highestBet + 1
        val highestPossibleBet = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && highestPossibleBet > highestBet) {
            return CoinBet(highestPossibleBet)
        }
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
    private val goalScore: Int,
) : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val idealBet = ceil(cardValue * takeFactor).toInt()
        val coins = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && coins > highestBet) {
            return CoinBet(coins)
        }
        // If no one has bet yet, bet the ideal bet if possible
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