package engine

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val startingCoins: Int,
    val startingPoints: Int,
    val numOfCardDecks: Int,
    val aiPlayerCount: Int,
    val aiPlayerDifficulty: AiPlayerDifficulty,
    val goalScore: Int,
    val randomizeFirstPlayer: Boolean,
) {
    companion object {
        val EASY = GameSettings(
            startingCoins = 10,
            startingPoints = 0,
            numOfCardDecks = 1,
            aiPlayerCount = 3,
            aiPlayerDifficulty = AiPlayerDifficulty.EASY,
            goalScore = 30,
            randomizeFirstPlayer = false,
        )

        val DEFAULT = GameSettings(
            startingCoins = 10,
            startingPoints = 0,
            numOfCardDecks = 2,
            aiPlayerCount = 3,
            aiPlayerDifficulty = AiPlayerDifficulty.MEDIUM,
            goalScore = 30,
            randomizeFirstPlayer = false,
        )

        val HARD = GameSettings(
            startingCoins = 15,
            startingPoints = 0,
            numOfCardDecks = 3,
            aiPlayerCount = 3,
            aiPlayerDifficulty = AiPlayerDifficulty.HARD,
            goalScore = 40,
            randomizeFirstPlayer = false, // FIXME: Set to true once fixed
        )
    }
}

enum class AiPlayerDifficulty {
    EASY,
    MEDIUM,
    HARD,
}