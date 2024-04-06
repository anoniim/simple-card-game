import engine.*
import engine.player.PlayerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::GamePrefs)
    single { GameSettings.DEFAULT }
    singleOf(::PlayerFactory)

    factory {
        val settings = get<GameSettings>()
        CardDeck(settings.numOfCardDecks)
    }
    factory { (playerName: String) ->
        val playerFactory = get<PlayerFactory>()
        val players = playerFactory.createPlayers(playerName)
        GameEngine(players, get(), get())
    }
}