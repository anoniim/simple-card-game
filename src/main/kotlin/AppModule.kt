import engine.CardDeck
import engine.GamePrefs
import engine.GameSettings
import engine.player.PlayerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::GamePrefs)
    single { GameSettings.DEFAULT }
    singleOf(::PlayerFactory)
    singleOf(::BetGenerator)

    factory {
        val settings = get<GameSettings>()
        CardDeck(settings.numOfCardDecks)
    }
    factory { (playerName: String) ->
        val playerFactory = get<PlayerFactory>()
        val players = playerFactory.createPlayers(playerName)
        Game(players, get(), get(), get())
    }
}