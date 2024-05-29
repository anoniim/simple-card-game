package engine.rating

import engine.player.Player
import kotlin.math.pow

class EloRatingSystem(
    private val leaderboard: Leaderboard
) {

    private val winnerKFactor = 10
    private val loserKFactor = 3 * winnerKFactor
    private val exitPenalty = -10.0

    fun updateRatings(players: List<Player>, winner: Player): Leaderboard {
        var winnerRatingTotalDelta = 0.0
        val winnerRating = winner.getRating()
        for (player in players) {
            if (player != winner) {
                val loserRating = player.getRating()
                val (winnerRatingDelta, loserRatingDelta) = calculateNewRatings(winnerRating, loserRating)
                leaderboard.updateLoserRating(player.name, loserRatingDelta)
                winnerRatingTotalDelta += winnerRatingDelta
            }
        }
        leaderboard.updateWinnerRating(winner.name, winnerRatingTotalDelta)
        return leaderboard
    }

    private fun calculateNewRatings(winnerRating: Double, loserRating: Double): RatingDelta {
        val winnerWinProbability = calculateWinProbability(winnerRating, loserRating)
        val loserWinProbability = calculateWinProbability(loserRating, winnerRating)

        val winnerRatingDelta = winnerKFactor * (1 - winnerWinProbability)
        val loserRatingDelta = loserKFactor * (0 - loserWinProbability)

        return RatingDelta(winnerRatingDelta, loserRatingDelta)
    }

    private fun calculateWinProbability(playerRating: Double, opponentRating: Double): Double {
        return 1.0 / (1 + 10.0.pow((opponentRating - playerRating) / 400))
    }

    private fun Player.getRating() = leaderboard.getPlayerRating(name)

    fun penalizeExit(humanPlayer: Player?): Leaderboard {
        if (humanPlayer == null) throw IllegalStateException("Human player not found, WTF?!")
        leaderboard.updateLoserRating(humanPlayer.name, exitPenalty)
        return leaderboard
    }
}

private typealias RatingDelta = Pair<Double, Double> // <winnerDelta, loserDelta>
