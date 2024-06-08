import engine.CardDeck
import engine.GameSettings
import engine.player.PlayerFactory
import engine.rating.EloRatingSystem
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ui.sound.JavaXSoundPlayer
import net.solvetheriddle.cardgame.Sounds

val appModule = module {

    single<Sounds> { Sounds(JavaXSoundPlayer) }
    singleOf(::GamePrefs)
    factoryOf(::PlayerFactory)

    factory {
        val prefs = get<GamePrefs>()
        GameSettings.forDifficulty(prefs.loadGameDifficulty())
    }
    factory {
        val settings = get<GameSettings>()
        CardDeck(settings.numOfCardDecks)
    }
    factory {
        val prefs = get<GamePrefs>()
        EloRatingSystem(prefs.loadLeaderboard())
    }
    factory {
        val prefs = get<GamePrefs>()
        val playerName = prefs.loadPlayerName()
        val playerFactory = get<PlayerFactory>()
        val players = playerFactory.createPlayers(playerName)
        GameEngine(players, get(), get(), get(), get())
    }
}