package engine

import kotlinx.serialization.Serializable

@Suppress("PLUGIN_IS_NOT_ENABLED") // Plugin is enabled in the main module where this class is actually serialized
@Serializable
data class GameSettings(
    val startingCoins: Int,
    val startingPoints: Int,
    val numOfCardDecks: Int,
    val aiPlayerCount: Int,
    val gameDifficulty: GameDifficulty,
    val goalScore: Int,
    val randomizeFirstPlayer: Boolean,
) {
    companion object {

        fun forDifficulty(difficulty: GameDifficulty): GameSettings {
            return when (difficulty) {
                GameDifficulty.EASY -> EASY
                GameDifficulty.MEDIUM -> DEFAULT
                GameDifficulty.HARD -> HARD
            }
        }

        private val EASY = GameSettings(
            startingCoins = 10,
            startingPoints = 0,
            numOfCardDecks = 1,
            aiPlayerCount = 3,
            gameDifficulty = GameDifficulty.EASY,
            goalScore = 30,
            randomizeFirstPlayer = false,
        )

        private val DEFAULT = GameSettings(
            startingCoins = 10,
            startingPoints = 0,
            numOfCardDecks = 2,
            aiPlayerCount = 3,
            gameDifficulty = GameDifficulty.MEDIUM,
            goalScore = 30,
            randomizeFirstPlayer = false,
        )

        private val HARD = GameSettings(
            startingCoins = 15,
            startingPoints = 0,
            numOfCardDecks = 3,
            aiPlayerCount = 3,
            gameDifficulty = GameDifficulty.HARD,
            goalScore = 40,
            randomizeFirstPlayer = false, // FIXME: Set to true once fixed
        )
    }
}

enum class GameDifficulty {
    EASY,
    MEDIUM,
    HARD,
}