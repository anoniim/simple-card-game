import engine.GameDifficulty
import engine.rating.Leaderboard
import java.util.prefs.Preferences

private const val PREFS_NAME = "net.solvetheriddle.bidtowin"
private const val PLAYER_NAME = "playerName"
private const val LEADERBOARD = "leaderboard"
private const val DIFFICULTY = "difficulty"

class GamePrefs {
    private val prefs: Preferences = Preferences.userRoot().node(PREFS_NAME) //.also { it.run { clear() } }

    fun savePlayerName(playerName: String) {
        prefs.put(PLAYER_NAME, playerName)
        prefs.flush()
    }

    fun loadPlayerName(): String = prefs.get(PLAYER_NAME, "")

    fun saveLeaderboard(newLeaderboard: Leaderboard) {
        prefs.put(LEADERBOARD, newLeaderboard.serialize())
        prefs.flush()
    }

    fun loadLeaderboard(): Leaderboard {
        val serializedLeaderboard = prefs.get(LEADERBOARD, "")
        return Leaderboard.deserialize(serializedLeaderboard)
    }

    fun saveGameDifficulty(newDifficulty: GameDifficulty) {
        prefs.putInt(DIFFICULTY, newDifficulty.ordinal)
        prefs.flush()
    }

    fun loadGameDifficulty(): GameDifficulty {
        val loadedDifficultyIndex = prefs.getInt(DIFFICULTY, GameDifficulty.MEDIUM.ordinal)
        return GameDifficulty.entries[loadedDifficultyIndex]
    }
}