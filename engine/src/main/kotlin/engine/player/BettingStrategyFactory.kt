package engine.player

import engine.GameSettings

class BettingStrategyFactory(
    private val settings: GameSettings
) {
    fun totalRandom() = TotalRandomBettingStrategy()
    fun random() = RandomBettingStrategy(settings.goalScore)
    fun plusOne() = PlusOneBettingStrategy(settings.goalScore)
    fun highestRandomPlusOne() = HighestRandomPlusOneBettingStrategy(settings.goalScore)
    fun reasonableRandomPlusOne() = ReasonableRandomPlusOneBettingStrategy(settings.goalScore)
    fun conservativeRandomPlusOne() = ConservativeRandomPlusOneBettingStrategy(settings.goalScore)
    fun standard(takeFactor: Double) = StandardBettingStrategy(takeFactor, settings.goalScore)
    fun highStandard(minCardValue: Int, takeFactor: Double) = HighStandardBettingStrategy(minCardValue, takeFactor, settings.goalScore)
}
