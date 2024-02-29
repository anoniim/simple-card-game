package engine.player

import engine.GameSettings

class PlayerFactory(
    private val settings: GameSettings
) {

    fun createPlayers(playerName: String): List<Player> {
        return createAiPlayer(settings.aiPlayerCount) +
                createHumanPlayer(settings.aiPlayerCount, playerName)
    }

    private fun createAiPlayer(aiPlayerCount: Int): List<Player> {
        val names = aiPlayerNames.shuffled().take(aiPlayerCount)
        return List(aiPlayerCount) {
            Player(
                PlayerId(it),
                names[it],
                settings.startingCoins,
                settings.startingPoints
            )
        }
    }

    private fun createHumanPlayer(humanPlayerId: Int, playerName: String) = Player(
        PlayerId(humanPlayerId),
        playerName,
        settings.startingCoins,
        settings.startingPoints
    )
}

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
