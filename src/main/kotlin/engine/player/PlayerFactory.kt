package engine.player

import engine.BettingStrategy
import engine.ManualBettingStrategy
import engine.PlusOneBettingStrategy
import engine.RandomBettingStrategy
import engine.StandardBettingStrategy
import engine.GameSettings
import kotlin.random.Random

class PlayerFactory(
    private val settings: GameSettings
) {

    private val aiPlayers = listOf(
        AiPlayer("John", PlusOneBettingStrategy(settings.goalScore)),
        AiPlayer("Paul", PlusOneBettingStrategy(settings.goalScore)),
        AiPlayer("Debbie", RandomBettingStrategy(settings.goalScore)),
        AiPlayer("Camila", StandardBettingStrategy(1.0, settings.goalScore)),
        AiPlayer("Lucy", StandardBettingStrategy(0.95, settings.goalScore)),
        AiPlayer("Bob", StandardBettingStrategy(0.85, settings.goalScore)),
        AiPlayer("Thomas", StandardBettingStrategy(0.75, settings.goalScore)),
        AiPlayer("Diana", StandardBettingStrategy(0.65, settings.goalScore)),
        AiPlayer("Lisa", StandardBettingStrategy(0.55, settings.goalScore)),
        AiPlayer("Charlie", StandardBettingStrategy(0.50, settings.goalScore)),
        AiPlayer("Clair", StandardBettingStrategy(0.45, settings.goalScore)),
        AiPlayer("Bart", StandardBettingStrategy(0.40, settings.goalScore)),
        AiPlayer("Meghan", StandardBettingStrategy(0.35, settings.goalScore)),
        AiPlayer("Mike", StandardBettingStrategy(0.30, settings.goalScore)),
        AiPlayer("Cedric", StandardBettingStrategy(0.25, settings.goalScore)),
    )


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
        val names = aiPlayers.shuffled().take(aiPlayerCount)
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

