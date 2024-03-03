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
        return names.mapIndexed { index, name -> createPlayer(index, name, false) }
    }

    private fun createHumanPlayer(humanPlayerId: Int, playerName: String): List<Player> {
        return listOf(createPlayer(humanPlayerId, playerName, true))
    }

    private fun createPlayer(index: Int, name: String, isHuman: Boolean) = Player(
        PlayerId(index),
        name,
        isHuman = isHuman,
        settings.startingCoins,
        settings.startingPoints,
        null,
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
