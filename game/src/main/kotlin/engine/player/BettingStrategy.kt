package engine.player

import getHighestBetInCoins
import kotlin.math.ceil
import kotlin.math.min
import kotlin.random.Random

// TODO Add a rule: If the card would win the game for the human player, always bet

interface BettingStrategy {
    fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet
}

class PlusOneBettingStrategy(
    private val goalScore: Int
) : BettingStrategy {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        val highestBet = players.getHighestBetInCoins()
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

class TotalRandomBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        val highestBet = players.getHighestBetInCoins()
        val lowestPossibleBet = highestBet + 1
        val highestPossibleBet = player.coins
        return if (lowestPossibleBet < highestPossibleBet) {
            val randomBet = Random.nextInt(lowestPossibleBet, highestPossibleBet + 1)
            CoinBet(randomBet)
        } else Pass
    }
}

class RandomBettingStrategy(
    private val goalScore: Int
) : BettingStrategy {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        val highestBet = players.getHighestBetInCoins()
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

abstract class RandomPlusOneBettingStrategy(
    private val goalScore: Int
) : BettingStrategy {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        val highestBet = players.getHighestBetInCoins()
        val lowestPossibleBet = highestBet + 1
        val highestPossibleBet = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && highestPossibleBet > highestBet) {
            return CoinBet(highestPossibleBet)
        }
        // If the player has only one coin, bet it
        if (highestPossibleBet == 1) return CoinBet(1)
        // If this is the first bet, pick a random bet
        if (highestBet == 0) {
            val until = randomUntil(highestPossibleBet) + 1 // +1 because until is exclusive
            val randomBet = Random.nextInt(lowestPossibleBet, until)
            return CoinBet(randomBet)
        }
        // Otherwise bet plus one
        return if (lowestPossibleBet <= player.coins) {
            CoinBet(lowestPossibleBet)
        } else Pass
    }

    abstract fun randomUntil(highestPossibleBet: Int): Int
}

class HighestRandomPlusOneBettingStrategy(goalScore: Int) : RandomPlusOneBettingStrategy(goalScore) {
    override fun randomUntil(highestPossibleBet: Int): Int = highestPossibleBet
}

class ReasonableRandomPlusOneBettingStrategy(goalScore: Int) : RandomPlusOneBettingStrategy(goalScore) {
    override fun randomUntil(highestPossibleBet: Int): Int = 3 * highestPossibleBet / 4
}

class ConservativeRandomPlusOneBettingStrategy(goalScore: Int) : RandomPlusOneBettingStrategy(goalScore) {
    override fun randomUntil(highestPossibleBet: Int): Int {
        return highestPossibleBet / 2
    }
}

class ManualBettingStrategy : BettingStrategy {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        return Pass
    }
}

open class StandardBettingStrategy(
    private val takeFactor: Double,
    private val goalScore: Int,
) : BettingStrategy {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        val highestBet = players.getHighestBetInCoins()
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
        val canPayRequiredBid = requiredBet <= idealBet && requiredBet <= coins
        return if (canPayRequiredBid) {
            // If any of the upcoming players can bet more than the required bet, bet so much to beat others or highest possible bet until the ideal
            val playersWhoCanOverbid = players.filter { it != player && it.bet == null && it.coins > requiredBet + 1 }
            if (playersWhoCanOverbid.isNotEmpty()) {
                val idealOrToBeatOthers = playersWhoCanOverbid.maxOf { min(it.coins, min(player.coins, idealBet)) }
                CoinBet(idealOrToBeatOthers)
            } else CoinBet(requiredBet)
        } else Pass
    }
}

class HighStandardBettingStrategy(
    private val minCardValue: Int,
    takeFactor: Double,
    private val goalScore: Int,
) : StandardBettingStrategy(takeFactor, goalScore) {
    override fun generateBet(cardValue: Int, players: List<Player>, player: Player): Bet {
        val highestBet = players.getHighestBetInCoins()
        val coins = player.coins
        // If the card value is what the player needs to win, bet everything
        if (cardValue >= goalScore - player.score && coins > highestBet) {
            return CoinBet(coins)
        }
        // Don't take anything below minimum value
        if (cardValue < minCardValue) return Pass
        return super.generateBet(cardValue, players, player)
    }
}