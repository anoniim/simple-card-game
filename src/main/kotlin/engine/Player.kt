package engine

import STARTING_COINS
import STARTING_POINTS

class Player(
    val id: PlayerId,
    val name: String,
    var coins: Int,
    var score: Int,
) {
    companion object {
        fun createAiPlayer(aiPlayerCount: Int): List<Player> {
            val names = aiPlayerNames.shuffled().take(aiPlayerCount)
            return List(aiPlayerCount) { Player(PlayerId(it), names[it], STARTING_COINS, STARTING_POINTS) }
        }

        fun createHumanPlayer(humanPlayerId: Int, playerName: String) = Player(
            PlayerId(humanPlayerId),
            playerName, STARTING_COINS, STARTING_POINTS
        )
    }
}


@JvmInline
value class PlayerId(val value: Int)


private val aiPlayerNames = listOf(
    "John",
    "Thomas",
    "Debbie",
    "Camila",
    "Bob",
    "Lucy",
    "Diana",
    "Charlie",
    "Meghan",
    "Cedric",
    "Mike",
    "Bart",
    "Lisa",
)
