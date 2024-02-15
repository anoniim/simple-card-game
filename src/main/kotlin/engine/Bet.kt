package engine

sealed class Bet
class CoinBet(val coins: Int) : Bet()
data object Pass : Bet()