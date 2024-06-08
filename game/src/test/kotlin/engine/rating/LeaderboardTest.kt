package engine.rating

import kotlin.test.Test
import kotlin.test.assertEquals

private const val PLAYER_ONE_NAME = "PLAYER_ONE"
private val PLAYER_ONE_STATS = PlayerStats(1000.0, 11, 10)
private val PLAYER_ONE = PLAYER_ONE_NAME to PLAYER_ONE_STATS

private const val PLAYER_TWO_NAME = "PLAYER-TWO"
private val PLAYER_TWO_STATS = PlayerStats(678.9, 10, 5)
private val PLAYER_TWO = PLAYER_TWO_NAME to PLAYER_TWO_STATS

private const val PLAYER_THREE_NAME = "PLAYER-THREE"
private val PLAYER_THREE_STATS = PlayerStats(-1.9, 1, 1)
private val PLAYER_THREE = PLAYER_THREE_NAME to PLAYER_THREE_STATS

class LeaderboardTest {

    @Test
    fun `serialize empty leaderboard`() {
        val leaderboard = Leaderboard(emptyMap())

        val result = leaderboard.serialize()

        assertEquals("", result)
    }

    @Test
    fun `serialize leaderboard with one player`() {
        val leaderboard = Leaderboard(mapOf(
            PLAYER_ONE,
        ))

        val result = leaderboard.serialize()

        assertEquals("PLAYER_ONE:1000.0;11;10;;", result)
    }

    @Test
    fun `serialize leaderboard with two players`() {
        val leaderboard = Leaderboard(mapOf(
            PLAYER_ONE,
            PLAYER_TWO,
        ))

        val result = leaderboard.serialize()

        assertEquals("PLAYER_ONE:1000.0;11;10;;PLAYER-TWO:678.9;10;5;;", result)
    }

    @Test
    fun `serialize player stats with negative rating `() {
        val result = PLAYER_THREE_STATS.serialize()

        assertEquals("-1.9;1;1", result)
    }
}