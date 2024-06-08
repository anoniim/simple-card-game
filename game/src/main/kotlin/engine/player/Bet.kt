package engine.player

sealed class Bet
data class CoinBet(val coins: Int) : Bet()
data object Pass : Bet()