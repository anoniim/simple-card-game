package engine

sealed class GameState
data class ActiveGameState(
    val players: List<Player>,
    val currentRound: Round,
    val currentPlayerIndex: Int,
    val winner: Player? = null,
) : GameState() {
    fun isHumanPlayerTurn() = currentPlayerIndex == players.lastIndex

    fun placeBet(playerId: PlayerId, bet: Bet) {
        currentRound.bets[playerId] = bet
    }
}

data object MenuGameState : GameState() // TODO
data object GameOverState : GameState()