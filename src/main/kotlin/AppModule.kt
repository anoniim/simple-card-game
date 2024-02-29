import engine.GameSettings
import engine.player.PlayerFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single { GameSettings.DEFAULT }
    singleOf(::PlayerFactory)
}