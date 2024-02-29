import engine.*
import engine.player.Player

class BetGenerator {

    fun generateBet(aiPlayer: Player, currentRound: Round): Bet {
        val cardValue = currentRound.card.points
        val currentScore = aiPlayer.score
        val coins = aiPlayer.coins
        val highestBet = currentRound.highestBet()
        val desiredBet = highestBet + 1
        return if (desiredBet <= coins) {
            CoinBet(desiredBet)
        } else Pass
    }

}
