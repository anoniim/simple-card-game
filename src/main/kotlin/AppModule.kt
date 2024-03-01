import engine.GameSettings
import engine.player.PlayerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import engine.GamePrefs

val appModule = module {
    singleOf(::GamePrefs)
    single { GameSettings.DEFAULT }
    singleOf(::PlayerFactory)

    factory { (playerName: String) ->
        val playerFactory = get<PlayerFactory>()
        val players = playerFactory.createPlayers(playerName)
        Game(players)
    }
}