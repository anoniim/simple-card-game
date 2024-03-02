package engine.player

import engine.GameSettings

class PlayerFactory(
    private val settings: GameSettings
) {

    fun createPlayers(playerName: String): Map<PlayerId, Player> {
        return createAiPlayer(settings.aiPlayerCount) +
                createHumanPlayer(settings.aiPlayerCount, playerName)
    }

    private fun createAiPlayer(aiPlayerCount: Int): Map<PlayerId, Player> {
        val names = aiPlayerNames.shuffled().take(aiPlayerCount)
        return names.mapIndexed { index, name ->
            val playerId = index + 1
            PlayerId(playerId) to Player(
                PlayerId(playerId),
                name,
                isHuman = false,
            )
        }.toMap()
    }

    private fun createHumanPlayer(humanPlayerId: Int, playerName: String): Map<PlayerId, Player> {
        return mapOf(
            PlayerId(humanPlayerId) to Player(
                PlayerId(humanPlayerId),
                playerName,
                isHuman = true,
            )
        )
    }
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
