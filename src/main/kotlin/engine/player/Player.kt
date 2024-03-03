package engine.player


class Player(
    val id: PlayerId,
    val name: String,
    val isHuman: Boolean,
)

@JvmInline
value class PlayerId(val value: Int)

operator fun List<Player>.get(playerId: PlayerId) = get(playerId.value)
