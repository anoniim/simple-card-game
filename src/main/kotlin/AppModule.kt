import engine.*
import engine.player.PlayerFactory
import engine.rating.EloRatingSystem
import engine.rating.Leaderboard
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
    factory { (playerName: String, leaderboard: Leaderboard) ->
        val playerFactory = get<PlayerFactory>()
        val players = playerFactory.createPlayers(playerName)
        val ratingSystem = EloRatingSystem(leaderboard)
        GameEngine(players, get(), get(), ratingSystem)
    }
}