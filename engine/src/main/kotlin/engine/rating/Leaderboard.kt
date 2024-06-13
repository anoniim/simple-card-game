package engine.rating

const val DEFAULT_RATING = 1000.0

private const val NAME_DELIMITER = ":"
private const val PLAYER_DELIMITER = ";;"
private const val STAT_DELIMITER = ";"

class Leaderboard(
    initialStats: Map<String, PlayerStats>
) {

    private val stats: MutableMap<String, PlayerStats> = initialStats.toMutableMap()

    fun getPlayerRating(playerName: String) = stats[playerName]?.rating ?: DEFAULT_RATING

    fun getDisplayRows(): List<DisplayRow> {
        return stats.map {
            val winRatio = it.value.totalWins.toDouble() / it.value.totalGames.toDouble()
            val formattedWinRatio = (winRatio * 100).toInt()
            DisplayRow(it.key, it.value.rating.toInt(), it.value.totalGames, formattedWinRatio)
        }.sortedBy { it.rating }
            .reversed()
    }

    fun serialize(): String {
        return buildString {
            stats.forEach {
                append(it.key)
                append(NAME_DELIMITER)
                append(it.value.serialize())
                append(PLAYER_DELIMITER)
            }
        }
    }

    fun updateLoserRating(loserName: String, loserRatingDelta: Double) {
        println("Loser's rating update: $loserName $loserRatingDelta")
        val currentStats = playerStats(loserName)
        val updatedStats = currentStats.copy(
            rating = currentStats.rating + loserRatingDelta,
            totalGames = currentStats.totalGames + 1)
        stats[loserName] = updatedStats
    }

    fun updateWinnerRating(winnerName: String, winnerRatingDelta: Double) {
        println("Winner's rating update: $winnerName $winnerRatingDelta")
        val currentStats = playerStats(winnerName)
        val updatedStats = currentStats.copy(
            rating = currentStats.rating + winnerRatingDelta,
            totalGames = currentStats.totalGames + 1,
            totalWins = currentStats.totalWins + 1)
        stats[winnerName] = updatedStats
    }

    fun updateRating(player: String, playerRatingDelta: Double) {
        println("Rating update: $player $playerRatingDelta")
        val currentStats = playerStats(player)
        val updatedStats = currentStats.copy(rating = currentStats.rating + playerRatingDelta)
        stats[player] = updatedStats
    }

    private fun playerStats(playerName: String) = stats[playerName] ?: defaultPlayerStats()

    private fun defaultPlayerStats() = PlayerStats(
        rating = DEFAULT_RATING,
        totalGames = 0,
        totalWins = 0
    )

    companion object {
        fun deserialize(serializedLeaderboard: String): Leaderboard {
            println("deserializing $serializedLeaderboard")
            if (serializedLeaderboard.isEmpty()) return Leaderboard(emptyMap())

            val segmentsPlusOne = serializedLeaderboard.split(PLAYER_DELIMITER)
            val segments = segmentsPlusOne.take(segmentsPlusOne.size - 1)
            val playerStats = buildMap {
                segments.forEach { playerSegment ->
                    val nameStats = playerSegment.split(NAME_DELIMITER)
                    put(nameStats[0], PlayerStats.deserialize(nameStats[1]))
                }
            }
            return Leaderboard(playerStats)
        }
    }
}

data class PlayerStats(
    val rating: Double,
    val totalGames: Int,
    val totalWins: Int,
) {
    fun serialize(): String {
        return buildString {
            append(rating)
            append(STAT_DELIMITER)
            append(totalGames)
            append(STAT_DELIMITER)
            append(totalWins)
        }
    }

    companion object {
        fun deserialize(serializedPlayerStats: String): PlayerStats {
            val segments = serializedPlayerStats.split(STAT_DELIMITER)
            return PlayerStats(
                rating = segments[0].toDouble(),
                totalGames = segments[1].toInt(),
                totalWins = segments[2].toInt())
        }
    }
}

data class DisplayRow(
    val name: String,
    val rating: Int,
    val games: Int,
    val winRatio: Int
)