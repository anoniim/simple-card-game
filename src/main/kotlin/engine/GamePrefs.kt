package engine

import java.util.prefs.Preferences

private const val PLAYER_NAME = "playerName"

class GamePrefs {
    private val prefs: Preferences = Preferences.userRoot().node(this::class.java.name)

    fun setPlayerName(playerName: String) = prefs.put(PLAYER_NAME, playerName )

    fun getPlayerName(): String = prefs.get(PLAYER_NAME, "")
}