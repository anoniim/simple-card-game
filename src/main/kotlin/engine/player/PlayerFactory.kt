package engine.player

import engine.*
import kotlin.random.Random

class PlayerFactory(
    private val settings: GameSettings
) {

    val bettingStrategyFactory = BettingStrategyFactory(settings)

    private val aiPlayers = listOf(
        AiPlayer("Wacko", TotalRandomBettingStrategy()),
        AiPlayer("Debbie", RandomBettingStrategy(settings.goalScore)),
        AiPlayer("John", PlusOneBettingStrategy(settings.goalScore)),
        AiPlayer("Milly", HighestRandomPlusOneBettingStrategy(settings.goalScore)),
        AiPlayer("Darren", ReasonableRandomPlusOneBettingStrategy(settings.goalScore)),
        AiPlayer("Tony", ConservativeRandomPlusOneBettingStrategy(settings.goalScore)),
        AiPlayer("Lucy", StandardBettingStrategy(1.2, settings.goalScore)),
        AiPlayer("Camila", StandardBettingStrategy(1.0, settings.goalScore)),
        AiPlayer("Thomas", StandardBettingStrategy(0.9, settings.goalScore)),
        AiPlayer("Lisa", StandardBettingStrategy(0.8, settings.goalScore)),
        AiPlayer("Clair", StandardBettingStrategy(0.7, settings.goalScore)),
//        AiPlayer("Meghan", HighStandardBettingStrategy(5, 1.0, settings.goalScore)),
//        AiPlayer("Cedric", HighStandardBettingStrategy(5, 0.9, settings.goalScore)),
//        AiPlayer("Diana", HighStandardBettingStrategy(5, 0.8, settings.goalScore)),
//        AiPlayer("Charlie", HighStandardBettingStrategy(5, 0.7, settings.goalScore)),
//        AiPlayer("Paul", HighStandardBettingStrategy(8,1.0, settings.goalScore)),
//        AiPlayer("Bob", HighStandardBettingStrategy(8,0.9, settings.goalScore)),
//        AiPlayer("Bart", HighStandardBettingStrategy(8,0.8, settings.goalScore)),
//        AiPlayer("Mike", HighStandardBettingStrategy(8,0.7, settings.goalScore)),
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

