package engine

import Player
import Round

sealed class GameState
data class ActiveGameState(
    val players: List<Player>,
    val currentRound: Round,
    val currentPlayerIndex: Int,
    val winner: Player? = null,
) : GameState()

data object MenuGameState : GameState() // TODO
data object GameOverState : GameState()