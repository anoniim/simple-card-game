package engine

import engine.player.Player
import kotlin.math.ceil
import kotlin.random.Random

// TODO Add a rule: If the card would win the game for the human player, always bet

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

class RandomPlusOneBettingStrategy(
    private val goalScore: Int
) : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val lowestPossibleBet = highestBet + 1
        val highestPossibleBet = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && highestPossibleBet > highestBet) {
            return CoinBet(highestPossibleBet)
        }
        // If this is the first bet, pick a random bet
        if (highestBet == 0) {
            val randomBet = Random.nextInt(lowestPossibleBet, highestPossibleBet + 1)
            return CoinBet(randomBet)
        }
        // Otherwise bet plus one
        return if (lowestPossibleBet <= player.coins) {
            CoinBet(lowestPossibleBet)
        } else Pass
    }
}

class ManualBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        return Pass
    }
}

open class StandardBettingStrategy(
    private val takeFactor: Double,
    private val goalScore: Int,
) : BettingStrategy {
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val coins = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && coins > highestBet) {
            return CoinBet(coins)
        }
        val idealBet = ceil(cardValue * takeFactor).toInt()
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

class HighStandardBettingStrategy(
    private val minCardValue: Int,
    takeFactor: Double,
    private val goalScore: Int,
) : StandardBettingStrategy(takeFactor, goalScore){
    override fun generateBet(cardValue: Int, player: Player, highestBet: Int): Bet {
        val coins = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && coins > highestBet) {
            return CoinBet(coins)
        }
        // Don't take anything below minimum value
        if (cardValue < minCardValue) return Pass
        return super.generateBet(cardValue, player, highestBet)
    }
}