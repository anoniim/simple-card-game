package engine

import engine.rating.Leaderboard
import java.util.prefs.Preferences

private const val PLAYER_NAME = "playerName"
private const val LEADERBOARD = "leaderboard"

class GamePrefs {
    private val prefs: Preferences = Preferences.userRoot().node(this::class.java.name)

    fun setPlayerName(playerName: String) = prefs.put(PLAYER_NAME, playerName )

    fun getPlayerName(): String = prefs.get(PLAYER_NAME, "")

    fun getLeaderboard(): Leaderboard {
        val serializedLeaderboard = prefs.get(LEADERBOARD, "")
        println("serialized: $serializedLeaderboard")
        return Leaderboard.deserialize(serializedLeaderboard)
    }

    fun updateLeaderboard(newLeaderboard: Leaderboard) {
        prefs.put(LEADERBOARD, newLeaderboard.serialize())
        println("deserialized: ${newLeaderboard.serialize()}")
    }
}