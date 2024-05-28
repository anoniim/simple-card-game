package engine.player

import engine.*
import kotlin.random.Random

class PlayerFactory(
    private val settings: GameSettings
) {

    private val bettingStrategy = BettingStrategyFactory(settings)

    private val john = AiPlayer("John", bettingStrategy.plusOne())
    private val lilly = AiPlayer("Lilly", bettingStrategy.plusOne())

    private val wacko = AiPlayer("Wacko", bettingStrategy.totalRandom())
    private val debbie = AiPlayer("Debbie", bettingStrategy.random())
    private val millie = AiPlayer("Millie", bettingStrategy.highestRandomPlusOne())
    private val darren = AiPlayer("Darren", bettingStrategy.reasonableRandomPlusOne())
    private val tony = AiPlayer("Tony", bettingStrategy.conservativeRandomPlusOne())

    private val lucy = AiPlayer("Lucy", bettingStrategy.standard(1.2))
    private val camila = AiPlayer("Camila", bettingStrategy.standard(1.0))
    private val thomas = AiPlayer("Thomas", bettingStrategy.standard(0.9))
    private val lisa = AiPlayer("Lisa", bettingStrategy.standard(0.8))
    private val josh = AiPlayer("Josh", bettingStrategy.standard(0.7))

    private val meghan = AiPlayer("Meghan", bettingStrategy.highStandard(5, 1.0))
    private val cedric = AiPlayer("Cedric", bettingStrategy.highStandard(5, 0.9))
    private val diana = AiPlayer("Diana", bettingStrategy.highStandard(5, 0.8))
    private val charlie = AiPlayer("Charlie", bettingStrategy.highStandard(5, 0.7))

    private val paul = AiPlayer("Paul", bettingStrategy.highStandard(8, 1.0))
    private val bob = AiPlayer("Bob", bettingStrategy.highStandard(8, 0.9))
    private val bart = AiPlayer("Bart", bettingStrategy.highStandard(8, 0.8))
    private val mike = AiPlayer("Mike", bettingStrategy.highStandard(8, 0.7))

    private val aiPlayersEasy = listOf(
        wacko,
        debbie,
        john,
        millie,
        darren,
        tony,
        lucy,
    )

    private val aiPlayersMedium = listOf(
        john,
        lilly,
        lucy,
        camila,
        thomas,
        lisa,
        josh,
        meghan,
    )

    private val aiPlayersHard = listOf(
        oneOf(
            lucy,
            camila,
            thomas,
            lisa,
            josh,
        ),
        oneOf(
            meghan,
            cedric,
            diana,
            charlie,
        ),
        oneOf(
            paul,
            bob,
            bart,
            mike,
        )
    )

    private fun oneOf(vararg players: AiPlayer): AiPlayer {
        return players.random()
    }


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
        val names = aiPlayersEasy.shuffled().take(aiPlayerCount)
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

