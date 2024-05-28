import engine.CardDeck
import engine.GamePrefs
import engine.GameSettings
import engine.player.PlayerFactory
import engine.rating.EloRatingSystem
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    singleOf(::GamePrefs)
    factoryOf(::PlayerFactory)

    factory {
        val prefs = get<GamePrefs>()
        prefs.loadGameSettings()
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
        GameEngine(players, get(), get(), get())
    }
}