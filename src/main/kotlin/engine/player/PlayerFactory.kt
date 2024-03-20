package engine.player

import BettingStrategy
import ManualBettingStrategy
import PlusOneBettingStrategy
import RandomBettingStrategy
import StandardBettingStrategy
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
        return names.mapIndexed { index, player ->
            createPlayer(index, player.name, false, player.bettingStrategy)
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

class AiPlayer(
    val name: String,
    val bettingStrategy: BettingStrategy,
)

private val aiPlayerNames = listOf(
    AiPlayer("John", PlusOneBettingStrategy()),
    AiPlayer("Debbie", RandomBettingStrategy()),
    AiPlayer("Camila", StandardBettingStrategy(1.0)),
    AiPlayer("Lucy", StandardBettingStrategy(0.95)),
    AiPlayer("Bob", StandardBettingStrategy(0.85)),
    AiPlayer("Thomas", StandardBettingStrategy(0.75)),
    AiPlayer("Diana", StandardBettingStrategy(0.65)),
    AiPlayer("Lisa", StandardBettingStrategy(0.55)),
    AiPlayer("Charlie", StandardBettingStrategy(0.50)),
    AiPlayer("Clair", StandardBettingStrategy(0.45)),
    AiPlayer("Bart", StandardBettingStrategy(0.40)),
    AiPlayer("Meghan", StandardBettingStrategy(0.35)),
    AiPlayer("Mike", StandardBettingStrategy(0.30)),
    AiPlayer("Cedric", StandardBettingStrategy(0.25)),
)
