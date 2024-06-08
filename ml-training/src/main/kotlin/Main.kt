package net.solvetheriddle.cardgame

import GameEngine
import engine.CardDeck
import engine.GameDifficulty
import engine.GameSettings
import engine.player.PlayerFactory
import engine.rating.EloRatingSystem
import engine.rating.Leaderboard
import mocks.NoOpSoundPlayer

fun main() {
    println("Let's do some ML training!")
    val game = createNewGameEngine("CP1")
    game.startGame()
}

private fun createNewGameEngine(modelName: String): GameEngine {
    val settings = GameSettings.forDifficulty(GameDifficulty.EASY)
    val cardDeck = CardDeck(settings.numOfCardDecks)
    val ratingSystem = EloRatingSystem(Leaderboard(emptyMap()))
    val sounds = Sounds(NoOpSoundPlayer())
    val players = PlayerFactory(settings).createPlayers(modelName)
    return GameEngine(players, cardDeck, settings, ratingSystem, sounds)
}