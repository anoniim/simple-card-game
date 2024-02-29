package engine

const val STARTING_COINS = 10
const val STARTING_POINTS = 0
const val NUM_OF_CARD_DECKS = 1
const val AI_PLAYER_COUNT = 3
const val GOAL_SCORE = 30

class GameSettings(
    val startingCoins: Int,
    val startingPoints: Int,
    val numOfCardDecks: Int,
    val aiPlayerCount: Int,
    val goalScore: Int,
) {
    companion object {
        val DEFAULT = GameSettings(
            STARTING_COINS,
            STARTING_POINTS,
            NUM_OF_CARD_DECKS,
            AI_PLAYER_COUNT,
            GOAL_SCORE
        )
    }
}
