package engine

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val startingCoins: Int,
    val startingPoints: Int,
    val numOfCardDecks: Int,
    val aiPlayerCount: Int,
    val goalScore: Int,
    val randomizeFirstPlayer: Boolean,
) {

    companion object {
        val DEFAULT = GameSettings(
            startingCoins = 10,
            startingPoints = 0,
            numOfCardDecks = 2,
            aiPlayerCount = 3,
            goalScore = 30,
            randomizeFirstPlayer = false,
        )
    }
}