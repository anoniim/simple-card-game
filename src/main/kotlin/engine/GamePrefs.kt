package engine

import engine.rating.Leaderboard
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences

private const val PLAYER_NAME = "playerName"
private const val LEADERBOARD = "leaderboard"
private const val SETTINGS = "settings"

class GamePrefs {
    private val prefs: Preferences = Preferences.userRoot().node(this::class.java.name)

    fun savePlayerName(playerName: String) = prefs.put(PLAYER_NAME, playerName )

    fun loadPlayerName(): String = prefs.get(PLAYER_NAME, "")

    fun saveLeaderboard(newLeaderboard: Leaderboard) {
        prefs.put(LEADERBOARD, newLeaderboard.serialize())
    }

    fun loadLeaderboard(): Leaderboard {
        val serializedLeaderboard = prefs.get(LEADERBOARD, "")
        return Leaderboard.deserialize(serializedLeaderboard)
    }

    fun saveGameSettings(newSettings: GameSettings) {
        prefs.put(SETTINGS, Json.encodeToString(newSettings))
    }

    fun loadGameSettings(): GameSettings {
        val loadedSettings = prefs.get(SETTINGS, Json.encodeToString(GameSettings.DEFAULT))
        return Json.decodeFromString(loadedSettings)
    }
}