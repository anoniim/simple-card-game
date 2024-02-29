package engine.player


class Player(
    val id: PlayerId,
    val name: String,
    var coins: Int,
    var score: Int,
)

@JvmInline
value class PlayerId(val value: Int)

