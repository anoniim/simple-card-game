package engine.player

import BettingStrategy
import ManualBettingStrategy
import PlusOneBettingStrategy
import engine.GameSettings
import kotlin.random.Random

class PlayerFactory(
    private val settings: GameSettings
) {

    fun createPlayers(playerName: String): List<Player> {
        val allPlayers = createAiPlayer(settings.aiPlayerCount) + createHumanPlayer(settings.aiPlayerCount, playerName)
        return allPlayers.setFirstPlayer(getFirstPlayer())
    }

    private fun getFirstPlayer(): Int {
        return if (settings.randomizeFirstPlayer) {
            Random.nextInt(settings.aiPlayerCount + 1)
        } else 0
    }

    private fun createAiPlayer(aiPlayerCount: Int): List<Player> {
        val names = aiPlayerNames.shuffled().take(aiPlayerCount)
        return names.mapIndexed { index, name ->
            createPlayer(index, name, false, PlusOneBettingStrategy())
        }
    }

    private fun createHumanPlayer(humanPlayerId: Int, playerName: String): List<Player> {
        return listOf(createPlayer(humanPlayerId, playerName, true, ManualBettingStrategy()))
    }

    private fun createPlayer(index: Int, name: String, isHuman: Boolean, bettingStrategy: BettingStrategy) = Player(
        PlayerId(index),
        name,
        isHuman = isHuman,
        settings.startingCoins,
        settings.startingPoints,
        bettingStrategy = bettingStrategy,
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
