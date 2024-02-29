import engine.GameSettings
import engine.player.PlayerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

val appModule = module {
    single { GameSettings.DEFAULT }
    singleOf(::PlayerFactory)

    factory { (playerName: String) ->
        val playerFactory = get<PlayerFactory>()
        val players = playerFactory.createPlayers(playerName)
        Game(players)
    }
}